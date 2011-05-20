/*
 * Created on Jun 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.log.LogType;
import org.eclipse.osee.ats.core.workflow.transition.TransitionAdapter;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * Create PeerToPeer Review from transition if defined by StateDefinition.
 * 
 * @author Donald G. Dunne
 */
public class PeerReviewDefinitionManager extends TransitionAdapter {

   /**
    * Creates PeerToPeer review if one of same name doesn't already exist
    */
   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(PeerReviewDefinition peerRevDef, SkynetTransaction transaction, TeamWorkFlowArtifact teamArt, Date createdDate, User createdBy) throws OseeCoreException {
      String title = peerRevDef.getTitle();
      if (!Strings.isValid(title)) {
         title = String.format("Review [%s]", teamArt.getName());
      }
      if (Artifacts.getNames(ReviewManager.getReviews(teamArt)).contains(title)) {
         // Already created this review
         return null;
      }
      PeerToPeerReviewArtifact peerArt =
         PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt, title, peerRevDef.getRelatedToState(), createdDate,
            createdBy, transaction);
      if (Strings.isValid(peerRevDef.getDescription())) {
         peerArt.setSoleAttributeFromString(AtsAttributeTypes.Description, peerRevDef.getDescription());
      }
      ReviewBlockType reviewBlockType = peerRevDef.getBlockingType();
      if (reviewBlockType != null) {
         peerArt.setSoleAttributeFromString(AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
      }
      if (Strings.isValid(peerRevDef.getLocation())) {
         peerArt.setSoleAttributeFromString(AtsAttributeTypes.Location, peerRevDef.getLocation());
      }
      Collection<IBasicUser> assignees = UserManager.getUsersByUserId(peerRevDef.getAssignees());
      if (assignees.size() > 0) {
         peerArt.getStateMgr().setAssignees(assignees);
      }
      peerArt.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", peerRevDef.getName()));
      return peerArt;
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

      for (PeerReviewDefinition peerRevDef : teamArt.getStateDefinition().getPeerReviews()) {
         if (peerRevDef.getStateEventType() != null && peerRevDef.getStateEventType().equals(
            StateEventType.TransitionTo)) {
            PeerToPeerReviewArtifact decArt =
               createNewPeerToPeerReview(peerRevDef, transaction, teamArt, createdDate, createdBy);
            if (decArt != null) {
               decArt.persist(transaction);
            }
         }
      }
   }

}
