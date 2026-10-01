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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.orcs.rest.internal.ws.PresenceRegistry.LocalPresenceNotifier;
import org.eclipse.osee.orcs.rest.internal.ws.PresenceRegistry.PresenceUpdate;
import org.eclipse.osee.orcs.rest.internal.ws.PresenceRegistry.PresenceUser;
import org.eclipse.osee.orcs.rest.internal.ws.ServerToServerEvent.PresenceEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link PresenceRegistry}. Exercises the lease reconcile/leave and cross-server merge
 * logic through the two injected interfaces ({@link LocalPresenceNotifier} to observe local SSE
 * fan-out, {@link PresenceRegistry.CrossServerPresenceRelay} to observe peer publishing). The
 * time-driven reaper ({@link PresenceRegistry#start()}) is intentionally not exercised here -- its
 * expiry is wall-clock based with no time seam, so it belongs to integration testing; the mutation
 * and merge paths that determine correctness are all covered.
 *
 * @author Boeing
 */
public class PresenceRegistryTest {

   private static final String CONTEXT_A = "branch/1/artifact/10";
   private static final String CONTEXT_B = "workflow/200683";

   // @formatter:off
   @Mock private LocalPresenceNotifier notifier;
   @Mock private PresenceRegistry.CrossServerPresenceRelay relay;
   @Captor private ArgumentCaptor<PresenceUpdate> updateCaptor;
   @Captor private ArgumentCaptor<Set<Long>> sinksCaptor;
   @Captor private ArgumentCaptor<List<PresenceEntry>> relayUsersCaptor;
   // @formatter:on

   private PresenceRegistry registry;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      registry = new PresenceRegistry(notifier);
      registry.setCrossServerPresenceRelay(relay);
   }

   @Test
   public void testHeartbeatCreatesLeaseNotifiesLocalAndPublishesToPeers() {
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));

      verify(notifier).notifySinks(sinksCaptor.capture(), eq("presenceUpdate"), updateCaptor.capture());
      assertEquals(Set.of(100L), sinksCaptor.getValue());
      PresenceUpdate update = updateCaptor.getValue();
      assertEquals(CONTEXT_A, update.getContext());
      assertEquals(userIds(update.getUsers()), Arrays.asList("user-1"));

      verify(relay).relayPresence(eq(CONTEXT_A), relayUsersCaptor.capture());
      assertEquals(Arrays.asList("user-1"),
         relayUsersCaptor.getValue().stream().map(PresenceEntry::getUserId).collect(Collectors.toList()));
   }

   @Test
   public void testResyncOnRelayReregistrationRepublishesAllLocalPresence() {
      // Two contexts have local presence.
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      registry.heartbeat("user-2", "Sam", 200L, Set.of(CONTEXT_B));
      reset(notifier, relay);

      // The relay (re)registers -- e.g. server startup or a cross-server bus reconnect. All current
      // local presence must be re-published to peers so they repopulate promptly rather than
      // waiting on the remote TTL (and so a leave that was lost while the bus was down is corrected).
      registry.setCrossServerPresenceRelay(relay);

      verify(relay).relayPresence(eq(CONTEXT_A), relayUsersCaptor.capture());
      assertEquals(Arrays.asList("user-1"),
         relayUsersCaptor.getValue().stream().map(PresenceEntry::getUserId).collect(Collectors.toList()));
      verify(relay).relayPresence(eq(CONTEXT_B), relayUsersCaptor.capture());
      assertEquals(Arrays.asList("user-2"),
         relayUsersCaptor.getValue().stream().map(PresenceEntry::getUserId).collect(Collectors.toList()));
   }

   @Test
   public void testHeartbeatOnlyNotifiesLocallyForNewlyAddedContexts() {
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      // Same context again (unchanged) plus a genuinely new one.
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A, CONTEXT_B));

      // Local SSE re-render only for the newly added context, not the unchanged one.
      verify(notifier, times(1)).notifySinks(any(), eq("presenceUpdate"), any());
      verify(notifier).notifySinks(any(), eq("presenceUpdate"),
         org.mockito.ArgumentMatchers.argThat(u -> CONTEXT_B.equals(u.getContext())));

      // Unchanged context is still re-published to peers (TTL refresh), so both are published.
      verify(relay).relayPresence(eq(CONTEXT_A), any());
      verify(relay).relayPresence(eq(CONTEXT_B), any());
   }

   @Test
   public void testHeartbeatFromNewSinkForSameContextReNotifiesTheReconnectingSink() {
      // A user is present in CONTEXT_A on sink 100 (e.g. their previous leader connection).
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      // The same user now reports CONTEXT_A from a DIFFERENT connection (sink 200) -- a reconnect
      // or leadership handoff after the old tab closed, before the old lease was reaped. Even
      // though the (userId, context) lease already exists, the changed sinkId must trigger a
      // roster re-broadcast so the new connection receives the current occupants (Bug: rejoin saw
      // no existing users), and delivery must target the live sink (200), not the stale one (100).
      registry.heartbeat("user-1", "Joe", 200L, Set.of(CONTEXT_A));

      verify(notifier).notifySinks(sinksCaptor.capture(), eq("presenceUpdate"), updateCaptor.capture());
      assertEquals(Set.of(200L), sinksCaptor.getValue());
      assertEquals(CONTEXT_A, updateCaptor.getValue().getContext());
      assertEquals(Arrays.asList("user-1"), userIds(updateCaptor.getValue().getUsers()));
   }

   @Test
   public void testHeartbeatWithReducedContextSetRemovesStaleLeaseAndTellsPeers() {
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A, CONTEXT_B));
      reset(notifier, relay);

      // User is now only viewing CONTEXT_A; the CONTEXT_B lease must be dropped.
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));

      // CONTEXT_B now has no local watcher, so there is no local sink to notify -- but peers must
      // still be told it emptied (empty user list) so a peer's clients drop this server's user.
      verify(relay).relayPresence(eq(CONTEXT_B), relayUsersCaptor.capture());
      assertTrue(relayUsersCaptor.getValue().isEmpty());
      // No local notification for the emptied context (nothing left to render locally).
      verify(notifier, never()).notifySinks(any(), eq("presenceUpdate"),
         org.mockito.ArgumentMatchers.argThat(u -> CONTEXT_B.equals(u.getContext())));
   }

   @Test
   public void testDropSinkForUserRemovesOnlyThatUsersLeasesOnThatSink() {
      // Two users are present in the same context on two different SSE connections (sinks).
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      registry.heartbeat("user-2", "Sam", 200L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      // user-1's tab closes and reports its own sink (100). Only user-1's lease must be dropped;
      // the roster re-broadcast to CONTEXT_A's remaining sinks must show just user-2.
      registry.dropSinkForUser("user-1", 100L);

      verify(notifier).notifySinks(any(), eq("presenceUpdate"),
         org.mockito.ArgumentMatchers.argThat(u -> CONTEXT_A.equals(u.getContext())
            && userIds(u.getUsers()).equals(Arrays.asList("user-2"))));
   }

   @Test
   public void testDropSinkForUserIgnoresAnotherUsersSinkId() {
      // user-1 present on sink 100, user-2 present on sink 200, same context.
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      registry.heartbeat("user-2", "Sam", 200L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      // user-1 (authenticated) reports user-2's sink id (200), attempting to evict user-2. The
      // ownership scope means nothing is removed: user-1 holds no lease on sink 200. No leases
      // change, so no broadcast fires and user-2's presence is untouched.
      registry.dropSinkForUser("user-1", 200L);

      verify(notifier, never()).notifySinks(any(), anyString(), any());
      verify(relay, never()).relayPresence(anyString(), any());
   }

   @Test
   public void testApplyRemotePresenceMergesPeerUsersIntoLocalBroadcast() {
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      registry.applyRemotePresence("server-B", CONTEXT_A, Arrays.asList(new PresenceEntry("user-9", "Remote")));

      // Local clients see the merged set (local user-1 + peer user-9)...
      verify(notifier).notifySinks(any(), eq("presenceUpdate"), updateCaptor.capture());
      assertTrue(userIds(updateCaptor.getValue().getUsers()).containsAll(Arrays.asList("user-1", "user-9")));
      // ...but a peer-originated change is NOT re-published to peers (loop prevention).
      verify(relay, never()).relayPresence(anyString(), any());
   }

   @Test
   public void testApplyRemotePresenceWithEmptyListClearsPeerContribution() {
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      registry.applyRemotePresence("server-B", CONTEXT_A, Arrays.asList(new PresenceEntry("user-9", "Remote")));
      reset(notifier, relay);

      registry.applyRemotePresence("server-B", CONTEXT_A, List.of());

      verify(notifier).notifySinks(any(), eq("presenceUpdate"), updateCaptor.capture());
      assertEquals(Arrays.asList("user-1"), userIds(updateCaptor.getValue().getUsers()));
   }

   @Test
   public void testMergedUsersAreDedupedByUserId() {
      // Same user id present both locally and reported by a peer -> appears once.
      registry.heartbeat("user-1", "Joe", 100L, Set.of(CONTEXT_A));
      reset(notifier, relay);

      registry.applyRemotePresence("server-B", CONTEXT_A, Arrays.asList(new PresenceEntry("user-1", "Joe")));

      verify(notifier).notifySinks(any(), eq("presenceUpdate"), updateCaptor.capture());
      assertEquals(Arrays.asList("user-1"), userIds(updateCaptor.getValue().getUsers()));
   }

   @Test
   public void testOnlyLeasesWithSinkIdAreNotified() {
      // A lease with a null sinkId contributes to the user set but is not itself a notification
      // target; with no other target sinks, notifySinks must not fire (empty target set).
      registry.heartbeat("user-1", "Joe", null, Set.of(CONTEXT_A));

      verify(notifier, never()).notifySinks(any(), anyString(), any());
      // Peers are still told about the user regardless of local sink targeting.
      verify(relay).relayPresence(eq(CONTEXT_A), any());
   }

   private static List<String> userIds(List<PresenceUser> users) {
      return users.stream().map(PresenceUser::getUserId).collect(Collectors.toList());
   }
}
