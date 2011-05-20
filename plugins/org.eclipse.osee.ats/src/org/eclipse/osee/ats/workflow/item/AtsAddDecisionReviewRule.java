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
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.StateEventType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
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

   public static Collection<IBasicUser> getAssigneesOrDefault(RuleDefinition ruleDefinition) throws OseeCoreException {
      String value = getDecisionParameterValue(ruleDefinition, DecisionParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new IBasicUser[] {UserManager.getUser()});
      }
      Collection<IBasicUser> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }

   public static Collection<IBasicUser> getAssigneesOrDefault(WorkRuleDefinition workRuleDefinition) throws OseeCoreException {
      String value = getDecisionParameterValue(workRuleDefinition, DecisionParameter.assignees);
      if (!Strings.isValid(value)) {
         return Arrays.asList(new IBasicUser[] {UserManager.getUser()});
      }
      Collection<IBasicUser> users = UsersByIds.getUsers(value);
      if (users.isEmpty()) {
         users.add(UserManager.getUser());
      }
      return users;
   }
}
