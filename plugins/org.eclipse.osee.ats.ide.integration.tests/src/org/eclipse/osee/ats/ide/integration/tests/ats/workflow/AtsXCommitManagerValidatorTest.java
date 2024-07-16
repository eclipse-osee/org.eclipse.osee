/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.util.ArtifactValueProvider;
import org.eclipse.osee.ats.ide.integration.tests.util.AtsMockitoTest;
import org.eclipse.osee.ats.ide.util.validate.AtsXCommitManagerValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author Donald G. Dunne
 */
public class AtsXCommitManagerValidatorTest extends AtsMockitoTest {

   public AtsXCommitManagerValidatorTest() {
      super("AtsXCommitManagerValidatorTest");
   }

   // @formatter:off
   @Mock WidgetDefinition widgetDef;
   @Mock WidgetDefinition notXCommitManagerWidget;
   @Mock ArtifactValueProvider provider;
   @Mock IValueProvider notArtifactValueProvider;
   @Mock IAtsWorkItem workItem;
   // @formatter:on

   @Override
   @Before
   public void setup() {
      super.setup();

      when(widgetDef.getXWidgetName()).thenReturn("XCommitManager");
      when(widgetDef.getName()).thenReturn("Commit Manager");
      when(implement.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())).thenReturn(false);
   }

   @Test
   public void testValidateTransition_notCommitManager() {
      AtsXCommitManagerValidator validator = new AtsXCommitManagerValidator();

      WidgetResult result = validator.validateTransition(workItem, null, notXCommitManagerWidget, null, null, null);
      Assert.assertEquals(WidgetResult.Success, result);

      result = validator.validateTransition(workItem, notArtifactValueProvider, widgetDef, null, null, atsApi);
      Assert.assertEquals(WidgetResult.Success, result);

      when(provider.getObject()).thenReturn(task1);
      result = validator.validateTransition(workItem, provider, widgetDef, null, null, atsApi);
      Assert.assertEquals(WidgetResult.Success, result);

   }

   @Test
   public void testValidateTransition_noChangesToCommit() {
      AtsXCommitManagerValidator validator = new AtsXCommitManagerValidator();

      when(provider.getObject()).thenReturn(teamWf);
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(false);
      when(branchService.isCommittedBranchExists(teamWf)).thenReturn(false);
      WidgetResult result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(WidgetResult.Success, result);
   }

   @Test
   public void testValidateTransition_changesToCommit() {
      AtsXCommitManagerValidator validator = new AtsXCommitManagerValidator();

      when(provider.getObject()).thenReturn(teamWf);
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(true);
      when(branchService.isCommittedBranchExists(teamWf)).thenReturn(false);
      when(branchService.isAllObjectsToCommitToConfigured(teamWf)).thenReturn(true);
      WidgetResult result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(AtsXCommitManagerValidator.ALL_BRANCHES_MUST_BE_COMMITTED, result.getDetails());

      when(provider.getObject()).thenReturn(teamWf);
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(false);
      when(branchService.isCommittedBranchExists(teamWf)).thenReturn(true);
      when(branchService.isAllObjectsToCommitToConfigured(teamWf)).thenReturn(true);
      result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(AtsXCommitManagerValidator.ALL_BRANCHES_MUST_BE_COMMITTED, result.getDetails());
   }

   @Test
   public void testValidateTransition_transitionToWithWorkingBranchRuleExists() {
      AtsXCommitManagerValidator validator = new AtsXCommitManagerValidator();

      when(provider.getObject()).thenReturn(teamWf);
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(true);
      when(branchService.isCommittedBranchExists(teamWf)).thenReturn(false);
      when(branchService.isAllObjectsToCommitToConfigured(teamWf)).thenReturn(true);
      when(implement.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())).thenReturn(true);
      WidgetResult result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(WidgetResult.Success, result);
   }

   @Test
   public void testValidateTransition_branchesMustBeConfigured() {

      AtsXCommitManagerValidator validator = new AtsXCommitManagerValidator();
      when(provider.getObject()).thenReturn(teamWf);
      when(branchService.isWorkingBranchInWork(teamWf)).thenReturn(true);
      when(branchService.isCommittedBranchExists(teamWf)).thenReturn(false);
      when(branchService.isAllObjectsToCommitToConfigured(teamWf)).thenReturn(true);
      when(implement.hasRule(RuleDefinitionOption.AllowTransitionWithWorkingBranch.name())).thenReturn(false);

      when(branchService.isBranchesAllCommitted(teamWf)).thenReturn(true);
      WidgetResult result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(WidgetResult.Success, result);

      when(branchService.isBranchesAllCommitted(teamWf)).thenReturn(false);
      result = validator.validateTransition(workItem, provider, widgetDef, analyze, implement, atsApi);
      Assert.assertEquals(AtsXCommitManagerValidator.ALL_BRANCHES_MUST_BE_COMMITTED, result.getDetails());

   }

}
