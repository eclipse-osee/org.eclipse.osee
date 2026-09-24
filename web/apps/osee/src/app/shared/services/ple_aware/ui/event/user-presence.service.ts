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
import { HttpClient, HttpContext } from '@angular/common/http';
import {
	Injectable,
	Signal,
	WritableSignal,
	effect,
	inject,
	signal,
	DestroyRef,
	Injector,
	runInInjectionContext,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { apiURL, environment } from '@osee/environments';
import {
	SseEventService,
	WebLocksService,
	presenceUser,
} from '@osee/shared/services/network';
import { UserDataAccountService } from '@osee/auth';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, take } from 'rxjs';
import { SKIP_LOADING } from '../../../../../interceptors/loading-indicator.interceptor';

export { presenceUser };

/**
 * Handle returned by watchContext. Read `users` to see who else is viewing
 * the same context.
 */
export type presenceHandle = {
	readonly users: Signal<presenceUser[]>;
};

/** Client heartbeat interval (ms). */
const HEARTBEAT_INTERVAL_MS = 15_000;

/** Prefix for the per-tab liveness Web Lock; the leader queries these to know which tabs are alive. */
const TAB_LOCK_PREFIX = 'osee-presence-tab-';

/** True when two string sets contain exactly the same members. */
function sameStringSet(a: Set<string>, b: Set<string>): boolean {
	if (a.size !== b.size) {
		return false;
	}
	for (const value of a) {
		if (!b.has(value)) {
			return false;
		}
	}
	return true;
}

/**
 * Messages sent between tabs on the presence BroadcastChannel.
 * Each tab reports its contexts to the leader. The leader aggregates
 * and sends one heartbeat to the server.
 */
type presenceChannelMessage =
	// A tab stating its current watched contexts. This is the ONLY tab-state message: a change, a
	// join, and a leave are all just a new announcement (an empty `contexts` array means the tab is
	// watching nothing / is going away). The leader keys these by `tabId`; a departed tab is
	// detected by its per-tab Web Lock being released (see pruneDepartedTabs), so no separate
	// "closing" message is needed.
	| { type: 'announce'; tabId: string; contexts: string[] }
	// Leader -> all tabs: "re-announce your contexts". Sent when a tab becomes leader and on every
	// heartbeat tick, so the leader's aggregate is continuously reconciled from the live tabs.
	| { type: 'request-announce' };

/**
 * Lease-based, leader-aggregated user presence service.
 *
 * Architecture:
 * - Each tab reports its watched contexts to the leader via BroadcastChannel
 * - The leader aggregates all contexts and sends ONE heartbeat per user every 15s
 * - The heartbeat contains the full context set — server reconciles (adds/removes)
 * - Leases expire after 45s without renewal (handles crash)
 * - SSE delivers presence updates to interested clients
 * - Components call watchContext() and get a signal of other users
 */
@Injectable({
	providedIn: 'root',
})
export class UserPresenceService {
	private readonly http = inject(HttpClient);
	private readonly sseEventService = inject(SseEventService);
	private readonly webLocks = inject(WebLocksService);
	private readonly injector = inject(Injector);
	private readonly userService = inject(UserDataAccountService);
	private readonly destroyRef = inject(DestroyRef);

	private readonly tabId = crypto.randomUUID();

	/**
	 * Name of the per-tab Web Lock this tab holds for its entire lifetime. The browser releases it
	 * automatically when the tab goes away (clean close, crash, or kill), so the leader can tell
	 * which tabs are still alive by querying held locks -- a reliable liveness signal that does not
	 * depend on a best-effort unload message.
	 */
	private readonly tabLockName = `${TAB_LOCK_PREFIX}${this.tabId}`;

	/** Releases this tab's liveness lock (resolves the holding promise) on teardown. */
	private releaseTabLock: (() => void) | null = null;

	private readonly currentUserId = toSignal(
		this.userService.user.pipe(map((u): string => u?.id ?? '')),
		{ initialValue: '' }
	);

	/** This tab's active contexts. */
	private readonly localContexts = new Set<string>();

	/**
	 * Leader's view: each known tab's watched contexts. Only populated on the leader tab. Liveness
	 * is determined by the per-tab Web Lock (see {@link pruneDepartedTabs}), not by freshness, so
	 * no timestamp is tracked here.
	 */
	private readonly allTabContexts = new Map<string, Set<string>>();

	/**
	 * Dispatch map: context -> the set of subscriber signals watching it. Multiple components can
	 * watch the same context; a presence update fans out to ALL of their signals. Using a Set of
	 * signals (rather than one signal + a refCount) is what makes the second and later watchers
	 * actually receive updates. The context entry is dropped when its last subscriber leaves.
	 */
	private readonly contextSignals = new Map<
		string,
		Set<WritableSignal<presenceUser[]>>
	>();

	/** BroadcastChannel for inter-tab presence coordination. */
	private presenceChannel: BroadcastChannel | null = null;

	/** Heartbeat interval (only active on leader). */
	private heartbeatInterval: ReturnType<typeof setInterval> | null = null;

	/**
	 * The presence leader is the tab that owns the SSE connection -- and thus the authoritative live
	 * sinkId. Binding to it (not a separate lock) keeps the heartbeat's sinkId and the connection in
	 * lockstep, so a heartbeat never carries a stale/mirrored sinkId.
	 */
	private get isPresenceLeader(): boolean {
		return this.sseEventService.isConnectionLeader();
	}

	constructor() {
		this.sseEventService.presenceUpdates$
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((update) => {
				const subscribers = this.contextSignals.get(update.context);
				if (subscribers && subscribers.size > 0) {
					const myId = this.currentUserId();
					const others = myId
						? update.users.filter((u) => u.userId !== myId)
						: update.users;
					// Fan out to every signal watching this context, not just the first.
					for (const users of subscribers) {
						users.set(others);
					}
				}
			});

		this.presenceChannel = new BroadcastChannel('osee-presence');
		this.presenceChannel.onmessage = (event) =>
			this.handlePresenceChannelMessage(
				event.data as presenceChannelMessage
			);

		// Hold a per-tab liveness lock for this tab's lifetime. The browser releases it on tab
		// close/crash/kill, letting the leader detect departures reliably (see pruneStaleTabs).
		this.webLocks.requestLock(
			this.tabLockName,
			() =>
				new Promise<void>((resolve) => {
					this.releaseTabLock = resolve;
				})
		);

		// The presence heartbeat is sent by the SSE-connection leader -- the tab that owns the
		// browser's single connection and its live sinkId -- REGARDLESS of which page that tab is
		// on. Aggregation makes this correct: every watching tab (even on another page) reports its
		// contexts to the leader via `announce`, so the leader heartbeats the union. Binding to the
		// connection owner keeps the heartbeat's sinkId always current (no cross-tab staleness).
		// serverReady is a dependency so the heartbeat fires the moment the server is ready rather
		// than up to a full interval later. The heartbeat itself is gated on a non-empty aggregate
		// (see doSendHeartbeat), so a leader on a page nobody is watching stays quiet.
		effect(() => {
			const leader = this.sseEventService.isConnectionLeader();
			const ready = this.sseEventService.serverReady();
			if (leader) {
				// startLeading seeds announces + starts the periodic loop but does NOT send.
				this.startLeading();
				// The single "send now" -- fires on becoming leader (if already ready) and again
				// when serverReady later flips true. Debounced, so re-runs coalesce.
				if (ready) {
					this.sendImmediateHeartbeat();
				}
			} else {
				this.stopLeading();
			}
		});

		// Best-effort leave on page unload.
		window.addEventListener('pagehide', this.onPageHide);

		// Release timers, listeners, and the channel on teardown. This is a page-lifetime
		// singleton, but explicit cleanup keeps it consistent with the sibling event services
		// and correct under test/HMR teardown.
		this.destroyRef.onDestroy(() => {
			window.removeEventListener('pagehide', this.onPageHide);
			if (this.heartbeatInterval) {
				clearInterval(this.heartbeatInterval);
				this.heartbeatInterval = null;
			}
			if (this.heartbeatDebounceTimer) {
				clearTimeout(this.heartbeatDebounceTimer);
				this.heartbeatDebounceTimer = null;
			}
			this.releaseTabLock?.();
			this.releaseTabLock = null;
			this.presenceChannel?.close();
			this.presenceChannel = null;
		});
	}

	/**
	 * Ensures this service is constructed in EVERY tab, called once from the app root -- like the
	 * sibling event services. This matters because the presence heartbeat is sent by the
	 * SSE-connection leader, and that leader may be on a page that never watches a context (e.g. the
	 * ACTRA world page). Without app-root construction, such a leader would have no presence service
	 * to receive other tabs' announces or heartbeat their contexts, and presence would silently not
	 * work. All real setup happens in the constructor; this is just the explicit construction hook.
	 */
	initialize(): void {
		// Intentionally empty: injecting + constructing the service is the initialization.
	}

	/**
	 * Best-effort cleanup on unload: an empty announce for sibling tabs of this browser, and (if
	 * leader) a server leave for this browser's SSE connection. Lease TTL is the backstop if lost.
	 * Bound field so it can be removed on destroy.
	 */
	private readonly onPageHide = (): void => {
		this.presenceChannel?.postMessage({
			type: 'announce',
			tabId: this.tabId,
			contexts: [],
		} as presenceChannelMessage);

		this.sendLeaveBeacon();
	};

	/**
	 * Tells the server this browser's SSE connection is leaving, so its presence clears at once
	 * rather than waiting out the lease TTL (the SSE stack has no server-side close callback). Only
	 * the leader owns the connection. `keepalive` lets the request survive the unloading page; the
	 * SSE auth headers are set explicitly since this raw fetch bypasses the Angular interceptor.
	 */
	private sendLeaveBeacon(): void {
		const sinkId = this.sseEventService.sseConnectionId;
		if (!this.isPresenceLeader || !sinkId) {
			return;
		}
		const headers: Record<string, string> = {
			'Content-Type': 'application/json',
		};
		const userId = this.currentUserId();
		if (environment.authScheme !== 'NONE' && userId) {
			headers['osee.account.id'] = userId;
			headers['Authorization'] = userId;
		}
		void fetch(`${apiURL}/orcs/sse/presence/leave`, {
			method: 'POST',
			headers,
			body: JSON.stringify({ sinkId: Number(sinkId) }),
			keepalive: true,
		}).catch(() => {
			// Unload is best-effort; the server's lease TTL reaps the connection if this is lost.
		});
	}

	/**
	 * Registers interest in a context. Returns a signal of other users viewing it.
	 */
	watchContext(
		contextKey: Signal<string>,
		destroyRef: DestroyRef
	): presenceHandle {
		const users = signal<presenceUser[]>([]);
		let currentContext = '';

		const effectRef = runInInjectionContext(this.injector, () =>
			effect(() => {
				const newContext = contextKey();
				if (!newContext || newContext === currentContext) {
					return;
				}
				if (currentContext) {
					this.removeLocalContext(currentContext, users);
				}
				currentContext = newContext;
				users.set([]);
				this.addLocalContext(newContext, users);
			})
		);

		destroyRef.onDestroy(() => {
			effectRef.destroy();
			if (currentContext) {
				this.removeLocalContext(currentContext, users);
			}
		});

		return { users: users.asReadonly() };
	}

	// ─── Local Context Management ─────────────────────────────────────

	private addLocalContext(
		context: string,
		users: WritableSignal<presenceUser[]>
	): void {
		this.localContexts.add(context);

		let subscribers = this.contextSignals.get(context);
		if (!subscribers) {
			subscribers = new Set<WritableSignal<presenceUser[]>>();
			this.contextSignals.set(context, subscribers);
		}
		subscribers.add(users);

		this.broadcastLocalContexts();
	}

	private removeLocalContext(
		context: string,
		users: WritableSignal<presenceUser[]>
	): void {
		const subscribers = this.contextSignals.get(context);
		if (subscribers) {
			subscribers.delete(users);
			if (subscribers.size === 0) {
				this.contextSignals.delete(context);
				// Only stop advertising the context once its LAST watcher is gone.
				this.localContexts.delete(context);
			}
		} else {
			this.localContexts.delete(context);
		}

		this.broadcastLocalContexts();
	}

	private broadcastLocalContexts(): void {
		this.presenceChannel?.postMessage({
			type: 'announce',
			tabId: this.tabId,
			contexts: [...this.localContexts],
		} as presenceChannelMessage);

		// Record our own contexts in the leader's view (if we ARE the leader) and heartbeat only
		// when that actually changed the aggregate.
		if (this.recordTabContexts(this.tabId, this.localContexts)) {
			this.sendImmediateHeartbeat();
		}
	}

	/**
	 * Records a tab's current contexts in the leader's view, or drops the tab when it announces
	 * nothing. The single write path for both {@code announce} messages and this leader tab's own
	 * changes. Returns true only when the aggregate actually changed, so callers can skip a
	 * redundant heartbeat on an unchanged re-announce.
	 */
	private recordTabContexts(tabId: string, contexts: Set<string>): boolean {
		if (contexts.size === 0) {
			return this.allTabContexts.delete(tabId);
		}
		const existing = this.allTabContexts.get(tabId);
		if (existing && sameStringSet(existing, contexts)) {
			return false;
		}
		this.allTabContexts.set(tabId, new Set(contexts));
		return true;
	}

	// ─── Presence Leadership (follows SSE-connection leadership) ───────

	/**
	 * This tab became the SSE-connection leader, so it takes over presence reporting for the whole
	 * browser -- aggregating every tab's contexts, including watchers on other pages. Seed the view
	 * by asking all tabs to (re-)announce, then start the heartbeat loop. The loop's sends are
	 * idle-gated (see doSendHeartbeat), so leading a page nobody watches costs nothing on the wire.
	 * Idempotent -- guarded so a repeated leader signal does not stack intervals.
	 */
	private startLeading(): void {
		if (this.heartbeatInterval) {
			return;
		}
		this.recordTabContexts(this.tabId, this.localContexts);

		// Ask all tabs to (re-)announce so the leader's aggregate is seeded promptly. Announces
		// arrive asynchronously and each triggers an immediate heartbeat, so a watcher on another
		// page populates the aggregate (and starts real heartbeats) within a debounce window.
		this.presenceChannel?.postMessage({
			type: 'request-announce',
		} as presenceChannelMessage);

		// Start the periodic loop only. The initial "send now" is owned by the leadership effect
		// (it fires once leader AND serverReady), so this method does not send here -- that would
		// double up with the effect's immediate heartbeat.
		this.heartbeatInterval = setInterval(
			() => this.sendHeartbeat(),
			HEARTBEAT_INTERVAL_MS
		);
	}

	/**
	 * This tab is no longer the SSE-connection leader (handoff or the connection dropped). Stop
	 * heartbeating so it never keeps asserting a sinkId it no longer owns.
	 */
	private stopLeading(): void {
		if (this.heartbeatInterval) {
			clearInterval(this.heartbeatInterval);
			this.heartbeatInterval = null;
		}
		if (this.heartbeatDebounceTimer) {
			clearTimeout(this.heartbeatDebounceTimer);
			this.heartbeatDebounceTimer = null;
		}
		// Reset the idle gate so a later re-promotion doesn't send a spurious "clear" heartbeat
		// before it has reported anything.
		this.lastHeartbeatHadContexts = false;
	}

	private handlePresenceChannelMessage(msg: presenceChannelMessage): void {
		switch (msg.type) {
			case 'announce':
				// A tab stated its contexts (a change, a join, or a leave via an empty set). Only
				// the leader aggregates; followers ignore announcements. Re-heartbeat ONLY when the
				// aggregate actually changed -- the periodic request-announce makes every live tab
				// re-announce each tick, and those unchanged re-announces must not each trigger an
				// extra heartbeat (that was a duplicate heartbeat every tick).
				if (
					this.isPresenceLeader &&
					this.recordTabContexts(msg.tabId, new Set(msg.contexts))
				) {
					this.sendImmediateHeartbeat();
				}
				break;
			case 'request-announce':
				// A new leader is asking all tabs to (re-)announce their contexts.
				this.broadcastLocalContexts();
				break;
		}
	}

	// ─── Heartbeat ────────────────────────────────────────────────────

	/**
	 * Debounce timer for immediate heartbeats. Uses trailing-edge debounce:
	 * rapid changes are coalesced into one request that fires after the
	 * debounce window. Nothing is lost — the last state always gets sent.
	 */
	private heartbeatDebounceTimer: ReturnType<typeof setTimeout> | null = null;

	/** Debounce window (ms). Rapid context changes within this window are batched. */
	private static readonly HEARTBEAT_DEBOUNCE_MS = 500;

	/**
	 * Whether the last heartbeat we sent carried a non-empty context set. Gates the idle case: when
	 * the aggregate is empty we skip the POST, EXCEPT for the one transition from non-empty to empty
	 * (the last watcher left), which must be sent so the server clears the leases. So a leader on a
	 * page nobody watches never POSTs, but a real "everyone left" still clears promptly.
	 */
	private lastHeartbeatHadContexts = false;

	/**
	 * Schedules a heartbeat to fire after the debounce window. Only the presence leader sends
	 * heartbeats. Rapid changes coalesce into one trailing-edge request; the last state wins.
	 */
	private sendImmediateHeartbeat(): void {
		if (!this.isPresenceLeader) {
			return;
		}
		if (this.heartbeatDebounceTimer) {
			clearTimeout(this.heartbeatDebounceTimer);
		}
		this.heartbeatDebounceTimer = setTimeout(() => {
			this.heartbeatDebounceTimer = null;
			this.doSendHeartbeat();
		}, UserPresenceService.HEARTBEAT_DEBOUNCE_MS);
	}

	/** Periodic (interval-driven) heartbeat: reconcile the aggregate from live tabs, then send. */
	private sendHeartbeat(): void {
		// Record this leader tab's own contexts.
		this.recordTabContexts(this.tabId, this.localContexts);

		// Re-request announces from all tabs every tick so the aggregate is self-healing: a lost
		// announce or a wrongly-pruned follower is rebuilt within one interval when the live tabs
		// re-announce. Without this, once the aggregate emptied it could stay empty forever and the
		// heartbeat would never restart. Cheap and idempotent -- a local BroadcastChannel message.
		this.presenceChannel?.postMessage({
			type: 'request-announce',
		} as presenceChannelMessage);

		// Prune tabs whose per-tab Web Lock is gone (browser-guaranteed on tab death), then send.
		// Async, so send after the prune resolves.
		void this.pruneDepartedTabs().then(() => this.doSendHeartbeat());
	}

	/**
	 * Removes {@link allTabContexts} entries for tabs that are no longer alive, so the heartbeat
	 * union stops including a departed tab's contexts.
	 *
	 * Signal: the per-tab Web Lock. The browser releases a tab's lock the instant it dies (clean
	 * close, crash, or kill), so a tab whose lock is neither held nor pending has departed --
	 * reliable and prompt, unlike a best-effort unload message. Presence only runs when Web Locks
	 * are available (SSE leadership uses the same API and refuses to run without it), so there is no
	 * "locks unavailable" case to fall back on here; a genuinely wedged tab that never releases its
	 * lock is reaped server-side by the lease TTL. Never evicts on staleness alone -- a live but
	 * quiet tab must not be dropped.
	 */
	private async pruneDepartedTabs(): Promise<void> {
		const activeNames = new Set(await this.webLocks.queryActiveLockNames());
		// If the query returned nothing usable (API unavailable, or an empty/failed snapshot), do
		// not prune -- we cannot distinguish "all gone" from "no data", and a false prune drops a
		// live viewer. Presence would not be running at all without locks, so this is just safety.
		if (!this.webLocks.isSupported || activeNames.size === 0) {
			return;
		}
		for (const [tabId] of this.allTabContexts) {
			if (tabId === this.tabId) {
				continue;
			}
			if (!activeNames.has(`${TAB_LOCK_PREFIX}${tabId}`)) {
				this.allTabContexts.delete(tabId);
			}
		}
	}

	private doSendHeartbeat(): void {
		// Only the connection leader heartbeats -- it owns the live sinkId. A handoff between the
		// debounce/interval firing and here would mean this tab no longer owns the connection.
		if (!this.isPresenceLeader) {
			return;
		}
		// Don't heartbeat until the server is ready. During (re)connect the server can't serve the
		// request, and with no live SSE there's nothing to deliver presence to anyway. The 15s
		// interval will send once readiness is confirmed.
		if (!this.sseEventService.serverReady()) {
			return;
		}

		const allContexts = new Set<string>();
		for (const contexts of this.allTabContexts.values()) {
			for (const ctx of contexts) {
				allContexts.add(ctx);
			}
		}

		// Idle gate: a leader on a page nobody watches has an empty aggregate. Skip the POST while
		// empty -- EXCEPT the single non-empty -> empty transition, which must be sent so the server
		// clears the leases (the last watcher left). This keeps an idle leader silent without ever
		// stranding presence that should be cleared.
		const hasContexts = allContexts.size > 0;
		if (!hasContexts && !this.lastHeartbeatHadContexts) {
			return;
		}

		// Bind the heartbeat to THIS leader's live sinkId. If the connection id isn't a usable
		// numeric sink yet, skip entirely rather than write a lease under a null sink -- a null-sink
		// lease is undeliverable (broadcastToLocalSinks skips it) and un-leaveable (dropSinkForUser
		// can't match it). The next interval tick sends once the id is established.
		const rawSinkId = this.sseEventService.sseConnectionId;
		const parsedSinkId = rawSinkId ? Number(rawSinkId) : NaN;
		if (!Number.isFinite(parsedSinkId)) {
			return;
		}
		const sinkId = parsedSinkId;
		this.lastHeartbeatHadContexts = hasContexts;

		this.http
			.post(
				`${apiURL}/orcs/sse/presence/heartbeat`,
				{
					contexts: [...allContexts],
					sinkId,
				},
				{ context: new HttpContext().set(SKIP_LOADING, true) }
			)
			.pipe(take(1))
			.subscribe();
	}
}
