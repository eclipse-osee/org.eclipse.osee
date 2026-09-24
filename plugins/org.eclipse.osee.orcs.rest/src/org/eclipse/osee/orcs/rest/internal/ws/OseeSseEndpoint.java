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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * JAX-RS SSE endpoint for real-time notifications and lease-based presence.
 * <p>
 * This class is the transport layer: it owns the SSE sink registry and the HTTP endpoints, and
 * delegates all presence state and cross-server merge to {@link PresenceRegistry}.
 */
@Path("sse")
public class OseeSseEndpoint {

   // --- SSE Connections ---

   private static final Map<Long, SseEventSink> sinks = new ConcurrentHashMap<>();
   private static final AtomicLong sinkIdGenerator = new AtomicLong(0);
   private static volatile Sse sseInstance;
   private static final ObjectMapper MAPPER = new ObjectMapper();

   /**
    * Per-JVM presence registry. Sends presence updates to the relevant local sinks via a notifier
    * that serializes the payload and delivers over SSE.
    */
   private static final PresenceRegistry PRESENCE = new PresenceRegistry(OseeSseEndpoint::notifyPresenceSinks);

   static {
      PRESENCE.start();
   }

   private final OrcsApi orcsApi;

   public OseeSseEndpoint(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   // --- SSE Connection ---

   @GET
   @Path("events")
   @Produces(MediaType.SERVER_SENT_EVENTS)
   public void subscribe(@Context SseEventSink eventSink, @Context Sse sse) {
      // The injected Sse is a JVM singleton; capture it once rather than rewriting per connection.
      if (sseInstance == null) {
         sseInstance = sse;
      }
      long sinkId = sinkIdGenerator.incrementAndGet();
      sinks.put(sinkId, eventSink);
      eventSink.send(sse.newEvent("connected", String.valueOf(sinkId)));
   }

   // --- Presence Endpoints ---

   /**
    * Heartbeat: the leader tab sends this every 15s with the authoritative set of contexts the
    * user is currently viewing across all their tabs. The registry reconciles leases and notifies.
    */
   @POST
   @Path("presence/heartbeat")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response heartbeat(PresenceHeartbeatRequest request) {
      if (request == null || request.getContexts() == null) {
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      ArtifactToken user = orcsApi.userService().getUser();
      if (!user.isValid()) {
         return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      PRESENCE.heartbeat(user.getIdString(), user.getName(), request.getSinkId(),
         Set.copyOf(request.getContexts()));
      return Response.ok().build();
   }

   /**
    * Leave: the leader tab sends this on unload (keepalive fetch) reporting its SSE connection's
    * sinkId, so presence clears promptly rather than waiting out the lease TTL -- the prompt path,
    * since the SSE stack has no server-side connection-close callback. Authenticated; removal is
    * scoped to the caller's user by {@link PresenceRegistry#dropSinkForUser}.
    */
   @POST
   @Path("presence/leave")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response leave(PresenceLeaveRequest request) {
      if (request == null || request.getSinkId() == null) {
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      ArtifactToken user = orcsApi.userService().getUser();
      if (!user.isValid()) {
         return Response.status(Response.Status.UNAUTHORIZED).build();
      }

      PRESENCE.dropSinkForUser(user.getIdString(), request.getSinkId());
      return Response.ok().build();
   }

   // --- Cross-server presence entry points (used by the server-to-server bus) --

   /** Registers/clears the cross-server presence relay. */
   public static void setCrossServerPresenceRelay(PresenceRegistry.CrossServerPresenceRelay relay) {
      PRESENCE.setCrossServerPresenceRelay(relay);
   }

   /**
    * Re-publishes this server's current presence to peers. Called when the cross-server bus
    * reconnects so peers repopulate this server's presence promptly instead of waiting on TTL.
    */
   public static void resyncPresenceToPeers() {
      PRESENCE.resyncPresenceToPeers();
   }

   /** Applies presence reported by a peer server; re-broadcasts merged presence to local clients. */
   public static void applyRemotePresence(String peerServerId, String context,
      List<ServerToServerEvent.PresenceEntry> users) {
      PRESENCE.applyRemotePresence(peerServerId, context, users);
   }

   // --- Event Broadcasting ---

   public static void broadcast(String eventName, String json) {
      Sse sse = sseInstance;
      if (sse == null || sinks.isEmpty()) {
         return;
      }

      Set<Long> deadSinks = ConcurrentHashMap.newKeySet();
      for (Map.Entry<Long, SseEventSink> entry : sinks.entrySet()) {
         SseEventSink sink = entry.getValue();
         if (sink.isClosed()) {
            deadSinks.add(entry.getKey());
         } else {
            try {
               sink.send(sse.newEvent(eventName, json));
            } catch (Exception ex) {
               deadSinks.add(entry.getKey());
            }
         }
      }

      for (Long deadId : deadSinks) {
         removeSink(deadId);
      }
   }

   /**
    * Removes a sink from the registry and drops any presence leases it held. Single cleanup path
    * for a dead/closed connection so presence for a disconnected client clears immediately rather
    * than lingering until its lease TTL lapses. Safe to call for an unknown id.
    */
   private static void removeSink(Long sinkId) {
      if (sinks.remove(sinkId) != null) {
         PRESENCE.dropSink(sinkId);
      }
   }

   public static int getConnectionCount() {
      return sinks.size();
   }

   /**
    * Delivers a presence update to a specific set of sinks. Provided to {@link PresenceRegistry} as
    * its {@link PresenceRegistry.LocalPresenceNotifier} so the presence layer stays free of SSE and
    * serialization concerns.
    */
   private static void notifyPresenceSinks(Set<Long> sinkIds, String eventName,
      PresenceRegistry.PresenceUpdate update) {
      Sse sse = sseInstance;
      if (sse == null) {
         return;
      }
      try {
         String json = MAPPER.writeValueAsString(update);
         for (Long sinkId : sinkIds) {
            SseEventSink sink = sinks.get(sinkId);
            if (sink == null) {
               continue;
            }
            if (sink.isClosed()) {
               removeSink(sinkId);
               continue;
            }
            try {
               sink.send(sse.newEvent(eventName, json));
            } catch (Exception ex) {
               // Reap immediately (same cleanup path as broadcast) rather than waiting for the
               // next full broadcast to notice this sink is dead.
               removeSink(sinkId);
            }
         }
      } catch (Exception ex) {
         OseeLog.logf(OseeSseEndpoint.class, Level.WARNING, "Failed to broadcast presence: %s", ex.getMessage());
      }
   }

   // --- Request DTOs ---

   public static class PresenceHeartbeatRequest {
      private List<String> contexts;
      private Long sinkId;

      public List<String> getContexts() {
         return contexts;
      }

      public void setContexts(List<String> contexts) {
         this.contexts = contexts;
      }

      public Long getSinkId() {
         return sinkId;
      }

      public void setSinkId(Long sinkId) {
         this.sinkId = sinkId;
      }
   }

   public static class PresenceLeaveRequest {
      private Long sinkId;

      public Long getSinkId() {
         return sinkId;
      }

      public void setSinkId(Long sinkId) {
         this.sinkId = sinkId;
      }
   }

}
