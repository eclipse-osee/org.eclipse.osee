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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeTransactionEventUtil {

   private PurgeTransactionEventUtil() {
      //Utility Class
   }

   public static Pair<TransactionEvent, Map<String, Long>> createPurgeTransactionEvent(Collection<TransactionRecord> purgedTransactions) {
      TransactionEvent transactionEvent = new TransactionEvent();
      transactionEvent.setEventType(TransactionEventType.Purged);
      Map<String, Long> guidToId = new HashMap<>();
      for (TransactionRecord transId : purgedTransactions) {

         TransactionChange txChg = new TransactionChange();
         txChg.setBranch(transId.getBranch());
         txChg.setTransactionId(transId.getId().intValue());
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

   public static void handleRemotePurgeTransactionEvent(TransactionEvent transEvent) {
      if (transEvent.getEventType() == TransactionEventType.Purged) {

         Set<Artifact> artifactsInCache = new HashSet<>();
         for (TransactionChange transChange : transEvent.getTransactionChanges()) {
            for (DefaultBasicGuidArtifact guidArt : transChange.getArtifacts()) {
               try {
                  Artifact artifact = ArtifactCache.getActive(guidArt);
                  if (artifact != null) {
                     artifactsInCache.add(artifact);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }

         }

         // This will kick the artifacts reloaded event which should be handled by Applications/UIs
         if (!artifactsInCache.isEmpty()) {
            try {
               ArtifactQuery.reloadArtifacts(artifactsInCache);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }
}
