/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.access;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoActionableItems;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.ide.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAccessContextId;
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
      Assert.assertFalse(mgr.isApplicable(AtsClientService.get().getAtsBranch()));
      Assert.assertFalse(mgr.isApplicable(SAW_Bld_1));

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertNotNull(teamArt);

      BranchId branch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
      Assert.assertNotNull(branch);

      Assert.assertTrue(mgr.isApplicable(branch));
   }

   @Before
   @After
   public void cleanup() {
      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         "testGetContextIdArtifact cleanup");
      Artifact teamDefArt = AtsClientService.get().getQueryServiceClient().getArtifact(teamArt.getTeamDefinition());
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
      Artifact teamDefArt = AtsClientService.get().getQueryServiceClient().getArtifact(teamArt.getTeamDefinition());
      teamDefArt.setAttributeValues(CoreAttributeTypes.AccessContextId,
         Arrays.asList(teamDefContextId1, teamDefContextId2));
      teamDefArt.persist(getClass().getSimpleName());

      Assert.assertEquals(2, mgr.getContextId(teamArt.getWorkingBranch(), false).size());

      String aiContextId = "6789";
      Artifact aiArt = AtsClientService.get().getQueryServiceClient().getArtifact(
         AtsClientService.get().getActionableItemService().getActionableItems(
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

      AtsClientService.get().clearCaches();

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
      Artifact teamDefArt = AtsClientService.get().getQueryServiceClient().getArtifact(teamArt.getTeamDefinition());
      teamDefArt.setAttributeValues(CoreAttributeTypes.AccessContextId, Arrays.asList(teamDefContextId1));
      teamDefArt.persist(getClass().getSimpleName());

      mgr.clearCache();

      Collection<IAccessContextId> contextIds = mgr.getContextId(teamArt.getWorkingBranch());
      Assert.assertEquals(1, contextIds.size());
      IAccessContextId contextId = contextIds.iterator().next();
      Assert.assertEquals(Long.valueOf(16546), contextId.getId());
   }
}
