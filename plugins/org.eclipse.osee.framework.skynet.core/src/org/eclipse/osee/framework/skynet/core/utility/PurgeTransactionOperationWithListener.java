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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.PurgeTransactionEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.PurgeTransactionOperation.PurgeTransactionListener;

public class PurgeTransactionOperationWithListener {

   public static IOperation getPurgeTransactionOperation(List<? extends TransactionId> transactions) throws OseeCoreException {
      List<Long> txIdsToDelete = new ArrayList<>();
      for (TransactionId record : transactions) {
         txIdsToDelete.add(record.getId());
      }
      return getPurgeTransactionOperationById(txIdsToDelete);
   }

   public static IOperation getPurgeTransactionOperation(TransactionId txIdsToDelete) throws OseeCoreException {
      return getPurgeTransactionOperation(Arrays.asList(txIdsToDelete));
   }

   public static IOperation getPurgeTransactionOperationById(List<Long> txIdsToDelete) throws OseeCoreException {

      final PurgeTransactionOperation op = new PurgeTransactionOperation(txIdsToDelete);

      PurgeTransactionListener listener = new PurgeTransactionListener() {

         @Override
         public void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions) {
            TransactionEvent transactionEvent = PurgeTransactionEventUtil.createPurgeTransactionEvent(transactions);
            try {
               OseeEventManager.kickTransactionEvent(op, transactionEvent);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error sending purge transaction events", ex);
            }
         }
      };
      op.addListener(listener);
      return op;
   }
}
