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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.ArtifactChangeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Phase 3: Receives lightweight server-to-server events from ActiveMQ.
 * <p>
 * When another server in the cluster commits a transaction, it publishes a
 * lightweight JSON event. This listener receives it and rebroadcasts it to local SSE clients, which
 * self-filter and GET current state (consistent with the same-server broadcast-to-all model).
 * <ul>
 *   <li>Ignores events from this server (echo prevention via originServerId)</li>
 *   <li>Rebroadcasts unconditionally to local sinks (the broadcast no-ops when there are none)</li>
 * </ul>
 */
@Component(immediate = true)
public class ServerToServerEventListener {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   @Reference
   private MessageService messageService;

   private volatile ConnectionNode connectionNode;
   private volatile OseeMessagingListener listener;

   @Activate
   public void activate() {
      try {
         connectionNode = messageService.getDefault();
         listener = new S2SMessagingListener();
         connectionNode.subscribe(ServerToServerMessageId.INSTANCE, listener);
         OseeLog.log(ServerToServerEventListener.class, Level.INFO,
            "ServerToServerEventListener: Subscribed to server-to-server events");
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventListener.class, Level.WARNING,
            "ServerToServerEventListener: Failed to subscribe: %s", ex.getMessage());
      }
   }

   @Deactivate
   public void deactivate() {
      try {
         if (connectionNode != null && listener != null) {
            connectionNode.unsubscribe(ServerToServerMessageId.INSTANCE, listener);
         }
      } catch (Exception ex) {
         OseeLog.logf(ServerToServerEventListener.class, Level.WARNING,
            "ServerToServerEventListener: Error during deactivation: %s", ex.getMessage());
      }
   }

   private class S2SMessagingListener extends OseeMessagingListener {

      public S2SMessagingListener() {
         super(String.class);
      }

      @Override
      public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         try {
            String json = (String) message;
            ServerToServerEvent event = MAPPER.readValue(json, ServerToServerEvent.class);

            // Ignore events from this server (echo prevention by per-JVM server id).
            if (ServerToServerEventPublisher.SERVER_ID.equals(event.getOriginServerId())) {
               return;
            }

            // Fan out to local SSE only; never re-publish (that would loop). Clients self-filter
            // and GET-on-notify; originId is preserved so a client whose tab is on a different
            // server still ignores its own echo.
            String eventType = event.getEventType();
            if (eventType == null || ServerToServerEvent.ARTIFACT_CHANGED.equals(eventType)) {
               relayArtifactChange(event);
            } else if (ServerToServerEvent.BRANCH_CHANGED.equals(eventType)) {
               SseBroadcastService.broadcastBranchChangeFromPeerServer(
                  event.getBranchId(), event.getChangeType(), event.getAuthorUserId(), event.getOriginId(),
                  event.getAssociatedArtifactId());
            } else if (ServerToServerEvent.BRANCH_REBASELINED.equals(eventType)) {
               SseBroadcastService.broadcastBranchRebaselinedFromPeerServer(
                  event.getBranchId(), event.getNewBranchId(), event.getAuthorUserId(), event.getOriginId());
            } else if (ServerToServerEvent.PRESENCE.equals(eventType)) {
               OseeSseEndpoint.applyRemotePresence(
                  event.getOriginServerId(), event.getContext(), event.getPresenceUsers());
            }
         } catch (Exception ex) {
            OseeLog.logf(ServerToServerEventListener.class, Level.WARNING,
               "Failed to process server-to-server event: %s", ex.getMessage());
         }
      }

      private void relayArtifactChange(ServerToServerEvent event) {
         String branchId = event.getBranchId();
         List<String> artifactIds = event.getArtifactIds();
         if (branchId == null || artifactIds == null || artifactIds.isEmpty()) {
            return;
         }
         List<String> changeTypes = event.getChangeTypes() != null && !event.getChangeTypes().isEmpty()
            ? event.getChangeTypes()
            : List.of(ArtifactChangeType.ATTRIBUTE_MODIFIED);
         List<ArtifactChangeMessage.AssociatedUsers> associatedUsers =
            SseBroadcastService.parseAssociatedUsers(event.getAssociatedUsersJson());
         SseBroadcastService.broadcastArtifactChange(
            branchId, artifactIds, event.getTransactionId(),
            event.getAuthorUserId(), changeTypes, associatedUsers, event.getOriginId());
      }
   }
}
