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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts.assertThatEquals;
import static org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts.assertThatIncreased;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * This test is intended to be run against a demo database. It tests the branch purge logic by counting the rows of the
 * version and txs tables, creating a branch, making changes and then purging the branch. If it works properly, all rows
 * should be equal.
 *
 * @author Donald G. Dunne
 */
public class BranchPurgeTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final String[] TABLES =
      new String[] {"osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs"};

   private BranchToken workingBranch;
   private TransactionEndpoint txEndpoint;

   @Before
   public void setup() {
      workingBranch = BranchToken.create(method.getQualifiedTestName());
      txEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
   }

   @After
   public void cleanup() throws Exception {
      if (BranchManager.branchExists(workingBranch)) {
         BranchManager.purgeBranch(workingBranch);
      }
   }

   @Test
   public void testPurgeBranch() throws Exception {
      txEndpoint.purgeUnusedBackingDataAndTransactions();

      Map<String, Integer> initialRowCount = TestUtil.getTableRowCounts(TABLES);

      BranchToken branch = BranchManager.createWorkingBranch(SAW_Bld_2, workingBranch);
      Collection<Artifact> softArts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirementMsWord, 10,
         method.getQualifiedTestName(), branch);
      TransactionManager.persistInTransaction("Test purge branch", softArts);

      SkynetTransaction transaction = TransactionManager.createTransaction(branch, method.getQualifiedTestName());
      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, method.getQualifiedTestName());
         softArt.persist(transaction);
      }
      transaction.execute();

      // Count rows and check that increased
      assertThatIncreased(initialRowCount, TestUtil.getTableRowCounts(TABLES));

      BranchManager.purgeBranch(branch);
      txEndpoint.purgeUnusedBackingDataAndTransactions();

      // Count rows and check that same as when began
      // TODO looks like artifacts are not being removed when purge a branch
      assertThatEquals(initialRowCount, TestUtil.getTableRowCounts(TABLES));
   }
}
