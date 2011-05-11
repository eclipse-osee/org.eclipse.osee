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
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.ats.core.workflow.log.LogType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAddDecisionReviewRule {

   public final static String ID = "atsAddDecisionReview";
   public static enum DecisionParameter {
      title,
      forState,
      forEvent,
      reviewBlockingType,
      assignees,
      options,
      description
   };

   public static enum DecisionRuleOption {
      None,
      TransitionToDecision
   }

   public static void setDecisionParameterValue(WorkRuleDefinition workRuleDefinition, DecisionParameter decisionParameter, String value) {
      workRuleDefinition.addWorkDataKeyValue(decisionParameter.name(), value);
   }

   public static String getDecisionParameterValue(WorkRuleDefinition workRuleDefinition, DecisionParameter decisionParameter) {
      return workRuleDefinition.getWorkDataValue(decisionParameter.name());
   }

   public static String getDecisionParameterValue(RuleDefinition ruleDefinition, DecisionParameter decisionParameter) {
      return ruleDefinition.getWorkDataValue(decisionParameter.name());
   }

   /**
    * Creates decision review if one of same name doesn't already exist
    */
   public static DecisionReviewArtifact createNewDecisionReview(DecisionReviewDefinition revDef, SkynetTransaction transaction, TeamWorkFlowArtifact teamArt, Date createdDate, User createdBy) throws OseeCoreException {
      if (Artifacts.artNames(ReviewManager.getReviews(teamArt)).contains(revDef.getTitle())) {
         // Already created this review
         return null;
      }
      DecisionReviewArtifact decArt = null;
      if (revDef.isAutoTransitionToDecision()) {
         decArt =
            ReviewManager.createNewDecisionReviewAndTransitionToDecision(teamArt, revDef.getTitle(),
               revDef.getDescription(), revDef.getRelatedToState(), revDef.getBlockingType(), revDef.getOptions(),
               UserManager.getUsersByUserId(revDef.getAssignees()), createdDate, createdBy, transaction);
      } else {
         decArt =
            ReviewManager.createNewDecisionReview(teamArt, revDef.getTitle(), revDef.getDescription(),
               revDef.getRelatedToState(), revDef.getBlockingType(), revDef.getOptions(),
               UserManager.getUsersByUserId(revDef.getAssignees()), createdDate, createdBy, transaction);
      }

      decArt.getLog().addLog(LogType.Note, null, String.format("Review [%s] auto-generated", revDef.getName()));
      return decArt;
   }

   public static ReviewBlockType getReviewBlockTypeOrDefault(RuleDefinition ruleDefinition) {
      String value = getDecisionParameterValue(ruleDefinition, DecisionParameter.reviewBlockingType);
      if (!Strings.isValid(value)) {
         return null;
      }
      return ReviewBlockType.valueOf(value);
   }

   public static ReviewBlockType getReviewBlockTypeOrDefault(WorkRuleDefinition workRuleDefinition) {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.reviewBlockingType);
      if (!Strings.isValid(value)) {
         return null;
      }
      return ReviewBlockType.valueOf(value);
   }

   public static String getDecisionOptionString(WorkRuleDefinition workRuleDefinition) {
      return getDecisionParameterValue(workRuleDefinition, DecisionParameter.options);
      // TODO May need to return default if none specified?
   }

   public static StateEventType getStateEventType(WorkRuleDefinition workRuleDefinition) {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.forEvent);
      if (!Strings.isValid(value)) {
         return null;
      }
      return StateEventType.valueOf(value);
   }

   public static String getReviewTitle(WorkRuleDefinition workRuleDefinition) {
      return getDecisionParameterValue(workRuleDefinition, DecisionParameter.title);
   }

   public static String getRelatedToState(WorkRuleDefinition workRuleDefinition) {
      return getDecisionParameterValue(workRuleDefinition, DecisionParameter.forState);
   }

   public static StateEventType getStateEventType(RuleDefinition ruleDefinition) {
      String value = getDecisionParameterValue(ruleDefinition, DecisionParameter.forEvent);
      if (!Strings.isValid(value)) {
         return null;
      }
      return StateEventType.valueOf(value);
   }

   public static Collection<User> getAssigneesOrDefault(RuleDefinition ruleDefinition) throws OseeCoreException {
      String value = getDecisionParameterValue(ruleDefinition, DecisionParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new User[] {UserManager.getUser()});
      }
      Collection<User> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }

   public static Collection<User> getAssigneesOrDefault(WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new User[] {UserManager.getUser()});
      }
      Collection<User> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }
}
