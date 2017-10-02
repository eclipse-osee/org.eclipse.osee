/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.PurgeTransactionEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation.PurgeTransactionListener;

public class PurgeTransactionOperationWithListener {

   public static IOperation getPurgeTransactionOperation(List<? extends TransactionId> transactions) {
      List<Long> txIdsToDelete = new ArrayList<>();
      for (TransactionId record : transactions) {
         txIdsToDelete.add(record.getId());
      }
      return getPurgeTransactionOperationById(txIdsToDelete);
   }

   public static IOperation getPurgeTransactionOperation(TransactionId txIdsToDelete) {
      return getPurgeTransactionOperation(Arrays.asList(txIdsToDelete));
   }

   public static IOperation getPurgeTransactionOperationById(List<Long> txIdsToDelete) {

      Collection<TransactionRecord> changedTransactions = new ArrayList<>();
      for (Long txId : txIdsToDelete) {
         changedTransactions.add(TransactionManager.getTransaction(TransactionId.valueOf(txId)));
      }
      Pair<TransactionEvent, Map<String, Long>> transEventAndIds =
         PurgeTransactionEventUtil.createPurgeTransactionEvent(changedTransactions);

      final PurgeTransactionOperation op = new PurgeTransactionOperation(txIdsToDelete, transEventAndIds);

      PurgeTransactionListener listener = new PurgeTransactionListener() {

         @Override
         public void onPurgeTransactionSuccess(List<Long> transactions, Pair<TransactionEvent, Map<String, Long>> transEventAndIds) {
            try {
               OseeEventManager.kickTransactionEvent(op, transEventAndIds.getFirst());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error sending purge transaction events", ex);
            }
            try {
               String message = String.format("Purge Transaction [%s] - Guid to Id Map [%s]",
                  transEventAndIds.getFirst().toString(), transEventAndIds.getSecond().toString());
               ServiceUtil.getOseeClient().getActivityLogEndpoint().createEntry(CoreActivityTypes.PURGE_TRANSACTION, 0L,
                  ActivityLog.COMPLETE_STATUS, message);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error activity logging purge transaction", ex);
            }
         }
      };
      op.addListener(listener);
      return op;
   }
}
