/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.branch;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test unit for {@AtsBranchServiceImpl}
 *
 * @author Shawn F. Cook
 */
public class AtsBranchServiceImplTest {

   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      TestUtil.setIsInTest(true);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      AtsTestUtil.cleanup();
      OseeProperties.setIsInTest(false);
      TestUtil.setIsInTest(false);
   }

   @Test
   public void testGetCommitTransactionsAndConfigItemsForTeamWf_teamDef() {
      AtsTestUtil.cleanupAndReset(
         AtsBranchServiceImplTest.class.getSimpleName() + ".testGetCommitTransactionsAndConfigItemsForTeamWf_teamDef");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      //Test Team Def-base Team Arts
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsTeamDefinition teamDef = teamArt.getTeamDefinition();
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.BaselineBranchId, SAW_Bld_1.getIdString());
      // clear versions to config item is from teamDef
      for (IAtsVersion version : AtsApiService.get().getVersionService().getVersions(teamDef)) {
         changes.deleteArtifact(AtsApiService.get().getQueryService().getArtifact(version));
      }
      changes.execute();

      AtsApiService.get().reloadServerAndClientCaches();

      Collection<Object> commitObjs =
         AtsApiService.get().getBranchService().getCommitTransactionsAndConfigItemsForTeamWf(teamArt);
      org.junit.Assert.assertEquals("commitObjs has wrong size", 1, commitObjs.size());
      assertTrue("commitObjs is missing teamDef",
         ((CommitConfigItem) commitObjs.iterator().next()).getConfigObject().equals(teamDef));
   }

   @Test
   public void testGetCommitTransactionsAndConfigItemsForTeamWf_versions() {
      AtsTestUtil.cleanupAndReset(
         AtsBranchServiceImplTest.class.getSimpleName() + ".testGetCommitTransactionsAndConfigItemsForTeamWf_versions");
      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();
      IAtsChangeSet changes =
         AtsApiService.get().createChangeSet("testGetCommitTransactionsAndConfigItemsForTeamWf_versions");

      //Test Version-based Team Arts
      IAtsVersion version1 = AtsTestUtil.getVerArt1();
      IAtsVersion version2 = AtsTestUtil.getVerArt2();

      changes.setRelation(AtsApiService.get().getQueryService().getArtifact(version1),
         AtsRelationTypes.ParallelVersion_Child, AtsApiService.get().getQueryService().getArtifact(version2));
      AtsApiService.get().getVersionService().setTargetedVersion(teamArt, version1, changes);
      changes.execute();

      Collection<Object> commitObjs =
         AtsApiService.get().getBranchService().getCommitTransactionsAndConfigItemsForTeamWf(teamArt);
      assertTrue("commitObjs has wrong size", commitObjs.size() == 2);
      assertTrue("commitObjs is missing verArt1",
         commitObjs.contains(new CommitConfigItem(version1, AtsApiService.get())));
      assertTrue("commitObjs is missing verArt2",
         commitObjs.contains(new CommitConfigItem(version2, AtsApiService.get())));
   }

   @Test
   public void testGetCommitTransactionsAndConfigItemsForTeam_txRecords() {
      AtsTestUtil.cleanupAndReset(
         AtsBranchServiceImplTest.class.getSimpleName() + ".testGetCommitTransactionsAndConfigItemsForTeam_txRecords");

      TeamWorkFlowArtifact teamArt = AtsTestUtil.getTeamWf();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      IAtsTeamDefinition teamDef = teamArt.getTeamDefinition();
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.BaselineBranchId, SAW_Bld_1.getIdString());
      // clear versions to config item is from teamDef
      for (IAtsVersion version : AtsApiService.get().getVersionService().getVersions(teamDef)) {
         changes.deleteArtifact(AtsApiService.get().getQueryService().getArtifact(version));
      }
      changes.execute();

      AtsApiService.get().reloadServerAndClientCaches();

      //Test TxRecords
      TransactionRecord txRecord = new TransactionRecord(1234L, SAW_Bld_1, "comment", new Date(0),
         UserManager.getUser(), UserManager.getUser().getArtId(), TransactionDetailsType.Baselined, 0L);
      Collection<TransactionRecord> commitTxs = new ArrayList<>();
      Collection<CommitConfigItem> configItems = new HashSet<>();
      commitTxs.add(txRecord);
      Collection<Object> commitObjs =
         AtsApiService.get().getBranchService().combineCommitTransactionsAndConfigItems(configItems, commitTxs);
      assertTrue("commitObjs has wrong size", commitObjs.size() == 1);

      Collection<CommitConfigItem> configItemsConfiguredToCommitTo =
         AtsApiService.get().getBranchService().getConfigArtifactsConfiguredToCommitTo(teamArt);
      configItems.add(configItemsConfiguredToCommitTo.iterator().next());
      commitObjs =
         AtsApiService.get().getBranchService().combineCommitTransactionsAndConfigItems(configItems, commitTxs);
      assertTrue("commitObjs has wrong size", commitObjs.size() == 1);
      assertTrue("commitObjs has wrong object",
         ((CommitConfigItem) commitObjs.iterator().next()).getConfigObject().equals(teamDef));
   }
}
