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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractOperation {

   private final List<Long> txIdsToDelete;
   public static interface PurgeTransactionListener {
      void onPurgeTransactionSuccess(List<Long> txIdsToDelete, Pair<TransactionEvent, Map<String, Long>> transEventAndIds);
   }

   private final Set<PurgeTransactionListener> listeners = new CopyOnWriteArraySet<>();
   private final Pair<TransactionEvent, Map<String, Long>> transEventAndIds;

   public PurgeTransactionOperation(List<Long> txIdsToDelete, Pair<TransactionEvent, Map<String, Long>> transEventAndIds) {
      super("Purge transactions " + txIdsToDelete, Activator.PLUGIN_ID, NullOperationLogger.getSingleton());
      this.txIdsToDelete = txIdsToDelete;
      this.transEventAndIds = transEventAndIds;
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

      String deleteTxs = Collections.toString(",", txIdsToDelete);
      try {
         Response result = txEndpoint.purgeTxs(deleteTxs);
         if (Status.OK.getStatusCode() == result.getStatus()) {
            for (PurgeTransactionListener listener : listeners) {
               listener.onPurgeTransactionSuccess(txIdsToDelete, transEventAndIds);
            }
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}