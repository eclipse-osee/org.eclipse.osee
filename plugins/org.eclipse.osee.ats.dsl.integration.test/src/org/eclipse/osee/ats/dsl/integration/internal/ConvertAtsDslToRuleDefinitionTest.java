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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RuleLocations;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link ConvertAtsDslToRuleDefinition}
 *
 * @author Mark Joy
 */
public class ConvertAtsDslToRuleDefinitionTest {

   // @formatter:off
   @Mock IAtsUserService userService;
   @Mock IAtsUser user;
   // @formatter:on

   private static final String TEST_CREATE_TASK_RULE = "TestCreateTaskRule";
   private static final String TEST_CREATE_TASK_RULE2 = "TestCreateTaskRuleTwo";

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(userService.getUserByName(Matchers.anyString())).thenReturn(user);
   }

   @Test
   public void test_successfulruleparsing() {
      List<IAtsRuleDefinition> ruleDefs = new ArrayList<>();
      AtsDsl atsDsl = null;
      try {
         atsDsl = ModelUtil.loadModel("Rule Definitions" + ".ats", getTestDSL());
      } catch (Exception ex) {
         fail("Could not instantiate AtsDsl." + ex.getMessage());
      }
      ConvertAtsDslToRuleDefinition convert = new ConvertAtsDslToRuleDefinition(atsDsl, ruleDefs, userService);
      ruleDefs = convert.convert();
      Assert.assertEquals("Should have two rules", 2, ruleDefs.size());
      Assert.assertEquals("Rule one should have one assignee", 1, ruleDefs.get(0).getAssignees().size());
      Assert.assertEquals("Rule one should be for CreateWorkflow Event", RuleEventType.CreateWorkflow,
         ruleDefs.get(0).getRuleEvents().get(0));
      Assert.assertEquals("Rule one should be for Team_Definition Location", RuleLocations.TeamDefinition,
         ruleDefs.get(0).getRuleLocs().get(0));
      Assert.assertEquals("Rule two should have two assignees", 2, ruleDefs.get(1).getAssignees().size());
      Assert.assertEquals("Rule two should be for Manual Event", RuleEventType.Manual,
         ruleDefs.get(1).getRuleEvents().get(0));
      Assert.assertEquals("Rule two should be for CreateWorkflow Event", RuleEventType.CreateWorkflow,
         ruleDefs.get(1).getRuleEvents().get(1));
      Assert.assertEquals("Rule two should be for Actionable Item Location", RuleLocations.ActionableItem,
         ruleDefs.get(1).getRuleLocs().get(0));
   }

   @Test
   public void test_unsuccessfulruleparsing() {
      AtsDsl atsDsl = null;
      try {
         atsDsl = ModelUtil.loadModel("Rule Definitions" + ".ats", getBadTestDSL());
      } catch (Exception ex) {
         // Do nothing
      }
      Assert.assertEquals("AtsDSL should be null", atsDsl, null);
   }

   private String getTestDSL() {
      String rule = "rule name \"" + TEST_CREATE_TASK_RULE + "\" { \n";
      rule += " title \"Create a Task from Rule\" \n";
      rule += " description \"This is the description of the task\" \n";
      rule += " ruleLocation TeamDefinition \n";
      rule += " assignees \"Joe Smith\" \n";
      rule += " relatedState \"Implement\" \n";
      rule += " taskWorkDef \"WorkDef_Team_Default\" \n";
      rule += " onEvent CreateWorkflow  \n} \n";
      rule += "rule name \"" + TEST_CREATE_TASK_RULE2 + "\" { \n";
      rule += " title \"Create a second Task from Rule\" \n";
      rule += " description \"This is a different description of a task\" \n";
      rule += " ruleLocation ActionableItem \n";
      rule += " assignees \"Joe Smith\" \n";
      rule += " assignees \"John Stevens\" \n";
      rule += " relatedState \"Analyze\" \n";
      rule += " taskWorkDef \"WorkDef_Team_Default\" \n";
      rule += " onEvent Manual  \n";
      rule += " onEvent CreateWorkflow  \n} \n";
      return rule;
   }

   private String getBadTestDSL() {
      String rule = "rule name \"" + TEST_CREATE_TASK_RULE + "\" { \n";
      rule += " title \"Create a Task from Rule\" \n";
      rule += " description \"This is the description of the task\" \n";
      // Remove ruleLocation to make rule invalid  rule += " ruleLocation TeamDefinition \n";
      rule += " assignees \"Joe Smith\" \n";
      rule += " taskWorkDef \"Something\" \n";
      rule += " relatedState \"Implement\" \n";
      rule += " onEvent CreateWorkflow  \n} \n";
      return rule;
   }

}
