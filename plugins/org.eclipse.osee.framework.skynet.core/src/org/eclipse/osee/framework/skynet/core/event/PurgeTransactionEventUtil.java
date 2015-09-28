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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeTransactionEventUtil {

   private PurgeTransactionEventUtil() {
      //Utility Class
   }

   public static TransactionEvent createPurgeTransactionEvent(Collection<TransactionRecord> purgedTransactions) {
      TransactionEvent transactionEvent = new TransactionEvent();
      transactionEvent.setEventType(TransactionEventType.Purged);

      return transactionEvent;
   }

   public static void handleRemotePurgeTransactionEvent(TransactionEvent transEvent) {
      if (transEvent.getEventType() == TransactionEventType.Purged) {

         Set<Artifact> artifactsInCache = new HashSet<>();
         for (TransactionChange transChange : transEvent.getTransactionChanges()) {
            try {
               TransactionManager.deCache(transChange.getTransactionId());
            } catch (OseeCoreException ex1) {
               OseeLog.log(Activator.class, Level.SEVERE, ex1);
            }

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
