/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.TransactionCommitTopic;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Publishes lightweight server-to-server events via ActiveMQ.
 * <p>
 * When a transaction commit happens on this server (via REST from a web client),
 * this handler publishes a lightweight JSON event to a dedicated ActiveMQ topic.
 * Other servers in the cluster subscribe to this topic and relay to their local
 * SSE web clients if anyone is watching the affected context.
 * <p>
 * Uses a separate topic from the desktop client's {@code RemotePersistEvent1}
 * to avoid interference with legacy JAXB-based messages.
 */
@Component(immediate = true, service = EventHandler.class, property = {
   EventConstants.EVENT_TOPIC + "=" + TransactionCommitTopic.TOPIC,
   "component.name=ServerToServerEventPublisher"})
public class ServerToServerEventPublisher implements EventHandler {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   /**
    * Identifier for this server instance -- filters echo events and keys this server's presence on
    * peers. Must be UNIQUE per instance (else co-located servers drop each other's events as their
    * own echo) and STABLE across restarts (else a restart orphans the old id's peer entries as
    * ghosts until their TTL lapses).
    */
   static final String SERVER_ID = computeServerId();

   private static String computeServerId() {
      // The HTTP port this server binds is unique per instance (e.g. 8089 vs 8090 on one host) and
      // stable across restarts. Do NOT use OseeClient.getPort(): it reads the client-facing
      // "osee.application.server" property, commonly unset on the server, so every instance would
      // share one id -- making each drop the other's cross-server events as its own echo.
      String port = System.getProperty("org.osgi.service.http.port");
      try {
         String host = InetAddress.getLocalHost().getHostName();
         if (host != null && !host.isEmpty() && port != null && !port.isEmpty()) {
            return host + ":" + port;
         }
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventPublisher.class, Level.WARNING,
            "ServerToServerEventPublisher: Could not derive stable server id (%s); using a random id.",
            ex.getMessage());
      }
      // Last resort: unique but not restart-stable (brief peer ghosts until the remote TTL).
      return UUID.randomUUID().toString();
   }

   @Reference
   private MessageService messageService;

   private volatile ConnectionNode connectionNode;

   @Activate
   public void activate() {
      try {
         connectionNode = messageService.getDefault();
         // Register as the cross-server relay so web-originated branch changes reach peer servers.
         SseBroadcastService.setCrossServerBranchRelay(new SseBroadcastService.CrossServerBranchRelay() {
            @Override
            public void relayBranchChange(String branchId, String changeType, String userId, String originId,
               String associatedArtifactId) {
               publishBranchChange(branchId, changeType, userId, originId, associatedArtifactId);
            }

            @Override
            public void relayBranchRebaselined(String oldBranchId, String newBranchId, String userId,
               String originId) {
               publishBranchRebaselined(oldBranchId, newBranchId, userId, originId);
            }
         });
         // Register as the cross-server presence relay so this server's per-context presence
         // reaches peer servers (which merge it into what they show their own clients). Registering
         // the relay also triggers an initial presence resync to peers.
         OseeSseEndpoint.setCrossServerPresenceRelay(this::publishPresence);
         // On a bus reconnect, re-publish all presence: relays sent while the bus was down were
         // lost (a one-shot leave in particular is never retried), so peers would otherwise carry
         // stale/ghost presence until the remote TTL lapses. Registered on the ConnectionNode,
         // which fires connected() immediately when already connected -- covering the initial seed.
         if (connectionNode != null) {
            connectionNode.addConnectionListener(new ConnectionListener() {
               @Override
               public void connected(ConnectionNode node) {
                  OseeSseEndpoint.resyncPresenceToPeers();
               }

               @Override
               public void notConnected(ConnectionNode node) {
                  // Publishing no-ops while disconnected; the next connected() callback resyncs.
               }
            });
         }
         OseeLog.logf(ServerToServerEventPublisher.class, Level.INFO,
            "ServerToServerEventPublisher: Activated with serverId=%s", SERVER_ID);
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventPublisher.class, Level.WARNING,
            "ServerToServerEventPublisher: Failed to get default connection node: %s", ex.getMessage());
      }
   }

   @Deactivate
   public void deactivate() {
      SseBroadcastService.setCrossServerBranchRelay(null);
      OseeSseEndpoint.setCrossServerPresenceRelay(null);
   }

   @Override
   public void handleEvent(Event event) {
      try {
         String branchId = (String) event.getProperty(TransactionCommitTopic.BRANCH_ID);
         String transactionId = (String) event.getProperty(TransactionCommitTopic.TRANSACTION_ID);
         String authorUserId = (String) event.getProperty(TransactionCommitTopic.AUTHOR_USER_ID);
         String[] artifactIdsArray = (String[]) event.getProperty(TransactionCommitTopic.ARTIFACT_IDS);

         if (branchId == null || transactionId == null || artifactIdsArray == null || artifactIdsArray.length == 0) {
            return;
         }

         List<String> artifactIds = Arrays.asList(artifactIdsArray);
         String associatedUsersJson = (String) event.getProperty(TransactionCommitTopic.ASSOCIATED_USERS);
         String originId = (String) event.getProperty(TransactionCommitTopic.ORIGIN_ID);
         String[] changeTypesArray = (String[]) event.getProperty(TransactionCommitTopic.CHANGE_TYPES);
         List<String> changeTypes =
            changeTypesArray != null && changeTypesArray.length > 0 ? Arrays.asList(changeTypesArray) : null;
         List<String> changedAttributeTypeIds = SseBroadcastService.parseChangedAttributeTypeIds(
            (String) event.getProperty(TransactionCommitTopic.CHANGED_ATTRIBUTE_TYPE_IDS));

         ServerToServerEvent s2sEvent = ServerToServerEvent.artifactChanged(
            branchId, artifactIds, transactionId, authorUserId, SERVER_ID, associatedUsersJson, originId,
            changeTypes, changedAttributeTypeIds);

         publish(s2sEvent);

         OseeLog.logf(ServerToServerEventPublisher.class, Level.FINE,
            "Published server-to-server event: branch=%s, tx=%s, artifacts=%d",
            branchId, transactionId, artifactIds.size());
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventPublisher.class, Level.WARNING,
            "Failed to publish server-to-server event: %s", ex.getMessage());
      }
   }

   /**
    * Publishes a branch metadata change to peer servers so their web clients are notified.
    * Registered as the cross-server branch relay on {@link SseBroadcastService}.
    */
   void publishBranchChange(String branchId, String changeType, String userId, String originId,
      String associatedArtifactId) {
      ServerToServerEvent s2sEvent =
         ServerToServerEvent.branchChanged(branchId, changeType, userId, SERVER_ID, originId, associatedArtifactId);
      publish(s2sEvent);
   }

   /**
    * Publishes an update-from-parent branch swap to peer servers so their web clients follow it.
    */
   void publishBranchRebaselined(String oldBranchId, String newBranchId, String userId, String originId) {
      ServerToServerEvent s2sEvent = ServerToServerEvent.branchRebaselined(oldBranchId, newBranchId,
         WebBranchChangeType.REBASELINED.getWebValue(), userId, SERVER_ID, originId);
      publish(s2sEvent);
   }

   /**
    * Publishes this server's presence for a context to peer servers so they merge it into what they
    * show their own clients. Registered as the cross-server presence relay on {@link OseeSseEndpoint}.
    * An empty {@code users} list clears this server's contribution on peers (last user left).
    */
   void publishPresence(String context, List<ServerToServerEvent.PresenceEntry> users) {
      ServerToServerEvent s2sEvent = ServerToServerEvent.presence(context, SERVER_ID, users);
      publish(s2sEvent);
   }

   private void publish(ServerToServerEvent s2sEvent) {
      ConnectionNode node = this.connectionNode;
      if (node == null) {
         return;
      }
      try {
         node.send(ServerToServerMessageId.INSTANCE, MAPPER.writeValueAsString(s2sEvent));
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventPublisher.class, Level.WARNING,
            "Failed to publish server-to-server event: %s", ex.getMessage());
      }
   }
}
