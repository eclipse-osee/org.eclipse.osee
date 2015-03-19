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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.DeleteTransaction;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractOperation {

   private final List<Integer> txIdsToDelete;
   public static interface PurgeTransactionListener {
      void onPurgeTransactionSuccess(Collection<TransactionRecord> transactions);
   }

   private final Set<PurgeTransactionListener> listeners = new CopyOnWriteArraySet<PurgeTransactionListener>();
   private final Collection<TransactionRecord> changedTransactions = new ArrayList<TransactionRecord>();

   public PurgeTransactionOperation(List<Integer> txIdsToDelete) {
      super("Purge transactions " + txIdsToDelete, Activator.PLUGIN_ID, NullOperationLogger.getSingleton());
      this.txIdsToDelete = txIdsToDelete;
   }

   public void addListener(PurgeTransactionListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   public void removeListener(PurgeTransactionListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeClient client = ServiceUtil.getOseeClient();
      TransactionEndpoint txEndpoint = client.getTransactionEndpoint();

      DeleteTransaction deleteTxs = new DeleteTransaction();
      deleteTxs.setTransactions(txIdsToDelete);
      try {
         Response result = txEndpoint.deleteTxs(deleteTxs);
         if (Status.OK.getStatusCode() == result.getStatus()) {
            for (PurgeTransactionListener listener : listeners) {
               listener.onPurgeTransactionSuccess(changedTransactions);
            }
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}