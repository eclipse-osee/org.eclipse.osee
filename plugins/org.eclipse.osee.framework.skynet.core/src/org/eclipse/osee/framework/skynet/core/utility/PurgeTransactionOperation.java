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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;

/**
 * @author Ryan D. Brooks
 */
public class PurgeTransactionOperation extends AbstractOperation {

   private final Pair<TransactionEvent, Map<String, Long>> transEventAndIds;
   private final String deleteTxs;

   public static IOperation getPurgeTransactionOperation(TransactionToken txIdsToDelete) {
      return getPurgeTransactionOperation(Arrays.asList(txIdsToDelete));
   }

   public static IOperation getPurgeTransactionOperation(List<TransactionToken> txIdsToDelete) {
      return new PurgeTransactionOperation(txIdsToDelete, createPurgeTransactionEvent(txIdsToDelete));
   }

   private PurgeTransactionOperation(List<TransactionToken> txsToDelete, Pair<TransactionEvent, Map<String, Long>> transEventAndIds) {
      super("Purge transactions " + txsToDelete, Activator.PLUGIN_ID, NullOperationLogger.getSingleton());
      deleteTxs = Collections.toString(",", txsToDelete);
      this.transEventAndIds = transEventAndIds;
   }

   private static Pair<TransactionEvent, Map<String, Long>> createPurgeTransactionEvent(Collection<TransactionToken> purgedTransactions) {
      TransactionEvent transactionEvent = new TransactionEvent();
      transactionEvent.setEventType(TransactionEventType.Purged);
      Map<String, Long> guidToId = new HashMap<>();
      for (TransactionToken transId : purgedTransactions) {

         TransactionChange txChg = new TransactionChange();
         txChg.setBranch(transId.getBranch());
         txChg.setTransactionId(transId);
         transactionEvent.getTransactionChanges().add(txChg);

         Collection<Change> changes = new ArrayList<>();
         IOperation operation = ChangeManager.comparedToPreviousTx(transId, changes);
         Operations.executeWorkAndCheckStatus(operation);
         if (!changes.isEmpty()) {
            for (Change change : changes) {
               try {
                  Artifact art = ArtifactQuery.getArtifactFromId(change.getArtId(), transId.getBranch(),
                     DeletionFlag.INCLUDE_DELETED);
                  guidToId.put(art.getGuid(), art.getId());
                  DefaultBasicGuidArtifact guidArt =
                     new DefaultBasicGuidArtifact(change.getBranch(), change.getArtifactType(), art.getGuid());
                  txChg.getArtifacts().add(guidArt);
               } catch (ArtifactDoesNotExist ex) {
                  OseeLog.log(Activator.class, Level.WARNING, ex);
               }
            }
         }
      }
      return new Pair<>(transactionEvent, guidToId);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      TransactionEndpoint txEndpoint = ServiceUtil.getOseeClient().getTransactionEndpoint();
      Response result = txEndpoint.purgeTxs(deleteTxs);

      if (Status.OK.getStatusCode() == result.getStatus()) {
         OseeEventManager.kickTransactionEvent(this, transEventAndIds.getFirst());
      }
   }
}