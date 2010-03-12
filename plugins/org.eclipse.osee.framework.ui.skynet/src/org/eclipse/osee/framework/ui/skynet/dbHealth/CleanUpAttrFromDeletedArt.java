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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class CleanUpAttrFromDeletedArt extends DatabaseHealthOperation {
   private static final String INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION =
      "insert into osee_txs (tx_current, mod_type, transaction_id, gamma_id)  select 3, 5, tx1.transaction_id, att1.gamma_id from osee_txs tx1, osee_txs tx2, osee_arts art1, osee_attribute att1 where tx1.gamma_id = art1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and tx1.branch_id = tx2.branch_id and tx2.gamma_id = att1.gamma_id and art1.art_id = att1.art_id and tx2.tx_current = 1 and tx1.transaction_id <> tx2.transaction_id";
   private static final String UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION =
      "update osee_txs set tx_current = 0 where (transaction_id, gamma_id) in (select tx2.transaction_id, tx2.gamma_id from osee_txs tx1, osee_txs tx2, osee_arts art1, osee_attribute att1 where tx1.gamma_id = art1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and tx1.branch_id = tx2.branch_id and tx2.gamma_id = att1.gamma_id and art1.art_id = att1.art_id and tx2.tx_current = 1 and tx1.transaction_id <> tx2.transaction_id)";
   private static final String UPDATE_OLD_ATTRS_SAME_TRANSACTION =
      "update osee_txs set tx_current = 3, mod_type = 5 where (transaction_id, gamma_id) in (select tx2.transaction_id, tx2.gamma_id from osee_txs tx1, osee_txs tx2, osee_arts art1, osee_attribute att1 where tx1.gamma_id = art1.gamma_id and tx1.tx_current = 2 and tx1.mod_type = 3 and tx1.branch_id = tx2.branch_id and tx2.gamma_id = att1.gamma_id and art1.art_id = att1.art_id and tx2.tx_current = 1 and tx1.transaction_id = tx2.transaction_id)";

   public CleanUpAttrFromDeletedArt() {
      super("attributes from deleted artifacts");
   }

   @Override
   public String getVerifyTaskName() {
      return Strings.emptyString();
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      if (isFixOperationEnabled()) {
         checkForCancelledStatus(monitor);
         monitor.setTaskName("INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(INSERT_ATTRS_TO_ART_COMMIT_TRANSACTION);
         monitor.worked(calculateWork(0.45));
         monitor.setTaskName("UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(UPDATE_OLD_ATTRS_NOT_SAME_TRANSACTION);
         monitor.worked(calculateWork(0.45));
         monitor.setTaskName("UPDATE_OLD_ATTRS_SAME_TRANSACTION");
         ConnectionHandler.runPreparedUpdate(UPDATE_OLD_ATTRS_SAME_TRANSACTION);
      } else {
         checkForCancelledStatus(monitor);
         monitor.worked(calculateWork(0.90));
      }
      monitor.worked(calculateWork(0.10));
   }

   @Override
   public String getCheckDescription() {
      return "Enter Check Description Here";
   }

   @Override
   public String getFixDescription() {
      return "Enter Fix Description Here";
   }

}
