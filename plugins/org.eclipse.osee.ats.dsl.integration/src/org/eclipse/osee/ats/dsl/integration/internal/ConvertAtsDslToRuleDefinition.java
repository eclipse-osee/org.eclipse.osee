/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.integration.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.CreateTaskRuleDefinition;
import org.eclipse.osee.ats.api.workdef.DecisionReviewRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RuleLocations;
import org.eclipse.osee.ats.api.workdef.model.PeerReviewRuleDefinition;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinition;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.CreateDecisionReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.CreatePeerReviewRule;
import org.eclipse.osee.ats.dsl.atsDsl.CreateTaskRule;
import org.eclipse.osee.ats.dsl.atsDsl.OnEventType;
import org.eclipse.osee.ats.dsl.atsDsl.Rule;
import org.eclipse.osee.ats.dsl.atsDsl.RuleLocation;
import org.eclipse.osee.ats.dsl.atsDsl.UserDef;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Mark Joy
 */
public class ConvertAtsDslToRuleDefinition {
   private final AtsDsl atsDsl;
   private final List<IAtsRuleDefinition> ruleList;
   private final IAtsUserService userService;

   public ConvertAtsDslToRuleDefinition(AtsDsl atsDsl, List<IAtsRuleDefinition> ruleList, IAtsUserService userService) {
      this.atsDsl = atsDsl;
      this.ruleList = ruleList;
      this.userService = userService;
   }

   public List<IAtsRuleDefinition> convert() {
      RuleDefinition newRule;
      for (Rule rule : atsDsl.getRule()) {
         if (rule instanceof CreateTaskRule) {
            newRule = new CreateTaskRuleDefinition();
            convertCreateTaskRule((CreateTaskRuleDefinition) newRule, (CreateTaskRule) rule);
         } else if (rule instanceof PeerReviewRuleDefinition) {
            newRule = new PeerReviewRuleDefinition();
            convertPeerReviewRule((PeerReviewRuleDefinition) newRule, (CreatePeerReviewRule) rule);
         } else if (rule instanceof DecisionReviewRuleDefinition) {
            newRule = new DecisionReviewRuleDefinition();
            convertDecisionReviewRule((DecisionReviewRuleDefinition) newRule, (CreateDecisionReviewRule) rule);
         } else {
            newRule = new RuleDefinition();
         }
         setRuleDefinitionValues(newRule, rule);
         ruleList.add(newRule);
      }
      return ruleList;
   }

   private void setRuleDefinitionValues(RuleDefinition newRule, Rule dslRule) {
      newRule.setName(Strings.unquote(dslRule.getName()));
      newRule.setTitle(Strings.unquote(dslRule.getTitle()));
      newRule.setDescription(Strings.unquote(dslRule.getDescription()));
      List<RuleLocations> ruleLocs = new ArrayList<>();
      for (RuleLocation loc : dslRule.getRuleLocation()) {
         ruleLocs.add(RuleLocations.valueOf(loc.getName()));
      }
      newRule.setRuleLocs(ruleLocs);

   }

   private void convertCreateTaskRule(CreateTaskRuleDefinition newRule, CreateTaskRule dslRule) {
      newRule.setTaskWorkDef(Strings.unquote(dslRule.getTaskWorkDef()));
      newRule.setRelatedState(Strings.unquote(dslRule.getRelatedState()));
      List<RuleEventType> ruleEvents = new ArrayList<>();
      for (OnEventType event : dslRule.getOnEvent()) {
         ruleEvents.add(RuleEventType.valueOf(event.getName()));
      }
      newRule.setRuleEvents(ruleEvents);
      createUserListFromNames(newRule.getAssignees(), dslRule.getAssignees());
   }

   private void convertDecisionReviewRule(DecisionReviewRuleDefinition newRule, CreateDecisionReviewRule dslRule) {
      newRule.setRelatedToState(Strings.unquote(dslRule.getRelatedToState()));
      newRule.setBlockingType(ReviewBlockType.valueOf(dslRule.getBlockingType().getName()));
      newRule.getRuleEvents().add(RuleEventType.valueOf(dslRule.getStateEvent().getName()));
      newRule.setAutoTransitionToDecision(dslRule.getAutoTransitionToDecision() == BooleanDef.TRUE);
      createUserListFromNames(newRule.getAssignees(), dslRule.getAssignees());
   }

   private void convertPeerReviewRule(PeerReviewRuleDefinition newRule, CreatePeerReviewRule dslRule) {
      newRule.setRelatedToState(Strings.unquote(dslRule.getRelatedToState()));
      newRule.setBlockingType(ReviewBlockType.valueOf(dslRule.getBlockingType().getName()));
      newRule.getRuleEvents().add(RuleEventType.valueOf(dslRule.getStateEvent().getName()));
      newRule.setLocation(Strings.unquote(dslRule.getLocation()));
      createUserListFromNames(newRule.getAssignees(), dslRule.getAssignees());
   }

   private void createUserListFromNames(List<IAtsUser> assignees, List<UserDef> userdefs) {
      IAtsUser atsUser = null;
      if (this.userService != null) {
         for (UserDef name : userdefs) {
            String modName = Strings.unquote(name.getName());
            // assignees are optional so need to catch the exception, but log if user is not found or valid
            try {
               atsUser = userService.getUserByName(modName);
            } catch (OseeCoreException ex) {
               OseeLog.log(ConvertAtsDslToRuleDefinition.class, Level.WARNING, "Could not find user: " + modName);
            }
            if (atsUser != null) {
               assignees.add(atsUser);
            }
         }
      }
   }
}
