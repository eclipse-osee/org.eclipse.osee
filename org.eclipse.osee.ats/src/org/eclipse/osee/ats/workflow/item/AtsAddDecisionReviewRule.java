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
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
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
public class AtsAddDecisionReviewRule extends WorkRuleDefinition {

   public static String ID = "atsAddDecisionReview";
   public static enum DecisionParameter {
      title, forState, forEvent, reviewBlockingType, assignees, options, description
   };

   public static enum DecisionRuleOption {
      None, TransitionToDecision
   }

   public AtsAddDecisionReviewRule() {
      this(ID, ID);
   }

   public AtsAddDecisionReviewRule(String name, String id) {
      super(name, id);
      setDescription("Work Page and Team Definition Option: Decision Review will be auto-created based on WorkData attribute values.");
      setDecisionParameterValue(this, DecisionParameter.title, "Enter Title Here");
      setDecisionParameterValue(this, DecisionParameter.reviewBlockingType, "Transition");
      setDecisionParameterValue(this, DecisionParameter.forState, "Implement");
      setDecisionParameterValue(this, DecisionParameter.forEvent, StateEventType.TransitionTo.name());
      try {
         setDecisionParameterValue(this, DecisionParameter.assignees, "<99999997>");
         setDecisionParameterValue(this, DecisionParameter.options, "Completed;Completed;");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public static void setDecisionParameterValue(WorkRuleDefinition workRuleDefinition, DecisionParameter decisionParameter, String value) {
      workRuleDefinition.addWorkDataKeyValue(decisionParameter.name(), value);
   }

   public static String getDecisionParameterValue(WorkRuleDefinition workRuleDefinition, DecisionParameter decisionParameter) {
      return workRuleDefinition.getWorkDataValue(decisionParameter.name());
   }

   /**
    * Creates decision review if one of same name doesn't already exist
    * 
    * @param atsAddDecisionReviewRule
    * @param sma
    * @return DecisionReviewArtifact
    * @throws OseeCoreException
    */
   public static DecisionReviewArtifact createNewDecisionReview(WorkRuleDefinition atsAddDecisionReviewRule, SkynetTransaction transaction, TeamWorkFlowArtifact teamArt, DecisionRuleOption... decisionRuleOption) throws OseeCoreException {
      if (!atsAddDecisionReviewRule.getId().startsWith(AtsAddDecisionReviewRule.ID)) {
         throw new IllegalArgumentException("WorkRuleDefinition must be AtsAddDecisionReviewRule.ID");
      }
      String title = getValueOrDefault(teamArt, atsAddDecisionReviewRule, DecisionParameter.title);
      if (Artifacts.artNames(ReviewManager.getReviews(teamArt)).contains(title)) {
         // Already created this review
         return null;
      }
      DecisionReviewArtifact decArt = null;
      if (Collections.getAggregate(decisionRuleOption).contains(DecisionRuleOption.TransitionToDecision)) {
         decArt =
               ReviewManager.createNewDecisionReviewAndTransitionToDecision(teamArt, title, getValueOrDefault(teamArt,
                     atsAddDecisionReviewRule, DecisionParameter.description), getValueOrDefault(teamArt,
                     atsAddDecisionReviewRule, DecisionParameter.forState), getReviewBlockTypeOrDefault(teamArt,
                     atsAddDecisionReviewRule), getValueOrDefault(teamArt, atsAddDecisionReviewRule,
                     DecisionParameter.options), getAssigneesOrDefault(teamArt, atsAddDecisionReviewRule), transaction);
      } else {
         decArt =
               ReviewManager.createNewDecisionReview(teamArt, title, getValueOrDefault(teamArt,
                     atsAddDecisionReviewRule, DecisionParameter.description), getValueOrDefault(teamArt,
                     atsAddDecisionReviewRule, DecisionParameter.forState), getReviewBlockTypeOrDefault(teamArt,
                     atsAddDecisionReviewRule), getValueOrDefault(teamArt, atsAddDecisionReviewRule,
                     DecisionParameter.options), getAssigneesOrDefault(teamArt, atsAddDecisionReviewRule), transaction);
      }

      decArt.getLog().addLog(LogType.Note, null,
            "Review auto-generated off rule " + atsAddDecisionReviewRule.getId());
      return decArt;
   }

   public static ReviewBlockType getReviewBlockTypeOrDefault(TeamWorkFlowArtifact teamArt, WorkRuleDefinition workRuleDefinition) {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.reviewBlockingType);
      if (value == null || value.equals("")) {
         return null;
      }
      return ReviewBlockType.valueOf(value);
   }

   public static StateEventType getStateEventType(TeamWorkFlowArtifact teamArt, WorkRuleDefinition workRuleDefinition) {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.forEvent);
      if (value == null || value.equals("")) {
         return null;
      }
      return StateEventType.valueOf(value);
   }

   private static String getValueOrDefault(TeamWorkFlowArtifact teamArt, WorkRuleDefinition workRuleDefinition, DecisionParameter decisionParameter) throws OseeCoreException {
      String value = getDecisionParameterValue(workRuleDefinition, decisionParameter);
      if (value == null || value.equals("")) {
         if (decisionParameter == DecisionParameter.title) {
            return "Decide on \"" + teamArt.getName() + "\"";
         } else if (decisionParameter == DecisionParameter.options) {
            return "Yes;Followup;<" + UserManager.getUser().getUserId() + ">\n" + "No;Completed;";
         } else if (decisionParameter == DecisionParameter.description) {
            return null;
         } else if (decisionParameter == DecisionParameter.forState) {
            return teamArt.getStateMgr().getCurrentStateName();
         }
      }
      return value;
   }

   public static Collection<User> getAssigneesOrDefault(TeamWorkFlowArtifact teamArt, WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.assignees);
      if (value == null || value.equals("")) {
         return Arrays.asList(new User[] {UserManager.getUser()});
      }
      Collection<User> users = UsersByIds.getUsers(value);
      if (users.size() == 0) {
         users.add(UserManager.getUser());
      }
      return users;
   }
}
