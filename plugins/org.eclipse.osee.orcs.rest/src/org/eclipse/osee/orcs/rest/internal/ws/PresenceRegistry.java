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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Lease-based presence registry for the SSE endpoint, including cross-server merge.
 * <p>
 * Owns all presence state (local leases, peer-reported presence, the expiry reaper, and the
 * cross-server relay hook) so the JAX-RS {@link OseeSseEndpoint} stays a thin transport layer. One
 * instance per JVM.
 * <p>
 * Presence model:
 * <ul>
 * <li>A client heartbeat every ~15s renews leases for the contexts it is viewing; a lease expires
 * after {@link #LEASE_TTL_MS} without renewal (crash/network loss).</li>
 * <li>Each local presence change is also published to peer servers so a client on another server in
 * the cluster is included; peer-reported presence carries its own TTL and is merged in.</li>
 * <li>Loop prevention: locally-originated changes broadcast to local sinks AND publish to peers;
 * peer-originated changes (and remote expiry) broadcast to local sinks only.</li>
 * </ul>
 * <p>
 * Context indexes ({@link #localByContext}, {@link #remoteByContext}) keep per-context lookups
 * proportional to the users in that context rather than the whole registry.
 */
public final class PresenceRegistry {

   /** Lease TTL without renewal (ms). */
   private static final long LEASE_TTL_MS = 45_000;

   /** Reaper interval (ms). */
   private static final long REAPER_INTERVAL_MS = 15_000;

   /**
    * TTL for a peer's reported presence. Longer than the heartbeat-driven republish cadence so a
    * healthy peer's entry never lapses between updates, but bounded so a dead peer clears.
    */
   private static final long REMOTE_PRESENCE_TTL_MS = 60_000;

   /** Sends a presence update to specific local SSE sinks (implemented by the transport layer). */
   public interface LocalPresenceNotifier {
      void notifySinks(Set<Long> sinkIds, String eventName, PresenceUpdate update);
   }

   /**
    * Relays this server's presence for a context to peer servers. Registered by
    * {@link ServerToServerEventPublisher}; null when it (or the cluster bus) is inactive. Kept as a
    * hook so the presence layer has no direct JMS dependency.
    */
   public interface CrossServerPresenceRelay {
      void relayPresence(String context, List<ServerToServerEvent.PresenceEntry> users);
   }

   /** Local leases keyed by "userId::context". */
   private final Map<String, PresenceLease> leases = new ConcurrentHashMap<>();
   /** Peer-reported presence keyed by "peerServerId::context". */
   private final Map<String, RemotePresence> remotePresence = new ConcurrentHashMap<>();

   /** Index: context -> local lease keys, so per-context lookups don't scan all leases. */
   private final Map<String, Set<String>> localByContext = new ConcurrentHashMap<>();
   /** Index: userId -> local lease keys, so per-user reconciliation (heartbeat/leave) doesn't scan all leases. */
   private final Map<String, Set<String>> localByUser = new ConcurrentHashMap<>();
   /** Index: context -> peer presence keys, so per-context merges don't scan all peers. */
   private final Map<String, Set<String>> remoteByContext = new ConcurrentHashMap<>();

   private final LocalPresenceNotifier notifier;
   private volatile CrossServerPresenceRelay crossServerPresenceRelay;
   private volatile ScheduledExecutorService reaper;

   public PresenceRegistry(LocalPresenceNotifier notifier) {
      this.notifier = notifier;
   }

   public void setCrossServerPresenceRelay(CrossServerPresenceRelay relay) {
      this.crossServerPresenceRelay = relay;
      // On (re)registration -- server startup or the cross-server bus (re)connecting -- push all of
      // this server's current presence to peers so they repopulate immediately instead of waiting
      // for the next per-context heartbeat republish (or missing a one-shot leave entirely). Peers
      // key our contribution by this server's stable id, so a resync overwrites cleanly.
      if (relay != null) {
         resyncPresenceToPeers();
      }
   }

   /**
    * Re-publishes this server's current presence for every context that has a local lease. Used to
    * seed/refresh peers when the relay (re)registers or the bus reconnects, since presence relays
    * are otherwise sent only on change or heartbeat and a peer that missed those (bus was down)
    * would show stale data.
    */
   public void resyncPresenceToPeers() {
      for (String context : Set.copyOf(localByContext.keySet())) {
         publishToPeers(context);
      }
   }

   public synchronized void start() {
      if (reaper == null) {
         reaper = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sse-presence-reaper");
            t.setDaemon(true);
            return t;
         });
         reaper.scheduleAtFixedRate(this::reap, REAPER_INTERVAL_MS, REAPER_INTERVAL_MS, TimeUnit.MILLISECONDS);
      }
   }

   public synchronized void stop() {
      if (reaper != null) {
         reaper.shutdownNow();
         reaper = null;
      }
   }

   // --- Mutations ---

   /**
    * Reconciles a user's leases against the contexts they are currently viewing: renews/creates
    * leases for {@code contexts}, removes their leases for any other context. Broadcasts changed
    * contexts (local + peers) and re-publishes unchanged ones to peers so a steady viewer's peer
    * TTL never lapses.
    */
   public void heartbeat(String userId, String userName, Long sinkId, Set<String> contexts) {
      long expiresAt = System.currentTimeMillis() + LEASE_TTL_MS;
      Set<String> contextsToNotify = new LinkedHashSet<>();

      for (String context : contexts) {
         String leaseKey = leaseKey(userId, context);
         PresenceLease existing = leases.get(leaseKey);
         // Notify on fresh presence, or when the user reports the context from a different SSE
         // connection (sinkId changed) -- a reconnect/handoff. Re-broadcasting there gives the
         // (re)joining connection the current roster and re-targets delivery to the live sink.
         boolean changed = existing == null || !sameSink(existing.getSinkId(), sinkId);
         leases.put(leaseKey, new PresenceLease(context, userId, userName, sinkId, expiresAt));
         indexAdd(localByContext, context, leaseKey);
         indexAdd(localByUser, userId, leaseKey);
         if (changed) {
            contextsToNotify.add(context);
         }
      }

      // Remove this user's leases for contexts they are no longer viewing. Snapshot the user's
      // lease keys first, since removeLease mutates the same index we would iterate.
      for (String leaseKey : Set.copyOf(localByUser.getOrDefault(userId, Set.of()))) {
         PresenceLease lease = leases.get(leaseKey);
         if (lease != null && !contexts.contains(lease.getContext())) {
            removeLease(leaseKey, lease.getContext(), userId);
            contextsToNotify.add(lease.getContext());
         }
      }

      for (String context : contextsToNotify) {
         broadcastLocallyAndToPeers(context);
      }
      // Renew peer TTL for unchanged contexts (publish only; local clients need no re-render).
      for (String context : contexts) {
         if (!contextsToNotify.contains(context)) {
            publishToPeers(context);
         }
      }
   }

   /**
    * Drops all leases held by a specific SSE sink. Called by the transport layer as soon as a sink
    * is found closed, so presence for a disconnected connection clears immediately instead of
    * lingering until its lease TTL lapses. A user with other live sinks keeps presence via those.
    */
   public void dropSink(Long sinkId) {
      if (sinkId == null) {
         return;
      }
      Set<String> affected = new LinkedHashSet<>();
      // Snapshot: removeLease mutates the maps being scanned.
      for (PresenceLease lease : Set.copyOf(leases.values())) {
         if (sinkId.equals(lease.getSinkId())) {
            removeLease(leaseKey(lease.getUserId(), lease.getContext()), lease.getContext(),
               lease.getUserId());
            affected.add(lease.getContext());
         }
      }
      for (String context : affected) {
         broadcastLocallyAndToPeers(context);
      }
   }

   /**
    * Drops the leases on {@code sinkId} that are owned by {@code userId}, and broadcasts the
    * affected contexts. Scoping by the authenticated userId means a client reporting a guessed
    * sinkId cannot evict another user; a non-matching pair is a harmless no-op.
    */
   public void dropSinkForUser(String userId, Long sinkId) {
      if (userId == null || sinkId == null) {
         return;
      }
      Set<String> affected = new LinkedHashSet<>();
      // Snapshot the user's lease keys before iterating, since removeLease mutates that index.
      for (String leaseKey : Set.copyOf(localByUser.getOrDefault(userId, Set.of()))) {
         PresenceLease lease = leases.get(leaseKey);
         if (lease != null && sinkId.equals(lease.getSinkId())) {
            removeLease(leaseKey, lease.getContext(), userId);
            affected.add(lease.getContext());
         }
      }
      for (String context : affected) {
         broadcastLocallyAndToPeers(context);
      }
   }

   /**
    * Applies presence reported by a peer server: replaces that peer's user set for the context,
    * then re-broadcasts the merged presence to local clients. Does NOT re-publish to peers (no
    * loop). An empty/null user list clears the peer's contribution.
    */
   public void applyRemotePresence(String peerServerId, String context,
      List<ServerToServerEvent.PresenceEntry> users) {
      if (peerServerId == null || context == null) {
         return;
      }
      String key = remoteKey(peerServerId, context);
      if (users == null || users.isEmpty()) {
         if (remotePresence.remove(key) != null) {
            indexRemove(remoteByContext, context, key);
         }
      } else {
         List<PresenceUser> mapped = users.stream().map(
            u -> new PresenceUser(u.getUserId(), u.getUserName())).collect(Collectors.toList());
         remotePresence.put(key,
            new RemotePresence(key, context, mapped, System.currentTimeMillis() + REMOTE_PRESENCE_TTL_MS));
         indexAdd(remoteByContext, context, key);
      }
      broadcastToLocalSinks(context);
   }

   // --- Reaper ---

   private void reap() {
      long now = System.currentTimeMillis();
      Set<String> localAffected = new LinkedHashSet<>();
      for (PresenceLease lease : leases.values()) {
         if (lease.getExpiresAt() < now) {
            removeLease(leaseKey(lease.getUserId(), lease.getContext()), lease.getContext(), lease.getUserId());
            localAffected.add(lease.getContext());
         }
      }

      Set<String> remoteAffected = new LinkedHashSet<>();
      for (RemotePresence remote : remotePresence.values()) {
         if (remote.getExpiresAt() < now) {
            removeRemote(remote.getKey(), remote.getContext());
            remoteAffected.add(remote.getContext());
         }
      }

      for (String context : localAffected) {
         broadcastLocallyAndToPeers(context);
      }
      // Remote-only expiry: refresh local clients but do not re-publish to peers (no loop).
      for (String context : remoteAffected) {
         if (!localAffected.contains(context)) {
            broadcastToLocalSinks(context);
         }
      }
   }

   // --- Broadcast / publish ---

   /**
    * Broadcasts a context's merged presence to local SSE clients AND publishes this server's own
    * users to peer servers. For locally-originated changes (heartbeat, leave, local expiry).
    */
   private void broadcastLocallyAndToPeers(String context) {
      publishToPeers(context);
      broadcastToLocalSinks(context);
   }

   /**
    * Broadcasts a context's merged presence to local SSE clients only -- does NOT publish to peers.
    * For peer-originated changes and remote expiry, to avoid a cross-server publish loop.
    */
   private void broadcastToLocalSinks(String context) {
      List<PresenceUser> users = getUsersForContext(context);
      Set<Long> targetSinks = new LinkedHashSet<>();
      for (String leaseKey : localByContext.getOrDefault(context, Set.of())) {
         PresenceLease lease = leases.get(leaseKey);
         if (lease != null && lease.getSinkId() != null) {
            targetSinks.add(lease.getSinkId());
         }
      }
      if (!targetSinks.isEmpty()) {
         notifier.notifySinks(targetSinks, "presenceUpdate", new PresenceUpdate(context, users));
      }
   }

   /** Publishes this server's own users for a context to peer servers via the cross-server relay. */
   private void publishToPeers(String context) {
      CrossServerPresenceRelay relay = crossServerPresenceRelay;
      if (relay == null) {
         return;
      }
      List<ServerToServerEvent.PresenceEntry> users = getLocalUsersForContext(context).stream().map(
         u -> new ServerToServerEvent.PresenceEntry(u.getUserId(), u.getUserName())).collect(Collectors.toList());
      relay.relayPresence(context, users);
   }

   // --- Queries ---

   /** This server's own users in a context (local leases only), deduped by user id. */
   private List<PresenceUser> getLocalUsersForContext(String context) {
      Set<PresenceUser> users = new LinkedHashSet<>();
      for (String leaseKey : localByContext.getOrDefault(context, Set.of())) {
         PresenceLease lease = leases.get(leaseKey);
         if (lease != null) {
            users.add(new PresenceUser(lease.getUserId(), lease.getUserName()));
         }
      }
      return List.copyOf(users);
   }

   /**
    * All users in a context: local leases merged with every non-expired peer's reported presence,
    * deduped by user id ({@link PresenceUser#equals}).
    */
   private List<PresenceUser> getUsersForContext(String context) {
      long now = System.currentTimeMillis();
      Set<PresenceUser> users = new LinkedHashSet<>(getLocalUsersForContext(context));
      for (String key : remoteByContext.getOrDefault(context, Set.of())) {
         RemotePresence remote = remotePresence.get(key);
         if (remote != null && remote.getExpiresAt() >= now) {
            users.addAll(remote.getUsers());
         }
      }
      return List.copyOf(users);
   }

   // --- Index / key helpers ---

   private void removeLease(String leaseKey, String context, String userId) {
      leases.remove(leaseKey);
      indexRemove(localByContext, context, leaseKey);
      indexRemove(localByUser, userId, leaseKey);
   }

   private void removeRemote(String key, String context) {
      remotePresence.remove(key);
      indexRemove(remoteByContext, context, key);
   }

   private static void indexAdd(Map<String, Set<String>> index, String outerKey, String memberKey) {
      index.computeIfAbsent(outerKey, k -> ConcurrentHashMap.newKeySet()).add(memberKey);
   }

   private static void indexRemove(Map<String, Set<String>> index, String outerKey, String memberKey) {
      index.computeIfPresent(outerKey, (k, members) -> {
         members.remove(memberKey);
         return members.isEmpty() ? null : members;
      });
   }

   /** Null-safe sinkId equality: treats two null sinkIds as the same connection. */
   private static boolean sameSink(Long a, Long b) {
      return a == null ? b == null : a.equals(b);
   }

   private static String leaseKey(String userId, String context) {
      return userId + "::" + context;
   }

   private static String remoteKey(String peerServerId, String context) {
      return peerServerId + "::" + context;
   }

   // --- Inner types ---

   private static final class PresenceLease {
      private final String context;
      private final String userId;
      private final String userName;
      private final Long sinkId;
      private final long expiresAt;

      PresenceLease(String context, String userId, String userName, Long sinkId, long expiresAt) {
         this.context = context;
         this.userId = userId;
         this.userName = userName;
         this.sinkId = sinkId;
         this.expiresAt = expiresAt;
      }

      String getContext() {
         return context;
      }

      String getUserId() {
         return userId;
      }

      String getUserName() {
         return userName;
      }

      Long getSinkId() {
         return sinkId;
      }

      long getExpiresAt() {
         return expiresAt;
      }
   }

   /** A peer server's reported users for a context, with a TTL for reaping a silent peer. */
   private static final class RemotePresence {
      private final String key;
      private final String context;
      private final List<PresenceUser> users;
      private final long expiresAt;

      RemotePresence(String key, String context, List<PresenceUser> users, long expiresAt) {
         this.key = key;
         this.context = context;
         this.users = users;
         this.expiresAt = expiresAt;
      }

      String getKey() {
         return key;
      }

      String getContext() {
         return context;
      }

      List<PresenceUser> getUsers() {
         return users;
      }

      long getExpiresAt() {
         return expiresAt;
      }
   }

   /** A single present user (id + display name). Equality/hash by user id for cross-tab dedup. */
   public static final class PresenceUser {
      private String userId;
      private String userName;

      public PresenceUser() {
      }

      public PresenceUser(String userId, String userName) {
         this.userId = userId;
         this.userName = userName;
      }

      public String getUserId() {
         return userId;
      }

      public void setUserId(String userId) {
         this.userId = userId;
      }

      public String getUserName() {
         return userName;
      }

      public void setUserName(String userName) {
         this.userName = userName;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }
         PresenceUser that = (PresenceUser) o;
         return userId != null && userId.equals(that.userId);
      }

      @Override
      public int hashCode() {
         return userId != null ? userId.hashCode() : 0;
      }
   }

   /** SSE payload: the current user set for a context. Matches the client {@code presenceUpdate}. */
   public static final class PresenceUpdate {
      private String context;
      private List<PresenceUser> users;

      public PresenceUpdate() {
      }

      public PresenceUpdate(String context, List<PresenceUser> users) {
         this.context = context;
         this.users = users;
      }

      public String getContext() {
         return context;
      }

      public void setContext(String context) {
         this.context = context;
      }

      public List<PresenceUser> getUsers() {
         return users;
      }

      public void setUsers(List<PresenceUser> users) {
         this.users = users;
      }
   }
}
