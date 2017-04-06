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
package org.eclipse.osee.ats.client.integration.tests.ats.workflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AddRuleData;
import org.eclipse.osee.ats.api.workdef.CreateTaskRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.NullRuleDefinition;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test unit for {@link CreateTaskRuleDefinition}
 *
 * @author Mark Joy
 */

public class CreateTaskRuleTest {

   private static final String CREATE_TASK_ACTION_FROM_TEAM_DEF_TITLE = "CreateTaskActionFromTeamDefTitle";
   private static final String CREATE_TASK_ACTION_FROM_AI_TITLE = "CreateTaskActionFromAITitle";
   private static final String TEST_CREATE_TASK_RULE = "TestCreateTaskRule";

   @Before
   @After
   public void cleanup() throws Exception {
      IAtsClient atsClient = AtsClientService.get();
      Artifact ruleArtifact = atsClient.getArtifact(AtsArtifactToken.RuleDefinitions);
      ruleArtifact.deleteAttributes(AtsAttributeTypes.DslSheet);
      ruleArtifact.persist(getClass().getSimpleName());
      ruleArtifact.reloadAttributesAndRelations();

      Artifact teamDefArt = atsClient.getArtifact(DemoArtifactToken.SAW_Code);
      teamDefArt.deleteAttribute(AtsAttributeTypes.RuleDefinition, TEST_CREATE_TASK_RULE);
      teamDefArt.persist(getClass().getSimpleName());
      teamDefArt.reloadAttributesAndRelations();

      Artifact aiArt = atsClient.getArtifact(DemoArtifactToken.SAW_Test_AI);
      aiArt.deleteAttribute(AtsAttributeTypes.RuleDefinition, TEST_CREATE_TASK_RULE);
      aiArt.persist(getClass().getSimpleName());
      aiArt.reloadAttributesAndRelations();

      AtsTestUtil.cleanupSimpleTest(CREATE_TASK_ACTION_FROM_TEAM_DEF_TITLE);
      AtsTestUtil.cleanupSimpleTest(CREATE_TASK_ACTION_FROM_AI_TITLE);
      AtsTestUtil.cleanupSimpleTest(getClass().getSimpleName());

      AtsClientService.get().getWorkDefinitionService().clearRuleDefinitionsCache();
   }

   @Test
   @Ignore
   public void testTeamDefConfig() {
      test(DemoArtifactToken.SAW_Code, DemoArtifactToken.SAW_Code_AI, CREATE_TASK_ACTION_FROM_TEAM_DEF_TITLE);
   }

   @Test
   @Ignore
   public void testActionableItemConfig() {
      test(DemoArtifactToken.SAW_Test_AI, DemoArtifactToken.SAW_Test_AI, CREATE_TASK_ACTION_FROM_AI_TITLE);
   }

   public void test(ArtifactToken configObjectToken, ArtifactToken actionableItem, String title) {
      IAtsClient atsClient = AtsClientService.get();
      Collection<IAtsRuleDefinition> allRuleDefs = atsClient.getWorkDefinitionService().getAllRuleDefinitions();
      Assert.assertEquals("We should have no rules", 0, allRuleDefs.size());

      IAtsRuleDefinition ruleDef = atsClient.getWorkDefinitionService().getRuleDefinition(TEST_CREATE_TASK_RULE);
      Assert.assertTrue("Should be a NullRuleDefinition", ruleDef instanceof NullRuleDefinition);

      // Retrieve Rule DSL artifact and add new rules for testing with
      Artifact ruleArtifact = atsClient.getArtifact(AtsArtifactToken.RuleDefinitions);
      ruleArtifact.setSoleAttributeFromString(AtsAttributeTypes.DslSheet, getTestDSL());
      ruleArtifact.persist(getClass().getSimpleName());

      ruleArtifact.reloadAttributesAndRelations();
      // check that rule is added successfully
      ruleDef = atsClient.getWorkDefinitionService().getRuleDefinition(TEST_CREATE_TASK_RULE);
      Assert.assertEquals("Should have found rule " + TEST_CREATE_TASK_RULE, TEST_CREATE_TASK_RULE, ruleDef.getName());

      // Update/Create? TeamWorkflow with rule for testing create task rule
      AddRuleData setRuleData = new AddRuleData();
      setRuleData.setConfigItemUuid(configObjectToken.getId());
      setRuleData.setRuleName(TEST_CREATE_TASK_RULE);
      AtsClientService.getRuleEp().addRuleToConfig(setRuleData);

      Artifact teamDefArt = atsClient.getArtifact(configObjectToken);
      teamDefArt.reloadAttributesAndRelations();
      String ruleName = teamDefArt.getSoleAttributeValueAsString(AtsAttributeTypes.RuleDefinition, "");
      Assert.assertEquals("Rule name should be " + TEST_CREATE_TASK_RULE, TEST_CREATE_TASK_RULE, ruleName);

      // Create new Workflow - rule should be triggered
      IAtsUser currentUser = atsClient.getUserService().getCurrentUser();
      IAtsChangeSet changes = atsClient.getStoreService().createAtsChangeSet(getClass().getSimpleName(), currentUser);
      ActionResult result = atsClient.getActionFactory().createAction(currentUser, getClass().getSimpleName(), title,
         ChangeType.Improvement, "priority", false, null,
         ActionableItems.getActionableItems(Arrays.asList(actionableItem.getName()), AtsClientService.get()),
         new Date(), currentUser, null, changes);
      changes.execute();

      // verify that tasks have been created
      Collection<IAtsTask> tasks = atsClient.getTaskService().getTasks(result.getFirstTeam());
      Assert.assertEquals("There can be only one", 1, tasks.size());
      IAtsTask next = tasks.iterator().next();
      Assert.assertEquals("Name should be *Create a Task from Rule*", "Create a Task from Rule - CreateTaskRuleTest",
         next.getName());
      Assert.assertEquals("Should be a description", "This is the description of the task", next.getDescription());

   }

   private String getTestDSL() {
      String rule = "rule name \"" + TEST_CREATE_TASK_RULE + "\" { \n";
      rule += " title \"Create a Task from Rule - CreateTaskRuleTest\" \n";
      rule += " description \"This is the description of the task\" \n";
      rule += " ruleLocation TeamDefinition \n";
      rule += " assignees \"Joe Smith\" \n";
      rule += " relatedState \"Implement\" \n";
      rule += " taskWorkDef \"WorkDef_Team_Default\" \n";
      rule += " onEvent CreateWorkflow  \n} \n";
      return rule;
   }

}
