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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Static utility for broadcasting SSE events to connected web clients.
 * Serializes domain-specific message objects and delegates to {@link OseeSseEndpoint}.
 * <p>
 * Freshness model:
 * <ul>
 *   <li><b>Artifact changes:</b> Use {@code transactionId} as the authoritative
 *       freshness key. It is globally monotonic (DB-allocated) and definitively
 *       orders all transaction-linked changes across all servers.</li>
 *   <li><b>Branch changes:</b> Notification-only -- the client performs a GET to
 *       retrieve current state. No push optimization needed (infrequent, cheap).</li>
 * </ul>
 */
public final class SseBroadcastService {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   private SseBroadcastService() {
      // static utility
   }

   /**
    * Broadcasts an artifact change to <b>all</b> connected SSE clients -- including the originating
    * connection. There is no server-side exclusion: the originating client recognizes its own echo
    * by matching the {@code originId} carried on the message and ignores it (self-dedup). Everyone
    * else fetches current state via GET.
    *
    * @param branchId the branch where changes occurred
    * @param artifactIds the artifact IDs that were affected
    * @param transactionId the committed transaction ID
    * @param userId the user who made the change
    * @param changeTypes what types of changes occurred
    * @param associatedUsers user references from the changed attributes, grouped by attribute
    * type (see {@link ArtifactChangeMessage.AssociatedUsers}); lets views decide relevance
    * client-side. May be null/empty.
    * @param changedAttributeTypeIds distinct attribute type ids changed in the transaction; lets
    * clients do targeted refreshes (e.g. only on a Name change). May be null/empty.
    * @param originId the client-minted origin id of the initiating tab (null for
    * desktop/internal-originated changes); echoed on the message for client self-dedup
    */
   public static void broadcastArtifactChange(String branchId, Collection<String> artifactIds, String transactionId,
      String userId, List<String> changeTypes, List<ArtifactChangeMessage.AssociatedUsers> associatedUsers,
      List<String> changedAttributeTypeIds, String originId) {
      try {
         ArtifactChangeMessage message = new ArtifactChangeMessage(branchId, artifactIds, transactionId, userId,
            changeTypes, associatedUsers, changedAttributeTypeIds, originId);
         String json = MAPPER.writeValueAsString(message);
         OseeSseEndpoint.broadcast("artifactChanged", json);
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to broadcast artifact change event: %s", ex.getMessage());
      }
   }

   /**
    * Broadcasts a branch metadata change to all connected SSE clients.
    * Branch changes are always broadcast to all clients (no self-exclusion needed
    * since the originator's UI also needs to react to branch state changes).
    * <p>
    * This is a notification-only event -- the client performs a GET to retrieve
    * current branch state. No data is pushed because branch metadata lacks an
    * authoritative ordering key.
    *
    * @param branchId the branch that was modified
    * @param changeType the kind of branch change
    * @param userId the user who made the change
    */
   public static void broadcastBranchChange(String branchId, WebBranchChangeType changeType, String userId) {
      broadcastBranchChange(branchId, changeType, userId, null);
   }

   /**
    * Relays a web-originated branch change to desktop clients over ActiveMQ. Registered
    * by {@link ActiveMqSseBridge} (which owns the messaging service); null when the bridge
    * is inactive. Kept as a hook so this bundle's SSE utility has no direct JMS dependency.
    */
   public interface BranchChangeRelay {
      void relayBranchChange(String branchId, String changeType, String userId);
   }

   private static volatile BranchChangeRelay branchChangeRelay;

   public static void setBranchChangeRelay(BranchChangeRelay relay) {
      branchChangeRelay = relay;
   }

   /**
    * Relays a web-originated branch change to peer web servers over the server-to-server bus.
    * Registered by {@link ServerToServerEventPublisher}; null when it is inactive. Separate from
    * {@link BranchChangeRelay} (desktop): this is the web-cluster path, so peer servers notify
    * their own web clients. Kept as a hook so this utility has no direct JMS dependency.
    */
   public interface CrossServerBranchRelay {
      void relayBranchChange(String branchId, String changeType, String userId, String originId,
         String associatedArtifactId);

      void relayBranchRebaselined(String oldBranchId, String newBranchId, String userId, String originId);
   }

   private static volatile CrossServerBranchRelay crossServerBranchRelay;

   public static void setCrossServerBranchRelay(CrossServerBranchRelay relay) {
      crossServerBranchRelay = relay;
   }

   /**
    * Broadcasts a branch change that originated on a remote (desktop) client to SSE web
    * clients only. Does NOT relay back to ActiveMQ -- the change is already on the bus, so
    * relaying would echo it to other desktop clients (and back to this server).
    */
   public static void broadcastBranchChangeFromRemoteClient(String branchId, String changeType, String userId) {
      broadcastBranchChangeToSse(branchId, changeType, userId, null);
   }

   /**
    * Broadcasts a branch change relayed from a peer web server (cross-server bus) to local SSE
    * clients only. Preserves {@code originId} so a client whose originating tab is connected to a
    * different server still recognizes and ignores its own echo. Does NOT re-relay (no loop).
    */
   public static void broadcastBranchChangeFromPeerServer(String branchId, String changeType, String userId,
      String originId, String associatedArtifactId) {
      broadcastBranchChangeToSse(branchId, changeType, userId, originId, associatedArtifactId);
   }

   /**
    * Broadcasts a rebaselined branch swap relayed from a peer web server to local SSE clients only.
    * Preserves {@code originId}; does NOT re-relay (no loop).
    */
   public static void broadcastBranchRebaselinedFromPeerServer(String oldBranchId, String newBranchId, String userId,
      String originId) {
      try {
         BranchChangeMessage message =
            new BranchChangeMessage(oldBranchId, WebBranchChangeType.REBASELINED.getWebValue(), userId, originId,
               newBranchId);
         OseeSseEndpoint.broadcast("branchChanged", MAPPER.writeValueAsString(message));
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to broadcast peer branch rebaselined event: %s", ex.getMessage());
      }
   }

   /**
    * Broadcasts a branch metadata change to <b>all</b> connected SSE clients. No server-side
    * exclusion -- the originating client recognizes its own echo by {@code originId} and ignores it.
    *
    * @param branchId the branch that was modified
    * @param changeType the kind of branch change
    * @param userId the user who made the change
    * @param originId the client-minted origin id of the initiating tab (null for
    * desktop/internal-originated changes); echoed on the message for client self-dedup
    */
   public static void broadcastBranchChange(String branchId, WebBranchChangeType changeType, String userId,
      String originId) {
      broadcastBranchChange(branchId, changeType, userId, originId, null);
   }

   /**
    * As above, additionally carrying the branch's associated artifact id on the SSE message so a
    * consumer that doesn't track the branch by id (e.g. an ATS workflow editor awaiting a branch
    * created for it) can match. Null when not available. The desktop/S2S relays are notification-
    * only and do not carry it.
    */
   public static void broadcastBranchChange(String branchId, WebBranchChangeType changeType, String userId,
      String originId, String associatedArtifactId) {
      String changeTypeValue = changeType.getWebValue();

      broadcastBranchChangeToSse(branchId, changeTypeValue, userId, originId, associatedArtifactId);

      // Fan out web-originated branch changes to desktop clients over ActiveMQ.
      BranchChangeRelay relay = branchChangeRelay;
      if (relay != null) {
         try {
            relay.relayBranchChange(branchId, changeTypeValue, userId);
         } catch (Exception ex) {
            OseeLog.logf(SseBroadcastService.class, Level.WARNING,
               "Failed to relay branch change to desktop clients: %s", ex.getMessage());
         }
      }

      // Fan out to peer web servers so their web clients are notified (web-cluster bus).
      CrossServerBranchRelay s2s = crossServerBranchRelay;
      if (s2s != null) {
         try {
            s2s.relayBranchChange(branchId, changeTypeValue, userId, originId, associatedArtifactId);
         } catch (Exception ex) {
            OseeLog.logf(SseBroadcastService.class, Level.WARNING,
               "Failed to relay branch change to peer servers: %s", ex.getMessage());
         }
      }
   }

   /**
    * Parses the {@code associatedUsers} JSON (as produced for the transaction commit topic /
    * carried on ActiveMQ) into typed message objects. Returns null on empty/invalid input.
    */
   @SuppressWarnings("unchecked")
   public static List<ArtifactChangeMessage.AssociatedUsers> parseAssociatedUsers(String json) {
      if (json == null || json.isEmpty() || json.equals("[]")) {
         return null;
      }
      try {
         List<Map<String, Object>> parsed = MAPPER.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
         List<ArtifactChangeMessage.AssociatedUsers> result = new ArrayList<>();
         for (Map<String, Object> group : parsed) {
            Object typeIdValue = group.get("typeId");
            // A group with no typeId is malformed; skip it rather than emitting the literal
            // string "null" (what String.valueOf(null) yields) as a bogus typeId downstream.
            if (typeIdValue == null) {
               continue;
            }
            String typeId = typeIdValue.toString();
            String encoding = (String) group.get("encoding");
            List<String> userIds = new ArrayList<>();
            Object ids = group.get("userIds");
            if (ids instanceof List) {
               for (Object id : (List<Object>) ids) {
                  if (id != null) {
                     userIds.add(id.toString());
                  }
               }
            }
            result.add(new ArtifactChangeMessage.AssociatedUsers(typeId, encoding, userIds));
         }
         return result.isEmpty() ? null : result;
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to parse associatedUsers JSON: %s", ex.getMessage());
         return null;
      }
   }

   /**
    * Parses the {@code changedAttributeTypeIds} JSON (a JSON array of id strings, as produced for
    * the transaction commit topic / carried on ActiveMQ) into a list. Returns null on empty/invalid
    * input.
    */
   public static List<String> parseChangedAttributeTypeIds(String json) {
      if (json == null || json.isEmpty() || json.equals("[]")) {
         return null;
      }
      try {
         List<String> result = MAPPER.readValue(json, new TypeReference<List<String>>() {});
         return result.isEmpty() ? null : result;
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to parse changedAttributeTypeIds JSON: %s", ex.getMessage());
         return null;
      }
   }

   /**
    * Broadcasts a {@code rebaselined} branch change to <b>all</b> connected SSE clients. Update-
    * from-parent retires {@code oldBranchId} (deleted/rebaselined) and makes {@code newBranchId}
    * the working branch going forward. Consumers keyed on the old branch id use {@code newBranchId}
    * to follow the swap.
    * <p>
    * SSE-only (not relayed to ActiveMQ): desktop clients derive the swap from the granular branch
    * events the operation already produces (rename/state/delete/commit), so a synthetic rebaselined
    * relay would double-notify them.
    *
    * @param oldBranchId the retired working branch id (the match key for existing consumers)
    * @param newBranchId the branch id going forward
    * @param userId the user who performed the update
    * @param originId the initiating tab's origin id (null for internal); for client self-dedup
    */
   public static void broadcastBranchRebaselined(String oldBranchId, String newBranchId, String userId,
      String originId) {
      try {
         BranchChangeMessage message =
            new BranchChangeMessage(oldBranchId, WebBranchChangeType.REBASELINED.getWebValue(), userId, originId,
               newBranchId);
         String json = MAPPER.writeValueAsString(message);
         OseeSseEndpoint.broadcast("branchChanged", json);
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to broadcast branch rebaselined event: %s", ex.getMessage());
      }

      // Fan out to peer web servers so their web clients follow the swap (web-cluster bus).
      // Not relayed to desktop -- desktop derives the swap from its own granular branch events.
      CrossServerBranchRelay s2s = crossServerBranchRelay;
      if (s2s != null) {
         try {
            s2s.relayBranchRebaselined(oldBranchId, newBranchId, userId, originId);
         } catch (Exception ex) {
            OseeLog.logf(SseBroadcastService.class, Level.WARNING,
               "Failed to relay branch rebaselined to peer servers: %s", ex.getMessage());
         }
      }
   }

   private static void broadcastBranchChangeToSse(String branchId, String changeType, String userId, String originId) {
      broadcastBranchChangeToSse(branchId, changeType, userId, originId, null);
   }

   private static void broadcastBranchChangeToSse(String branchId, String changeType, String userId, String originId,
      String associatedArtifactId) {
      try {
         BranchChangeMessage message =
            new BranchChangeMessage(branchId, changeType, userId, originId, null, associatedArtifactId);
         String json = MAPPER.writeValueAsString(message);
         OseeSseEndpoint.broadcast("branchChanged", json);
      } catch (Exception ex) {
         OseeLog.logf(SseBroadcastService.class, Level.WARNING,
            "Failed to broadcast branch change event: %s", ex.getMessage());
      }
   }
}
