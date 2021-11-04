/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionChange;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class TransactionRemoteEventHandler implements EventHandlerRemote<RemoteTransactionEvent1> {
   private final OrcsTokenService tokenService;

   public TransactionRemoteEventHandler(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   @Override
   public void handle(Transport transport, Sender sender, RemoteTransactionEvent1 remoteEvent) {
      TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(remoteEvent, tokenService);
      if (transEvent.getEventType() == TransactionEventType.Purged) {
         handleRemotePurgeTransactionEvent(transEvent);
         transport.send(sender, transEvent);
      } else {
         EventUtil.eventLog("REM: handleTransactionEvent - unhandled mod type [%s] ", transEvent.getEventType());
      }
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