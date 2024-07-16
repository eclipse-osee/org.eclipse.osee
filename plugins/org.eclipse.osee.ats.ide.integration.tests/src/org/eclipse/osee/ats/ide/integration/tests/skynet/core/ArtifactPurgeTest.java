/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, creating artifacts, changing them and then purging them. If it works properly, all rows should be
 * equal.<br/>
 * <br/>
 * Test unit for {@link PurgeArtifacts}
 *
 * @author Donald G. Dunne
 */
public class ArtifactPurgeTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final String[] TABLES =
      new String[] {"osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs"};

   private BranchToken workingBranch;

   @Before
   public void setup() {
      workingBranch = BranchToken.create(method.getQualifiedTestName());
      BranchManager.createWorkingBranch(SAW_Bld_2, workingBranch);
   }

   @After
   public void cleanup() throws Exception {
      if (BranchManager.branchExists(workingBranch)) {
         BranchManager.purgeBranch(workingBranch);
      }
   }

   // TODO Looks like attributes and relations created after initial artifact creation are not getting purged.  Needs Fix.
   @Ignore
   @Test
   public void testPurge() throws Exception {
      //      Operations.executeWorkAndCheckStatus(new PurgeUnusedBackingDataAndTransactions(NullOperationLogger.getSingleton()));

      Map<String, Integer> initialRowCount = TestUtil.getTableRowCounts(TABLES);

      Collection<Artifact> softArts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirementMsWord, 10,
         method.getQualifiedTestName(), workingBranch);
      TransactionManager.persistInTransaction(method.getQualifiedTestName(), softArts);

      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, method.getQualifiedTestName());
         softArt.persist(method.getQualifiedTestName());
      }

      // Count rows and check that increased
      Asserts.assertThatIncreased(initialRowCount, TestUtil.getTableRowCounts(TABLES));

      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(softArts));

      // Count rows and check that same as when began
      // TODO Looks like attributes created after initial artifact creation are not getting purged.  Needs Fix.
      Asserts.assertThatEquals(initialRowCount, TestUtil.getTableRowCounts(TABLES));
   }
}
