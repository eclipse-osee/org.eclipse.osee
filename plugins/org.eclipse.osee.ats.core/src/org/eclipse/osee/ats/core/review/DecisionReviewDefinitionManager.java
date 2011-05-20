/*
 * Created on Jun 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.transition.TransitionAdapter;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * Create DecisionReview from transition if defined by StateDefinition.
 * 
 * @author Donald G. Dunne
 */
public class DecisionReviewDefinitionManager extends TransitionAdapter {

   /**
    * Creates decision review if one of same name doesn't already exist
    */
   public static DecisionReviewArtifact createNewDecisionReview(DecisionReviewDefinition revDef, SkynetTransaction transaction, TeamWorkFlowArtifact teamArt, Date createdDate, User createdBy) throws OseeCoreException {
      if (Artifacts.getNames(ReviewManager.getReviews(teamArt)).contains(revDef.getTitle())) {
         // Already created this review
         return null;
      }
      // Add current user if no valid users specified
      Set<IBasicUser> users = new HashSet<IBasicUser>();
      users.addAll(UserManager.getUsersByUserId(revDef.getAssignees()));
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      if (!Strings.isValid(revDef.getTitle())) {
         throw new OseeStateException("ReviewDefinition must specify title for Team Workflow [%s] WorkDefinition [%s]",
            teamArt.toStringWithId(), teamArt.getWorkDefinition());
      }
      DecisionReviewArtifact decArt = null;
      if (revDef.isAutoTransitionToDecision()) {
         decArt =
            DecisionReviewManager.createNewDecisionReviewAndTransitionToDecision(teamArt, revDef.getTitle(),
               revDef.getDescription(), revDef.getRelatedToState(), revDef.getBlockingType(), revDef.getOptions(),
               users, createdDate, createdBy, transaction);
      } else {
         decArt =
            DecisionReviewManager.createNewDecisionReview(teamArt, revDef.getBlockingType(), revDef.getTitle(),
               revDef.getRelatedToState(), revDef.getDescription(), revDef.getOptions(), users, createdDate, createdBy);
      }
      decArt.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", revDef.getName()));
      decArt.persist(transaction);
      return decArt;
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      // Create any decision or peerToPeer reviews for transitionTo and transitionFrom
      if (!sma.isTeamWorkflow()) {
         return;
      }
      Date createdDate = new Date();
      User createdBy = UserManager.getUser(SystemUser.OseeSystem);
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) sma;

      for (DecisionReviewDefinition decRevDef : teamArt.getStateDefinition().getDecisionReviews()) {
         if (decRevDef.getStateEventType() != null && decRevDef.getStateEventType().equals(StateEventType.TransitionTo)) {
            DecisionReviewArtifact decArt =
               DecisionReviewDefinitionManager.createNewDecisionReview(decRevDef, transaction, teamArt, createdDate,
                  createdBy);
            if (decArt != null) {
               decArt.persist(transaction);
            }
         }
      }
   }
}
