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

import static org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts.assertThatEquals;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import java.util.Collection;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class PurgeTransactionTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String[] TABLES = new String[] {"osee_tx_details", "osee_txs"};
   private Collection<Artifact> softArts;

   @Test
   public void testPurgeTransaction() throws Exception {
      ElapsedTime time = new ElapsedTime("test", true);
      Map<String, Integer> initialRowCount = TestUtil.getTableRowCounts(TABLES);
      TransactionToken createTxId = createArtifacts();

      Map<String, Integer> preModifyCount = TestUtil.getTableRowCounts(TABLES);
      int initialTxCurrents = getCurrentRows(createTxId);

      purge(modifyArtifacts());
      assertThatEquals(preModifyCount, TestUtil.getTableRowCounts(TABLES));
      assertEquals("Purge Transaction did not correctly update tx_current.", initialTxCurrents,
         getCurrentRows(createTxId));

      purge(createTxId);
      assertThatEquals(initialRowCount, TestUtil.getTableRowCounts(TABLES));
      time.end();
   }

   private TransactionToken createArtifacts() throws Exception {
      SkynetTransaction createTransaction = TransactionManager.createTransaction(SAW_Bld_2, "Purge Transaction Test");
      softArts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirementMsWord, 1,
         getClass().getSimpleName(), SAW_Bld_2);
      for (Artifact softArt : softArts) {
         softArt.persist(createTransaction);
      }
      return createTransaction.execute();
   }

   private TransactionToken modifyArtifacts() throws Exception {
      SkynetTransaction modifyTransaction = TransactionManager.createTransaction(SAW_Bld_2, "Purge Transaction Test");
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, getClass().getSimpleName());
         softArt.persist(modifyTransaction);
      }
      return modifyTransaction.execute();
   }

   private void purge(TransactionToken transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperation.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
   }

   private int getCurrentRows(TransactionId createTxId) {
      final String query = "select count(*) from osee_txs where branch_id=? and transaction_id=? and tx_current=1";
      return ConnectionHandler.getJdbcClient().fetch(-1, query, CoreBranches.COMMON.getIdString(), createTxId);
   }
}
