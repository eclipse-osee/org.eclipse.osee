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
package org.eclipse.osee.ats.workflow.item;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
      title, forState, forEvent, reviewBlockingType, assignees, location, description
   };

   public AtsAddPeerToPeerReviewRule() {
      this(ID, ID);
   }

   public AtsAddPeerToPeerReviewRule(String name, String id) {
      super(name, id);
      setDescription("Work Page and Team Definition Option: PeerToPeer Review will be auto-created based on WorkData attribute values.");
      setPeerToPeerParameterValue(this, PeerToPeerParameter.reviewBlockingType, "Commit");
      setPeerToPeerParameterValue(this, PeerToPeerParameter.forState, "Implement");
      setPeerToPeerParameterValue(this, PeerToPeerParameter.forEvent, StateEventType.CreateBranch.name());
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
    * @param sma
    * @param transaction
    * @return review
    * @throws OseeCoreException
    */
   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(WorkRuleDefinition atsAddPeerToPeerReviewRule, TeamWorkFlowArtifact teamArt, SkynetTransaction transaction) throws OseeCoreException {
      if (!atsAddPeerToPeerReviewRule.getId().startsWith(AtsAddPeerToPeerReviewRule.ID)) {
         throw new IllegalArgumentException("WorkRuleDefinition must be AtsAddPeerToPeerReviewRule.ID");
      }
      String title = getValueOrDefault(teamArt, atsAddPeerToPeerReviewRule, PeerToPeerParameter.title);
      if (Artifacts.artNames(ReviewManager.getReviews(teamArt)).contains(title)) {
         // Already created this review
         return null;
      }
      PeerToPeerReviewArtifact peerArt =
            ReviewManager.createNewPeerToPeerReview(teamArt, title, getValueOrDefault(teamArt,
                  atsAddPeerToPeerReviewRule, PeerToPeerParameter.forState), UserManager.getUser(), new Date(),
                  transaction);
      String desc = getValueOrDefault(teamArt, atsAddPeerToPeerReviewRule, PeerToPeerParameter.description);
      if (desc != null && !desc.equals("")) {
         peerArt.setSoleAttributeFromString(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), desc);
      }
      ReviewBlockType reviewBlockType =
            AtsAddDecisionReviewRule.getReviewBlockTypeOrDefault(teamArt, atsAddPeerToPeerReviewRule);
      if (reviewBlockType != null) {
         peerArt.setSoleAttributeFromString(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName(),
               reviewBlockType.name());
      }
      String location = getValueOrDefault(teamArt, atsAddPeerToPeerReviewRule, PeerToPeerParameter.location);
      if (location != null && location.equals("")) {
         peerArt.setSoleAttributeFromString(ATSAttributes.LOCATION_ATTRIBUTE.getStoreName(), location);
      }
      Collection<User> assignees = AtsAddDecisionReviewRule.getAssigneesOrDefault(teamArt, atsAddPeerToPeerReviewRule);
      if (assignees.size() > 0) {
         peerArt.getStateMgr().setAssignees(assignees);
      }
      peerArt.getLog().addLog(LogType.Note, null,
            "Review auto-generated off rule " + atsAddPeerToPeerReviewRule.getId());
      return peerArt;
   }

   private static String getValueOrDefault(TeamWorkFlowArtifact teamArt, WorkRuleDefinition workRuleDefinition, PeerToPeerParameter peerToPeerParameter) throws OseeCoreException {
      String value = getPeerToPeerParameterValue(workRuleDefinition, peerToPeerParameter);
      if (value == null || value.equals("")) {
         if (peerToPeerParameter == PeerToPeerParameter.title) {
            return PeerToPeerReviewArtifact.getDefaultReviewTitle(teamArt);
         } else if (peerToPeerParameter == PeerToPeerParameter.forState) {
            return teamArt.getStateMgr().getCurrentStateName();
         } else if (peerToPeerParameter == PeerToPeerParameter.location) {
            return null;
         }
      }
      return value;
   }

}
