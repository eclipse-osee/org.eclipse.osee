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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.workdef.ReviewBlockType;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.StateEventType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAddPeerToPeerReviewRule extends WorkRuleDefinition {

   public final static String ID = "atsAddPeerToPeerReview";
   public static enum PeerToPeerParameter {
      title,
      forState,
      forEvent,
      reviewBlockingType,
      assignees,
      location,
      description
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

   public static String getPeerToPeerParameterValue(RuleDefinition ruleDefinition, PeerToPeerParameter decisionParameter) {
      return ruleDefinition.getWorkDataValue(decisionParameter.name());
   }

   public static Collection<User> getAssigneesOrDefault(WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new User[] {UserManager.getUser()});
      }
      Collection<User> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }

   public static String getReviewTitle(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.title);
   }

   public static String getRelatedToState(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.forState);
   }

   public static String getLocation(WorkRuleDefinition workRuleDefinition) {
      return getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.location);
   }

   public static StateEventType getStateEventType(WorkRuleDefinition workRuleDefinition) {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.forEvent);
      if (!Strings.isValid(value)) {
         return null;
      }
      return StateEventType.valueOf(value);
   }

   public static ReviewBlockType getReviewBlockTypeOrDefault(WorkRuleDefinition workRuleDefinition) {
      String value = getPeerToPeerParameterValue(workRuleDefinition, PeerToPeerParameter.reviewBlockingType);
      if (!Strings.isValid(value)) {
         return null;
      }
      return ReviewBlockType.valueOf(value);
   }

   public static String getPeerToPeerParameterValue(WorkRuleDefinition workRuleDefinition, PeerToPeerParameter decisionParameter) {
      return workRuleDefinition.getWorkDataValue(decisionParameter.name());
   }

   /**
    * Creates PeerToPeer review if one of same name doesn't already exist
    */
   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(PeerReviewDefinition peerRevDef, SkynetTransaction transaction, TeamWorkFlowArtifact teamArt, Date createdDate, User createdBy) throws OseeCoreException {
      String title = peerRevDef.getTitle();
      if (!Strings.isValid(title)) {
         title = String.format("Review [%s]", teamArt.getName());
      }
      if (Artifacts.artNames(ReviewManager.getReviews(teamArt)).contains(title)) {
         // Already created this review
         return null;
      }
      PeerToPeerReviewArtifact peerArt =
         ReviewManager.createNewPeerToPeerReview(teamArt, title, peerRevDef.getRelatedToState(), createdDate,
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
      Collection<User> assignees = UserManager.getUsersByUserId(peerRevDef.getAssignees());
      if (assignees.size() > 0) {
         peerArt.getStateMgr().setAssignees(assignees);
      }
      peerArt.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", peerRevDef.getName()));
      return peerArt;
   }

}
