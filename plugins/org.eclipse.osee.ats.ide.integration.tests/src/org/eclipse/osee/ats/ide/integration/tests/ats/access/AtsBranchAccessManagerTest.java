/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.access;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.ide.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class AtsBranchAccessManagerTest {

   @Test
   public void testIsApplicable() {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager();
      Assert.assertFalse(mgr.isApplicable(AtsApiService.get().getAtsBranch()));
      Assert.assertFalse(mgr.isApplicable(SAW_Bld_1));

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertNotNull(teamArt);

      BranchId branch = AtsApiService.get().getBranchService().getWorkingBranch(teamArt);
      Assert.assertNotNull(branch);

      Assert.assertTrue(mgr.isApplicable(branch));
   }

   @Before
   @After
   public void cleanup() {
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(),
         "testGetContextIdArtifact cleanup");
      Artifact teamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(teamArt.getTeamDefinition());
      teamDefArt.deleteAttributes(CoreAttributeTypes.AccessContextId);
      teamDefArt.persist(transaction);
      for (Artifact art : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_ActionableItem)) {
         art.deleteAttributes(CoreAttributeTypes.AccessContextId);
         art.persist(transaction);
      }
      teamArt.deleteAttributes(CoreAttributeTypes.AccessContextId);
      teamArt.persist(transaction);
      transaction.execute();
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.ats.ide.access.AtsBranchAccessManager#getContextId(org.eclipse.osee.framework.skynet.core.artifact.Artifact)}
    * {@link org.eclipse.osee.ats.ide.access.AtsBranchAccessManager#getEventFilters()}.
    * {@link org.eclipse.osee.ats.ide.access.AtsBranchAccessManager#handleArtifactEvent(org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent, org.eclipse.osee.framework.skynet.core.event.model.Sender)}
    */
   @Test
   public void testGetContextIdBranch() throws Exception {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager();
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);

      // confirm that no context id returned
      Assert.assertEquals(0, mgr.getContextId(teamArt.getWorkingBranch(), false).size());

      String teamDefContextId1 = "1234";
      String teamDefContextId2 = "2345";
      Artifact teamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(teamArt.getTeamDefinition());
      teamDefArt.setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamDefContextId1, teamDefContextId2));
      teamDefArt.persist(getClass().getSimpleName());

      Assert.assertEquals(2, mgr.getContextId(teamArt.getWorkingBranch(), false).size());

      String aiContextId = "6789";
      Artifact aiArt = AtsApiService.get().getQueryServiceIde().getArtifact(
         AtsApiService.get().getActionableItemService().getActionableItems(
            Arrays.asList(DemoActionableItems.SAW_Requirements.getName())).iterator().next());
      aiArt.setAttributeValues(CoreAttributeTypes.AccessContextId, Arrays.asList(aiContextId));
      aiArt.persist(getClass().getSimpleName());

      Assert.assertEquals(1, mgr.getContextId(teamArt.getWorkingBranch(), false).size());

      String teamContextId1 = "1234";
      String teamContextId2 = "2345";
      String teamContextId3 = "3456";
      teamArt.setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamContextId1, teamContextId2, teamContextId3));
      teamArt.persist(getClass().getSimpleName());

      AtsApiService.get().clearCaches();

      Assert.assertEquals(3, mgr.getContextId(teamArt.getWorkingBranch(), false).size());
   }

   /**
    * Test method for {
    *
    * @param useCache TODO@link org.eclipse.osee.ats.ide.access.AtsBranchAccessManager#convertAccessAttributeToGuid
    */
   @Test
   public void testConvertAccessAttributeToGuid() throws Exception {
      AtsBranchAccessManager mgr = new AtsBranchAccessManager();
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);

      // confirm that no context id returned
      Assert.assertEquals(0, mgr.getContextId(teamArt.getWorkingBranch()).size());

      String teamDefContextId1 = "16546, this is the name";
      Artifact teamDefArt = AtsApiService.get().getQueryServiceIde().getArtifact(teamArt.getTeamDefinition());
      teamDefArt.setAttributeValues(CoreAttributeTypes.AccessContextId, Arrays.asList(teamDefContextId1));
      teamDefArt.persist(getClass().getSimpleName());

      mgr.clearCache();

      Collection<AccessContextToken> contextIds = mgr.getContextId(teamArt.getWorkingBranch());
      Assert.assertEquals(1, contextIds.size());
      AccessContextToken contextId = contextIds.iterator().next();
      Assert.assertEquals(Long.valueOf(16546), contextId.getId());
   }
}
