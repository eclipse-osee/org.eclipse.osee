/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Collection;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.HoldState;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryServiceImplTest {

   /**
    * IAtsQuery AtsQueryServiceImpl.createQuery() tested in AtsQueryImplTest
    */

   /**
    * IAtsConfigQuery AtsQueryServiceImpl.createQuery() tested in AtsQueryImplTest
    */

   /**
    * IAtsWorkItemFilter createFilter() is tested in AtsWorkItemFilterTest
    */

   /**
    * Test for CriteriaAttributeTypeExists<br/>
    * Test for CriteriaAttributeTypeNotExists<br/>
    * getItems()<br/>
    * getItemIds()<br/>
    * getItemsNew()<br/>
    * getResultArtifacts()<br/>
    * getResultArtifactsNew()<br/>
    */
   @Test
   public void testAtsQueryAndQueryBuilderCriteria() {
      AtsApi atsApi = AtsApiService.get();
      String name = getClass().getSimpleName() + "-testAtsQuery";
      AtsTestUtil.cleanupAndReset(name);

      TeamWorkFlowArtifact teamWf = AtsTestUtil.getTeamWf();
      IAtsChangeSet changes = atsApi.createChangeSet("set hold");
      changes.setSoleAttributeValue((IAtsWorkItem) teamWf, AtsAttributeTypes.HoldReason, "the hold reason");
      TransactionToken tx = changes.execute();
      Assert.assertTrue(tx.isValid());

      IAtsQueryService querySvc = atsApi.getQueryService();
      IAtsQuery query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      // CriteriaAttributeTypeExists
      query.andHoldState(HoldState.On_Hold);
      Collection<IAtsWorkItem> items = query.getItems();
      Assert.assertTrue(items.size() == 1);
      Assert.assertTrue(items.iterator().next().getName().contains(name));

      querySvc = atsApi.getQueryService();
      query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      query.andHoldState(HoldState.On_Hold);
      Collection<ArtifactId> itemIds = query.getItemIds();
      Assert.assertTrue(itemIds.size() == 1);
      Assert.assertEquals(teamWf, itemIds.iterator().next());

      querySvc = atsApi.getQueryService();
      query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      query.andHoldState(HoldState.On_Hold);
      Collection<IAtsWorkItem> itemsNew = query.getItemsNew();
      Assert.assertTrue(itemsNew.size() == 1);
      Assert.assertEquals(teamWf, itemsNew.iterator().next());

      querySvc = atsApi.getQueryService();
      query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      query.andHoldState(HoldState.On_Hold);
      Collection<ArtifactToken> itemsResultArts = query.getResultArtifacts().getList();
      Assert.assertTrue(itemsResultArts.size() == 1);
      Assert.assertEquals(teamWf, itemsResultArts.iterator().next());

      querySvc = atsApi.getQueryService();
      query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      query.andHoldState(HoldState.On_Hold);
      Collection<ArtifactToken> itemsResultArtsNew = query.getResultArtifactsNew().getList();
      Assert.assertTrue(itemsResultArtsNew.size() == 1);
      Assert.assertEquals(teamWf, itemsResultArtsNew.iterator().next());

      querySvc = atsApi.getQueryService();
      query = querySvc.createQuery(WorkItemType.TeamWorkflow);
      // CriteriaAttributeTypeNotExists
      query.andHoldState(HoldState.Not_On_Hold);
      itemIds = query.getItemIds();
      Assert.assertTrue(itemIds.size() > 10);
      Assert.assertFalse(itemIds.contains(teamWf));
   }

   @Test
   public void testGetArtifact() {
      AtsApi atsApi = AtsApiService.get();
      IAtsQueryService querySvc = atsApi.getQueryService();

      ArtifactId art = querySvc.getArtifact(DemoArtifactToken.SAW_PL_TeamDef);
      Assert.assertNotNull(art);
      Assert.assertEquals(DemoArtifactToken.SAW_PL_TeamDef, art);

      Assert.assertTrue(art instanceof Artifact);

      @Nullable
      ArtifactToken art2 = querySvc.getArtifact(art);
      Assert.assertNotNull(art2);
      Assert.assertTrue(art2 instanceof Artifact);
      Assert.assertEquals(DemoArtifactToken.SAW_PL_TeamDef, art2);

      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(DemoArtifactToken.SAW_NoBranch_Code_TeamWf);

      @Nullable
      ArtifactToken art4 = querySvc.getArtifact(workItem);
      Assert.assertNotNull(art4);
      Assert.assertEquals(DemoArtifactToken.SAW_NoBranch_Code_TeamWf, art4);

      ArtifactId artId = ArtifactId.valueOf(DemoArtifactToken.SAW_NoBranch_Code_TeamWf.getId());
      ArtifactToken art3 = querySvc.getArtifact(artId);
      Assert.assertNotNull(art3);
      Assert.assertEquals(DemoArtifactToken.SAW_NoBranch_Code_TeamWf, art3);
   }
}