/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.ai;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.CreateTeamData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.ITeamDefinitionUtility;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test unit for {@link ModifyActionableItems}
 *
 * @author Donald G Dunne
 */
public class ModifyActionableItemsTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf;
   @Mock private AtsUser modifiedBy;
   @Mock private IAtsActionableItem ai1, ai2, ai3;
   @Mock private ITeamDefinitionUtility teamDefUtil;
   @Mock private IAtsTeamDefinition teamDef;
   @Mock private AtsApi atsApi;
   @Mock private IAtsTeamDefinitionService teamDefinitionService;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(atsApi.getTeamDefinitionService()).thenReturn(teamDefinitionService);
      when(teamDefinitionService.getLeads(teamDef)).thenReturn(Collections.emptyList());
   }

   @Test
   public void test_addAi() {
      when(teamWf.getActionableItems()).thenReturn(org.eclipse.osee.framework.jdk.core.util.Collections.hashSet(ai1));
      XResultData results = new XResultData(false);
      List<IAtsActionableItem> currAIsForAllWfs = Arrays.asList(ai1);
      List<IAtsActionableItem> currWorkflowDesiredAIs = Arrays.asList(ai1, ai2);
      List<IAtsActionableItem> newAIs = Collections.emptyList();

      ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs, currWorkflowDesiredAIs,
         newAIs, modifiedBy, teamDefUtil, atsApi);
      job.performModification();

      Assert.assertEquals(1, job.getAddAis().size());
      Assert.assertEquals(0, job.getRemoveAis().size());
      Assert.assertEquals(0, job.getTeamDatas().size());
   }

   @Test
   public void test_removeAi() {
      when(teamWf.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.hashSet(ai1, ai2));
      XResultData results = new XResultData(false);
      List<IAtsActionableItem> currAIsForAllWfs = Arrays.asList(ai1);
      List<IAtsActionableItem> currWorkflowDesiredAIs = Arrays.asList(ai1);
      List<IAtsActionableItem> newAIs = Collections.emptyList();

      ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs, currWorkflowDesiredAIs,
         newAIs, modifiedBy, teamDefUtil, atsApi);
      job.performModification();

      Assert.assertEquals(0, job.getAddAis().size());
      Assert.assertEquals(1, job.getRemoveAis().size());
      Assert.assertEquals(0, job.getTeamDatas().size());
   }

   @Test
   public void test_removeAllAi() {
      when(teamWf.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.hashSet(ai1, ai2));
      XResultData results = new XResultData(false);
      List<IAtsActionableItem> currAIsForAllWfs = Arrays.asList(ai1);
      List<IAtsActionableItem> currWorkflowDesiredAIs = Collections.emptyList();
      List<IAtsActionableItem> newAIs = Collections.emptyList();

      ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs, currWorkflowDesiredAIs,
         newAIs, modifiedBy, teamDefUtil, atsApi);
      job.performModification();

      Assert.assertTrue(results.toString().contains("Error: All AIs can not be removed"));
      Assert.assertEquals(0, job.getAddAis().size());
      Assert.assertEquals(0, job.getRemoveAis().size());
      Assert.assertEquals(0, job.getTeamDatas().size());
   }

   @Test
   public void test_newAi() {
      when(teamWf.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.hashSet(ai1, ai2));
      when(ai3.isActionable()).thenReturn(true);
      when(ai3.isAllowUserActionCreation()).thenReturn(true);
      when(teamDefUtil.getImpactedTeamDefs(Arrays.asList(ai3))).thenReturn(Collections.singleton(teamDef));

      XResultData results = new XResultData(false);
      List<IAtsActionableItem> currAIsForAllWfs = Arrays.asList(ai1, ai2);
      List<IAtsActionableItem> currWorkflowDesiredAIs = Arrays.asList(ai1, ai2);
      List<IAtsActionableItem> newAIs = Arrays.asList(ai3);

      ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs, currWorkflowDesiredAIs,
         newAIs, modifiedBy, teamDefUtil, atsApi);
      job.performModification();

      Assert.assertEquals(0, job.getAddAis().size());
      Assert.assertEquals(0, job.getRemoveAis().size());
      Assert.assertEquals(1, job.getTeamDatas().size());
      Assert.assertFalse(results.isErrors());
      CreateTeamData data = job.getTeamDatas().iterator().next();
      Assert.assertEquals(1, data.getActionableItems().size());
      Assert.assertEquals(ai3, data.getActionableItems().iterator().next());
      Assert.assertEquals(modifiedBy, data.getCreatedBy());
      Assert.assertEquals(teamDef, data.getTeamDef());
      Assert.assertFalse(results.toString().contains("impacted by an existing"));
   }

   @Test
   public void test_duplicateAi() {
      when(teamWf.getActionableItems()).thenReturn(
         org.eclipse.osee.framework.jdk.core.util.Collections.hashSet(ai1, ai2));
      when(ai2.isActionable()).thenReturn(true);
      when(ai2.isAllowUserActionCreation()).thenReturn(true);
      when(teamDefUtil.getImpactedTeamDefs(Arrays.asList(ai2))).thenReturn(Collections.singleton(teamDef));

      XResultData results = new XResultData(false);
      List<IAtsActionableItem> currAIsForAllWfs = Arrays.asList(ai1, ai2);
      List<IAtsActionableItem> currWorkflowDesiredAIs = Arrays.asList(ai1, ai2);
      List<IAtsActionableItem> newAIs = Arrays.asList(ai2);

      ModifyActionableItems job = new ModifyActionableItems(results, teamWf, currAIsForAllWfs, currWorkflowDesiredAIs,
         newAIs, modifiedBy, teamDefUtil, atsApi);
      job.performModification();

      Assert.assertEquals(0, job.getAddAis().size());
      Assert.assertEquals(0, job.getRemoveAis().size());
      Assert.assertEquals(1, job.getTeamDatas().size());
      CreateTeamData data = job.getTeamDatas().iterator().next();
      Assert.assertEquals(ai2, data.getActionableItems().iterator().next());
      Assert.assertTrue(results.toString().contains("impacted by an existing"));
   }

}
