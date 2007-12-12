/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewDecisionReviewJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class ReviewManager {

   private final SMAManager smaMgr;
   private static String VALIDATE_REVIEW_TITLE = "Is the resolution of this Action valid?";

   public ReviewManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   /**
    * Create a new decision review configured and transitioned to handle action validation
    * 
    * @param force will force the creation of the review without checking that a review should be created
    * @return new review
    * @throws SQLException
    */
   public DecisionReviewArtifact createValidateReview(boolean force) throws SQLException {
      // If not validate page, don't do anything
      if (!force && !smaMgr.getWorkPage().isValidatePage()) return null;
      // If validate review already created for this state, return
      if (!force && getReviewsFromCurrentState().size() > 0) {
         for (ReviewSMArtifact rev : getReviewsFromCurrentState()) {
            if (rev.getDescriptiveName().equals(VALIDATE_REVIEW_TITLE)) return null;
         }
      }
      // Create validate review
      try {

         DecisionReviewArtifact decRev = NewDecisionReviewJob.createNewDecisionReview(smaMgr.getSma(), true);
         decRev.setDescriptiveName(VALIDATE_REVIEW_TITLE);
         decRev.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
         decRev.setSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(),
               "No;Followup;" + getValidateReviewFollowupUsersStr() + "\n" + "Yes;Completed;");
         decRev.setSoleBooleanAttributeValue(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getStoreName(),
               smaMgr.getWorkPage().isValidateReviewBlocking());

         SMAManager revSmaMgr = new SMAManager(decRev);
         revSmaMgr.transition(DecisionReviewArtifact.StateNames.Decision.name(), smaMgr.getOriginator(), true);

         return decRev;

      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return null;
   }

   public PeerToPeerReviewArtifact createNewPeerToPeerReview(String againstState) throws SQLException {
      return createNewPeerToPeerReview(againstState, SkynetAuthentication.getInstance().getAuthenticatedUser(),
            new Date());
   }

   public PeerToPeerReviewArtifact createNewPeerToPeerReview(String againstState, User origUser, Date origDate) throws SQLException {
      return createNewPeerToPeerReview(smaMgr.getSma(), againstState, origUser, origDate);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(StateMachineArtifact teamParent, String againstState, User origUser, Date origDate) throws SQLException {
      PeerToPeerReviewArtifact peerToPeerRev =
            (PeerToPeerReviewArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  PeerToPeerReviewArtifact.ARTIFACT_NAME, BranchPersistenceManager.getInstance().getAtsBranch()).makeNewArtifact();

      if (teamParent != null) {
         teamParent.relate(RelationSide.TeamWorkflowToReview_Review, peerToPeerRev);
         if (againstState != null) peerToPeerRev.setSoleAttributeValue(
               ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), againstState);
      }

      peerToPeerRev.getLog().addLog(LogType.Originated, "", "", origDate, origUser);
      peerToPeerRev.setDescriptiveName("Peer to Peer Review");
      peerToPeerRev.setSoleBooleanAttributeValue(ATSAttributes.BLOCKING_REVIEW_ATTRIBUTE.getStoreName(), false);

      // Set state
      // Set current state and POCs
      peerToPeerRev.getCurrentStateDam().setState(
            new SMAState(DecisionReviewArtifact.StateNames.Prepare.name(),
                  SkynetAuthentication.getInstance().getAuthenticatedUser()));
      peerToPeerRev.getLog().addLog(LogType.StateEntered, DecisionReviewArtifact.StateNames.Prepare.name(), "",
            origDate, origUser);
      peerToPeerRev.persist(true);
      return peerToPeerRev;
   }

   public String getValidateReviewFollowupUsersStr() {
      try {
         return SMAState.getAssigneesStorageString(getValidateReviewFollowupUsers());
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
      }
   }

   public Collection<User> getValidateReviewFollowupUsers() throws SQLException {
      SMAState state = smaMgr.getSMAState("Implement", false);
      // Try to find an Implement state and it's assignees
      if (state != null && state.getAssignees().size() > 0) return state.getAssignees();
      try {
         // Else if Team Workflow , return it to the leads of this team
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getLeads();
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      // Else, return current user; should never hit this
      return Arrays.asList(new User[] {SkynetAuthentication.getInstance().getAuthenticatedUser()});
   }

   public Collection<ReviewSMArtifact> getReviews() throws SQLException {
      return smaMgr.getSma().getArtifacts(RelationSide.TeamWorkflowToReview_Review, ReviewSMArtifact.class);
   }

   public Collection<ReviewSMArtifact> getReviewsFromCurrentState() throws SQLException {
      return getReviews(smaMgr.getSma().getCurrentStateName());
   }

   public Collection<ReviewSMArtifact> getReviews(String stateName) throws SQLException {
      Set<ReviewSMArtifact> arts = new HashSet<ReviewSMArtifact>();
      if (!smaMgr.getSma().isTaskable()) return arts;
      for (ReviewSMArtifact revArt : getReviews()) {
         if (revArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName()).equals(stateName)) arts.add(revArt);
      }
      return arts;
   }

   public boolean hasReviews() {
      return smaMgr.getSma().hasArtifacts(RelationSide.TeamWorkflowToReview_Review);
   }

   public Result areReviewsComplete() {
      return areReviewsComplete(true);
   }

   public Result areReviewsComplete(boolean popup) {
      try {
         for (ReviewSMArtifact reviewArt : getReviews()) {
            SMAManager smaMgr = new SMAManager(reviewArt);
            if (!smaMgr.isCompleted() && smaMgr.isCancelled()) return new Result("Not Complete");
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return Result.TrueResult;
   }

   public static Collection<String> getAllReviewArtifactTypeNames() {
      return Arrays.asList(new String[] {DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME});
   }
}
