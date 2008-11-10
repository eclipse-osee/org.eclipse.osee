/*
 * Created on Sep 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAddPeerToPeerReviewRule extends WorkRuleDefinition {

   public static String ID = "atsAddPeerToPeerReview";
   public static enum PeerToPeerParameter {
      title, forState, reviewBlockingType, assignees, location, description
   };

   public AtsAddPeerToPeerReviewRule() {
      super(ID, ID);
      setDescription("Work Page and Team Definition Option: PeerToPeer Review will be auto-created based on WorkData attribute values.");
      setPeerToPeerParameterValue(this, PeerToPeerParameter.reviewBlockingType, "Transition");
      setPeerToPeerParameterValue(this, PeerToPeerParameter.forState, "Implement");
      try {
         setPeerToPeerParameterValue(this, PeerToPeerParameter.assignees, "<99999997>");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public static void setPeerToPeerParameterValue(WorkRuleDefinition workRuleDefinition, PeerToPeerParameter decisionParameter, String value) {
      workRuleDefinition.addWorkDataKeyValue(decisionParameter.name(), value);
   }

   public static String getPeerToPeerParameterValue(WorkRuleDefinition workRuleDefinition, PeerToPeerParameter decisionParameter) {
      return workRuleDefinition.getWorkDataValue(decisionParameter.name());
   }

   /**
    * Creates PeerToPeer review if one of same name doesn't already exist
    * 
    * @param atsAddPeerToPeerReviewRule
    * @param smaMgr
    * @param transaction 
    * @return review
    * @throws OseeCoreException
    */
   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(WorkRuleDefinition atsAddPeerToPeerReviewRule, SMAManager smaMgr, SkynetTransaction transaction) throws OseeCoreException {
      if (!atsAddPeerToPeerReviewRule.getId().startsWith(AtsAddPeerToPeerReviewRule.ID)) {
         throw new IllegalArgumentException("WorkRuleDefinition must be AtsAddPeerToPeerReviewRule.ID");
      }
      String title = getValueOrDefault(smaMgr, atsAddPeerToPeerReviewRule, PeerToPeerParameter.title);
      if (Artifacts.artNames(smaMgr.getReviewManager().getReviews()).contains(title)) {
         // Already created this review
         return null;
      }
      PeerToPeerReviewArtifact peerArt =
            ReviewManager.createNewPeerToPeerReview(smaMgr.getSma(), title, getValueOrDefault(smaMgr,
                  atsAddPeerToPeerReviewRule, PeerToPeerParameter.forState), UserManager.getUser(), new Date(), transaction);
      String desc = getValueOrDefault(smaMgr, atsAddPeerToPeerReviewRule, PeerToPeerParameter.description);
      if (desc != null && !desc.equals("")) {
         peerArt.setSoleAttributeFromString(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      }
      ReviewBlockType reviewBlockType =
            AtsAddDecisionReviewRule.getReviewBlockTypeOrDefault(smaMgr, atsAddPeerToPeerReviewRule);
      if (reviewBlockType != null) {
         peerArt.setSoleAttributeFromString(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName(),
               reviewBlockType.name());
      }
      String location = getValueOrDefault(smaMgr, atsAddPeerToPeerReviewRule, PeerToPeerParameter.location);
      if (location != null && location.equals("")) {
         peerArt.setSoleAttributeFromString(ATSAttributes.LOCATION_ATTRIBUTE.getStoreName(), location);
      }
      Collection<User> assignees = AtsAddDecisionReviewRule.getAssigneesOrDefault(smaMgr, atsAddPeerToPeerReviewRule);
      if (assignees.size() > 0) {
         peerArt.getSmaMgr().getStateMgr().setAssignees(assignees);
      }
      peerArt.getSmaMgr().getLog().addLog(LogType.Note, null,
            "Review auto-generated off rule " + atsAddPeerToPeerReviewRule.getId());
      return peerArt;
   }

   private static String getValueOrDefault(SMAManager smaMgr, WorkRuleDefinition workRuleDefinition, PeerToPeerParameter peerToPeerParameter) throws OseeCoreException {
      String value = getPeerToPeerParameterValue(workRuleDefinition, peerToPeerParameter);
      if (value == null || value.equals("")) {
         if (peerToPeerParameter == PeerToPeerParameter.title) {
            return PeerToPeerReviewArtifact.getDefaultReviewTitle(smaMgr);
         } else if (peerToPeerParameter == PeerToPeerParameter.forState) {
            return smaMgr.getStateMgr().getCurrentStateName();
         } else if (peerToPeerParameter == PeerToPeerParameter.location) {
            return null;
         }
      }
      return value;
   }

}
