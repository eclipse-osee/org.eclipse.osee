/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts.assertThatEquals;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import java.util.Collection;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.Asserts;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperationWithListener;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class PurgeTransactionTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String[] TABLES =
      new String[] {"osee_attribute", "osee_artifact", "osee_relation_link", "osee_tx_details", "osee_txs"};

   private Collection<Artifact> softArts;
   private final TransactionEndpoint txEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();

   @Before
   @After
   public void cleanup()  {
      txEndpoint.purgeUnusedBackingDataAndTransactions();
   }

   @Test
   public void testPurgeTransaction() throws Exception {
      ElapsedTime time = new ElapsedTime("testPurgeTransaction", true);
      Map<String, Integer> initialRowCount = TestUtil.getTableRowCounts(TABLES);
      TransactionId createTxId = createArtifacts();

      Map<String, Integer> preModifyCount = TestUtil.getTableRowCounts(TABLES);
      int initialTxCurrents = getCurrentRows(createTxId);

      purge(modifyArtifacts());
      assertThatEquals(preModifyCount, TestUtil.getTableRowCounts(TABLES));
      assertEquals("Purge Transaction did not correctly update tx_current.", initialTxCurrents,
         getCurrentRows(createTxId));

      purge(createTxId);
      assertThatEquals(initialRowCount, TestUtil.getTableRowCounts(TABLES));
      time.end(Units.MIN);
      time.end(Units.SEC);
   }

   private TransactionId createArtifacts() throws Exception {
      SkynetTransaction createTransaction = TransactionManager.createTransaction(SAW_Bld_2, "Purge Transaction Test");
      softArts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirement, 10, getClass().getSimpleName(),
         SAW_Bld_2);
      for (Artifact softArt : softArts) {
         softArt.persist(createTransaction);
      }
      return createTransaction.execute();
   }

   private TransactionId modifyArtifacts() throws Exception {
      SkynetTransaction modifyTransaction = TransactionManager.createTransaction(SAW_Bld_2, "Purge Transaction Test");
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, getClass().getSimpleName());
         softArt.persist(modifyTransaction);
      }
      return modifyTransaction.execute();
   }

   private void purge(TransactionId transactionId) throws Exception {
      IOperation operation = PurgeTransactionOperationWithListener.getPurgeTransactionOperation(transactionId);
      Asserts.assertOperation(operation, IStatus.OK);
      txEndpoint.purgeUnusedBackingDataAndTransactions();
   }

   private int getCurrentRows(TransactionId createTxId)  {
      final String query = "select count(*) from osee_txs where transaction_id=? and tx_current=1";
      return ConnectionHandler.getJdbcClient().fetch(-1, query, createTxId);
   }
}
