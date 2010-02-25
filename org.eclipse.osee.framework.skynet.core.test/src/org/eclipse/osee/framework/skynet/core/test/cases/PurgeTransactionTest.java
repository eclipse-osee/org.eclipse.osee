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
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeTransactionOperation;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Ryan Schmitt
 */
public class PurgeTransactionTest {
   private Branch branch;
   Collection<Artifact> softArts;
   private SkynetTransaction createTransaction;
   private SkynetTransaction modifyTransaction;
   private int createId;
   private int modifyId;
   private Map<String, Integer> preCreateCount;
   private Map<String, Integer> preModifyCount;
   private Map<String, Integer> postModifyPurgeCount;
   private Map<String, Integer> postCreatePurgeCount;
   private static final List<String> tables =
         Arrays.asList("osee_attribute", "osee_arts", "osee_relation_link", "osee_tx_details", "osee_txs");

   @org.junit.Test
   public void testPurgeTransaction() throws Exception {
      init();

      createArtifacts();
      int initialTxCurrents = getCurrentRows();

      modifyArtifacts();
      purge(modifyId, postModifyPurgeCount);
      TestUtil.checkThatEqual(preModifyCount, postModifyPurgeCount);

      assertEquals("Purge Transaction did not correctly update tx_current.", initialTxCurrents, getCurrentRows());

      purge(createId, postCreatePurgeCount);
      TestUtil.checkThatEqual(preCreateCount, postCreatePurgeCount);
   }

   private void init() throws Exception {
      branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2.getName());
      preCreateCount = new HashMap<String, Integer>();
      preModifyCount = new HashMap<String, Integer>();
      postModifyPurgeCount = new HashMap<String, Integer>();
      postCreatePurgeCount = new HashMap<String, Integer>();
   }

   private void createArtifacts() throws Exception {
      DbUtil.getTableRowCounts(preCreateCount, tables);
      createTransaction = new SkynetTransaction(branch, "Purge Transaction Test");
      softArts =
            FrameworkTestUtil.createSimpleArtifacts(Requirements.SOFTWARE_REQUIREMENT, 10, getClass().getSimpleName(),
                  branch);
      for (Artifact softArt : softArts) {
         softArt.persist(createTransaction);
      }
      createId = createTransaction.getTransactionNumber();
      createTransaction.execute();
   }

   private void modifyArtifacts() throws Exception {
      DbUtil.getTableRowCounts(preModifyCount, tables);
      modifyTransaction = new SkynetTransaction(branch, "Purge Transaction Test");
      for (Artifact softArt : softArts) {
         softArt.addAttribute(StaticIdManager.STATIC_ID_ATTRIBUTE, getClass().getSimpleName());
         softArt.persist(modifyTransaction);
      }
      modifyId = modifyTransaction.getTransactionNumber();
      modifyTransaction.execute();
   }

   private void purge(int transactionId, Map<String, Integer> dbCount) throws Exception {
      IOseeDatabaseServiceProvider databaseProvider = Activator.getInstance();
      PurgeTransactionOperation purgeOp = new PurgeTransactionOperation(databaseProvider, transactionId);
      Operations.executeWorkAndCheckStatus(purgeOp, new NullProgressMonitor(), -1);
      DbUtil.getTableRowCounts(dbCount, tables);
   }

   private int getCurrentRows() throws OseeCoreException {
      final String query = "select count(*) from osee_txs where transaction_id=? and tx_current=1;";
      return ConnectionHandler.runPreparedQueryFetchInt(-1, query, createId);
   }
}
