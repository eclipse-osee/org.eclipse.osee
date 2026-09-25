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
import { Injectable, NgZone, OnDestroy, inject, signal } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Subject, Observable, filter, take } from 'rxjs';
import { apiURL, environment } from '@osee/environments';
import { UserDataAccountService } from '@osee/auth';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { ErrorService } from '../ple_aware/ui/error/error.service';
import { SKIP_LOADING } from '../../../interceptors/loading-indicator.interceptor';
import { WebLocksService } from './web-locks.service';

/**
 * Describes an artifact-change event received from the server SSE stream.
 * The server publishes this after a successful transaction commit.
 * The transactionId serves as the authoritative freshness key — globally
 * monotonic (DB-allocated), definitively ordering all changes across servers.
 * Clients receive this notification and call their existing REST endpoint
 * to fetch fresh data (pull model).
 */
export type artifactChangeEvent = {
	/** The branch on which the change occurred. */
	branchId: string;
	/** Artifact IDs that were modified, created, or deleted. */
	artifactIds: string[];
	/** The transaction ID of the committed change. Authoritative freshness key. */
	transactionId: string;
	/** Who made the change (session ID or user ID for self-suppression). */
	userId?: string;
	/** What kind of changes occurred in this transaction. */
	changeTypes?: artifactChangeType[];
	/**
	 * User references from the changed user-valued attributes, grouped by attribute type.
	 * Populated generically for attribute types marked as user references on the server.
	 * Consumers filter by the attribute type id they care about and match userIds against
	 * the current user to decide relevance client-side (e.g. Actra "My World").
	 */
	associatedUsers?: associatedUsers[];
	/**
	 * Distinct attribute type ids changed anywhere in this transaction. Lets consumers do targeted
	 * refreshes (e.g. a hierarchy label only when the Name attribute type changed) instead of
	 * reacting to every `attribute_modified`. Empty/absent when no attribute values changed.
	 */
	changedAttributeTypeIds?: string[];
	/**
	 * The origin id of the tab that initiated the change (echoed by the server). When it equals
	 * this tab's own origin id, the event is this tab's own echo and is treated as local.
	 */
	originId?: string;
};

/**
 * A group of user references carried by a changed attribute, tagged by the attribute type
 * they came from. `encoding` says whether the ids are user artifact ids or userId strings.
 */
export type associatedUsers = {
	/** The attribute type id these user references came from. */
	typeId: string;
	/** Identity space of userIds: 'artId' (user artifact id) or 'userId' (userId string). */
	encoding: 'artId' | 'userId';
	/** The user ids referenced by the changed attribute value(s). */
	userIds: string[];
};

/**
 * Types of changes that can occur in a transaction.
 * Used by consumers to decide which resources need refreshing.
 */
export type artifactChangeType =
	| 'attribute_modified'
	| 'artifact_created'
	| 'artifact_deleted'
	| 'relation_added'
	| 'relation_deleted'
	| 'relation_modified';

/**
 * Describes a branch metadata change event received from the server SSE stream.
 * The server publishes this when branch properties are modified.
 * This is notification-only — the client performs a GET to retrieve current state.
 */
export type branchChangeEvent = {
	/** The branch that was modified. */
	branchId: string;
	/** What kind of branch change occurred. */
	changeType: branchChangeType;
	/** Who made the change. */
	userId?: string;
	/**
	 * The origin id of the tab that initiated the change (echoed by the server). When it equals
	 * this tab's own origin id, the event is this tab's own echo and is treated as local.
	 */
	originId?: string;
	/**
	 * For a {@link branchChangeType} of `'rebaselined'`: the id of the new working branch that
	 * replaced `branchId` (which is now deleted/rebaselined). Consumers keyed on the old
	 * `branchId` use this to re-point to the branch going forward. Undefined for other types.
	 */
	newBranchId?: string;
	/**
	 * The changed branch's associated artifact id. Present on `created` and `state_changed`/
	 * `deleted` events; lets a consumer that doesn't track the branch by id (e.g. an ATS workflow
	 * editor awaiting a working branch created for it) match the event to its artifact. Undefined
	 * on other change types and on desktop-relayed events.
	 */
	associatedArtifactId?: string;
};

/**
 * Types of branch metadata changes.
 */
export type branchChangeType =
	| 'created'
	| 'committed'
	| 'renamed'
	| 'archived'
	| 'unarchived'
	| 'state_changed'
	| 'type_changed'
	| 'deleted'
	| 'purged'
	| 'rebaselined';

/**
 * Connection state of the SSE stream.
 */
export type sseConnectionState = 'disconnected' | 'connecting' | 'connected';

/**
 * Represents a user currently viewing a context.
 */
export type presenceUser = {
	userId: string;
	userName: string;
};

/**
 * A presence update for a specific context — lists all users viewing that context.
 */
export type presenceUpdate = {
	context: string;
	users: presenceUser[];
};

/**
 * Messages sent over the BroadcastChannel between tabs for event distribution.
 * Leader election is handled by Web Locks — the channel is only for data.
 */
type sseChannelMessage =
	| { type: 'artifact'; payload: artifactChangeEvent }
	| { type: 'branch'; payload: branchChangeEvent }
	| { type: 'state'; payload: sseConnectionState }
	| { type: 'connectionId'; payload: string }
	| { type: 'presenceUpdate'; payload: presenceUpdate }
	| { type: 'reestablished' }
	| { type: 'force-reconnect' }
	| { type: 'request-state' };

/** Name of the Web Lock used for leader election. */
const LEADER_LOCK_NAME = 'osee-sse-leader';

/** Reconnect backoff parameters (leader only). */
const RECONNECT_BASE_DELAY_MS = 1000;
const RECONNECT_MAX_DELAY_MS = 30000;
/** Wall-clock window of continuous failure before the leader pauses and goes disconnected. */
const RECONNECT_GIVE_UP_WINDOW_MS = 60_000;
/** Retry interval while paused, in case no `online`/`visibilitychange` edge fires. */
const RECONNECT_PERIODIC_RETRY_MS = 45_000;
/** Minimum spacing between resync announcements, to coalesce flapping reconnects. */
const RESYNC_COALESCE_WINDOW_MS = 3_000;
/** How long to mute the global error popup after a resync (transient warmup failures). */
const RESYNC_ERROR_MUTE_MS = 15_000;
/** Server readiness probe endpoint; 200 = ready, 503 = warming up. */
const READINESS_URL = `${apiURL}/health/ready`;
/** Backoff bounds for polling the readiness probe after an SSE (re)connect. */
const READINESS_POLL_BASE_MS = 500;
const READINESS_POLL_MAX_MS = 5_000;

/**
 * Service that maintains an SSE connection to the OSEE application server using
 * Web Locks for leader election across same-origin tabs.
 *
 * Only ONE tab (the lock holder) holds the actual SSE connection. All other tabs
 * receive events via BroadcastChannel. When the leader tab closes or crashes,
 * the browser automatically releases the lock and the next waiting tab acquires
 * it — providing instant failover with zero heartbeats or timeouts.
 *
 * Consumers interact with this service identically regardless of whether the
 * current tab is the leader or a follower — the public API is the same.
 */
@Injectable({
	providedIn: 'root',
})
export class SseEventService implements OnDestroy {
	private readonly userService = inject(UserDataAccountService);
	private readonly errorService = inject(ErrorService);
	private readonly snackBar = inject(MatSnackBar);
	private readonly webLocks = inject(WebLocksService);
	/**
	 * The SSE lifecycle runs on callbacks that fire OUTSIDE Angular's zone: the Web Locks
	 * leadership callback and the BroadcastChannel `onmessage` handler. Signal writes there mark
	 * reactive consumers dirty but do not themselves trigger change detection, so `effect()`s (e.g.
	 * presence keying off {@link isConnectionLeader}) may not flush until an unrelated event runs
	 * CD -- causing intermittent "didn't react to leadership/state change" bugs. Running those
	 * callbacks through the zone makes the writes reliably schedule CD.
	 */
	private readonly ngZone = inject(NgZone);
	/** Ensures the unsupported-environment notice shows at most once. */
	private notifiedRealtimeUnavailable = false;
	private readonly http = inject(HttpClient);
	/** True while a user-resolution retry loop is active, to avoid stacking duplicates. */
	private resolvingUser = false;
	/** Timestamp (ms) when the current user-resolution loop began; bounds auth retries. */
	private userResolveStartedAt = 0;
	private readonly _artifactEvents = new Subject<artifactChangeEvent>();
	private readonly _branchEvents = new Subject<branchChangeEvent>();
	private readonly _presenceUpdates = new Subject<presenceUpdate>();
	private readonly _connectionReestablished = new Subject<void>();
	private readonly _connectionState =
		signal<sseConnectionState>('disconnected');
	/** True when the server has confirmed readiness on the current connection; gates mutations. */
	private readonly _serverReady = signal(false);

	private abortController: AbortController | null = null;
	private currentUserId = '';
	private _sseConnectionId = '';
	private channel: BroadcastChannel | null = null;
	/**
	 * Single source of truth for "this tab holds the SSE connection lock". A signal so reactive
	 * consumers (presence keys off {@link isConnectionLeader}) update; read synchronously via the
	 * {@link isLeader} getter in the non-reactive hot paths.
	 */
	private readonly _isConnectionLeader = signal(false);
	private get isLeader(): boolean {
		return this._isConnectionLeader();
	}

	/** Consecutive failed reconnect attempts (leader only); drives exponential backoff. */
	private reconnectAttempts = 0;
	/**
	 * True when the leader has stopped the in-band backoff loop after the give-up window and paused
	 * retrying, awaiting a recovery trigger (network back online, tab regains focus, or the
	 * periodic timer). See {@link RECONNECT_GIVE_UP_WINDOW_MS}.
	 */
	private reconnectPaused = false;
	/**
	 * Timestamp (ms) of the first failure in the current failure streak, or 0 when connected. Used
	 * to decide give-up by wall-clock window rather than attempt count.
	 */
	private firstFailureAt = 0;
	/** Handle for the periodic retry timer active while paused; null when not paused. */
	private periodicRetryTimer: ReturnType<typeof setInterval> | null = null;
	/** Bound recovery handler registered on `online`/`visibilitychange` while paused. */
	private readonly recoveryHandler = () => this.attemptRecovery();

	/** True once this browser has held a live SSE connection; distinguishes reconnect from cold start. */
	private browserWasConnected = false;
	/** True when the last open failed with an auth status (401/403); not retried via backoff. */
	private authExpired = false;
	/** Timestamp (ms) of the last resync announcement; used to coalesce flapping reconnects. */
	private lastResyncAt = 0;
	/** Generation id for the current readiness poll; bumped to cancel a superseded poll. */
	private readinessPollId = 0;
	/** Timestamp (ms) when the current readiness-poll phase began; bounds it independently. */
	private readinessStartedAt = 0;

	/** The unique SSE connection ID assigned by the server. Used to exclude self from broadcasts. */
	get sseConnectionId(): string {
		return this._sseConnectionId;
	}

	/** Observable stream of all artifact change events from the server. */
	readonly artifactChanges$: Observable<artifactChangeEvent> =
		this._artifactEvents.asObservable();

	/** Observable stream of all branch change events from the server. */
	readonly branchChanges$: Observable<branchChangeEvent> =
		this._branchEvents.asObservable();

	/** Current connection state as a signal (for template binding). */
	readonly connectionState = this._connectionState.asReadonly();

	/**
	 * True when the server is connected AND has confirmed readiness. Consumers gate save/mutation
	 * actions on this so writes are blocked while the server is still warming up after a restart.
	 */
	readonly serverReady = this._serverReady.asReadonly();

	/**
	 * True only while THIS tab holds the SSE connection (leader). The connection-owning tab is the
	 * one with the authoritative live {@link sseConnectionId}, so presence sends its heartbeat and
	 * unload leave from this tab -- never from a follower, which would carry a stale/mirrored sinkId.
	 */
	readonly isConnectionLeader = this._isConnectionLeader.asReadonly();

	/** Observable stream of context-aware presence updates. */
	readonly presenceUpdates$: Observable<presenceUpdate> =
		this._presenceUpdates.asObservable();

	/**
	 * Emits when the SSE connection is re-established after a disconnection.
	 * Consumers should reset sequence tracking and perform a full refresh
	 * since events may have been missed during the disconnect.
	 */
	readonly connectionReestablished$: Observable<void> =
		this._connectionReestablished.asObservable();

	/**
	 * Returns an observable filtered to artifact events affecting a specific branch.
	 */
	artifactChangesForBranch(
		branchId: string
	): Observable<artifactChangeEvent> {
		return this.artifactChanges$.pipe(
			filter((event) => event.branchId === branchId)
		);
	}

	/**
	 * Returns an observable filtered to artifact events affecting a specific artifact on a branch.
	 */
	artifactChangesForArtifact(
		branchId: string,
		artifactId: string
	): Observable<artifactChangeEvent> {
		return this.artifactChanges$.pipe(
			filter(
				(event) =>
					event.branchId === branchId &&
					event.artifactIds.includes(artifactId)
			)
		);
	}

	/**
	 * Returns an observable filtered to branch events for a specific branch.
	 */
	branchChangesForBranch(branchId: string): Observable<branchChangeEvent> {
		return this.branchChanges$.pipe(
			filter((event) => event.branchId === branchId)
		);
	}

	/**
	 * Opens the SSE connection (or joins the leader-elected group).
	 * Safe to call multiple times — will no-op if already connected/connecting.
	 *
	 * Uses Web Locks API for leader election: only the tab that holds the
	 * 'osee-sse-leader' lock opens the SSE connection. Other tabs queue
	 * behind the lock and take over automatically when the leader releases it
	 * (on tab close, crash, or navigation — guaranteed by the browser).
	 */
	connect(): void {
		if (this.channel) {
			return;
		}

		// Set up BroadcastChannel for event distribution (all tabs). onmessage fires outside
		// Angular's zone; run the handler in the zone so signal writes (connection state,
		// serverReady, connectionId, presence updates) schedule change detection reliably.
		this.channel = new BroadcastChannel('osee-sse');
		this.channel.onmessage = (event) =>
			this.ngZone.run(() =>
				this.handleChannelMessage(event.data as sseChannelMessage)
			);

		// Ask the leader for current state (covers the case where leader
		// already connected before this tab opened)
		this.channel.postMessage({
			type: 'request-state',
		} as sseChannelMessage);

		this.resolveUserThenLead();
	}

	/**
	 * Resolves a valid user then competes for leadership, retrying auth with backoff (up to the
	 * give-up window) if it resolves to the invalid-user sentinel.
	 */
	private resolveUserThenLead(): void {
		if (this.resolvingUser) {
			return;
		}
		this.resolvingUser = true;
		this.userResolveStartedAt = Date.now();
		this.tryResolveUser(0);
	}

	private tryResolveUser(attempt: number): void {
		this.userService.user.pipe(take(1)).subscribe((user) => {
			if (!!user?.id && user.id !== '-1') {
				this.resolvingUser = false;
				this.currentUserId = user.id;
				this.requestLeadership();
				return;
			}
			// Sentinel: auth failed (server not up yet). Give up after the window; otherwise
			// re-fetch auth on a capped backoff and check again.
			if (
				Date.now() - this.userResolveStartedAt >=
				RECONNECT_GIVE_UP_WINDOW_MS
			) {
				this.resolvingUser = false;
				return;
			}
			const delay = Math.min(
				READINESS_POLL_MAX_MS,
				READINESS_POLL_BASE_MS * 2 ** attempt
			);
			setTimeout(() => {
				if (!this.resolvingUser) {
					return;
				}
				this.userService.refresh();
				this.tryResolveUser(attempt + 1);
			}, delay);
		});
	}

	/**
	 * Gracefully closes the SSE connection and leaves the leader group.
	 */
	disconnect(): void {
		this.teardownSse();
		if (this.channel) {
			this.channel.close();
			this.channel = null;
		}
		this._connectionState.set('disconnected');
	}

	ngOnDestroy(): void {
		this.disconnect();
		this._artifactEvents.complete();
		this._branchEvents.complete();
		this._presenceUpdates.complete();
		this._connectionReestablished.complete();
	}

	// ─── Leader Election (Web Locks) ───────────────────────────────────

	/**
	 * Requests the leadership lock. The callback runs only when this tab
	 * holds the lock. When the tab closes or navigates away, the lock is
	 * automatically released by the browser and the next waiting tab's
	 * callback fires — providing instant, guaranteed failover.
	 */
	private requestLeadership(): void {
		const requested = this.webLocks.requestLock(
			LEADER_LOCK_NAME,
			async () => {
				// The Web Locks callback runs outside Angular's zone; run the leadership
				// transitions in the zone so the signal writes (isConnectionLeader, connection
				// state) reliably schedule change detection and downstream effects flush.
				this.ngZone.run(() => {
					this._isConnectionLeader.set(true);
					this.startConnection();
				});

				// Hold the lock for as long as this tab is alive.
				// The promise resolves only when disconnect() is called explicitly
				// or the tab is destroyed (browser releases the lock automatically).
				await new Promise<void>((resolve) => {
					this._leaderResolve = resolve;
				});

				// Cleanup when leadership ends (also zone-run: clears leadership signals).
				this.ngZone.run(() => this.teardownSse());
			}
		);
		if (!requested) {
			// Web Locks unavailable (non-secure context, old browser, test DOM).
			// Leader election -- and thus the SSE connection -- cannot run, so
			// degrade gracefully: mark not-live and notify the user once.
			this.handleRealtimeUnavailable();
		}
	}

	/**
	 * Reflects the "real-time unavailable" state on the connection badge and shows
	 * a one-time dismissible notice. Called when the Web Locks API is missing.
	 */
	private handleRealtimeUnavailable(): void {
		console.warn(
			'[SseEventService] Web Locks API unavailable (requires a secure context); ' +
				'real-time updates and presence are disabled for this session.'
		);
		this._connectionState.set('disconnected');
		if (!this.notifiedRealtimeUnavailable) {
			this.notifiedRealtimeUnavailable = true;
			this.snackBar.open(
				'Real-time updates, presence, and saving are unavailable in this environment. ' +
					'This typically requires a secure (HTTPS) connection -- contact your administrator.',
				'Dismiss'
			);
		}
	}

	/** Resolver for the leadership promise — call to release the lock explicitly. */
	private _leaderResolve: (() => void) | null = null;

	private teardownSse(): void {
		this._isConnectionLeader.set(false);
		this.reconnectPaused = false;
		this.reconnectAttempts = 0;
		this.firstFailureAt = 0;
		this.authExpired = false;
		// True teardown: abandon any in-flight readiness poll loop (bumps the generation) so a
		// scheduled retry from before teardown cannot resurrect the ready state afterwards.
		this.cancelReadinessPoll();
		this.markNotReady();
		this.clearRecoveryListeners();
		if (this.abortController) {
			this.abortController.abort();
			this.abortController = null;
		}
		if (this._leaderResolve) {
			this._leaderResolve();
			this._leaderResolve = null;
		}
	}

	// ─── BroadcastChannel (event distribution) ─────────────────────────

	private handleChannelMessage(msg: sseChannelMessage): void {
		switch (msg.type) {
			case 'artifact':
				this._artifactEvents.next(msg.payload);
				break;
			case 'branch':
				this._branchEvents.next(msg.payload);
				break;
			case 'state':
				if (msg.payload === 'connected') {
					// This browser has (had) a live connection — a later own-socket open by this
					// tab (e.g. after being promoted to leader) is a reconnect, not a cold start.
					this.browserWasConnected = true;
				}
				if (!this.isLeader) {
					this._connectionState.set(msg.payload);
					// The leader only broadcasts `connected` after readiness is confirmed, so a
					// follower mirrors readiness directly from connection state. Any non-connected
					// state means saves must be blocked on this follower too.
					this._serverReady.set(msg.payload === 'connected');
				}
				break;
			case 'connectionId':
				if (!this.isLeader) {
					this._sseConnectionId = msg.payload;
				}
				break;
			case 'presenceUpdate':
				this._presenceUpdates.next(msg.payload);
				break;
			case 'reestablished':
				// Leader reconnected after a drop; followers must also reset dedup and refresh.
				if (!this.isLeader) {
					// Followers refetch on resync too, so mute their warmup error popups as well.
					this.errorService.suppressHttpErrorsFor(
						RESYNC_ERROR_MUTE_MS
					);
					this._connectionReestablished.next();
				}
				break;
			case 'force-reconnect':
				// A follower requested an immediate reconnect (manual action). Only the leader
				// holds the socket, so only the leader acts.
				if (this.isLeader) {
					this.reconnectNow();
				}
				break;
			case 'request-state':
				// A new tab is asking for current state — leader responds
				if (this.isLeader) {
					this.broadcastState(this._connectionState());
					if (this._sseConnectionId) {
						this.channel?.postMessage({
							type: 'connectionId',
							payload: this._sseConnectionId,
						} as sseChannelMessage);
					}
				}
				break;
		}
	}

	// ─── SSE Connection (leader only) ──────────────────────────────────

	private startConnection(): void {
		this._connectionState.set('connecting');
		this.broadcastState('connecting');
		this.abortController = new AbortController();

		const sseUrl = this.buildSseUrl();
		const headers: Record<string, string> = {};

		if (environment.authScheme !== 'NONE' && this.currentUserId) {
			headers['osee.account.id'] = this.currentUserId;
			headers['Authorization'] = this.currentUserId;
		}

		fetchEventSource(sseUrl, {
			method: 'GET',
			headers,
			signal: this.abortController.signal,
			openWhenHidden: true,

			onopen: async (response) => {
				if (response.ok) {
					this.onOpenSuccess();
				} else if (response.status === 401 || response.status === 403) {
					// Auth expired: retrying the socket only burns backoff — the session must be
					// re-established (page reload through the auth flow). Park and stop retrying.
					this.handleAuthExpired();
					throw new Error(`SSE auth failed: ${response.status}`);
				} else {
					// Non-OK transport (5xx, etc.): throw so fetch-event-source stops its own retry
					// and our onerror applies backoff.
					throw new Error(`SSE open failed: ${response.status}`);
				}
			},

			onmessage: (event) => {
				if (event.event === 'connected') {
					this._sseConnectionId = event.data;
					this.channel?.postMessage({
						type: 'connectionId',
						payload: event.data,
					} as sseChannelMessage);
				} else if (event.event === 'artifactChanged') {
					this.handleArtifactMessage(event.data);
				} else if (event.event === 'branchChanged') {
					this.handleBranchMessage(event.data);
				} else if (event.event === 'presenceUpdate') {
					this.handlePresenceUpdate(event.data);
				}
			},

			onerror: (_err) => {
				// Auth failures don't retry via backoff — stop the loop and stay parked.
				if (this.authExpired) {
					throw _err instanceof Error
						? _err
						: new Error('SSE auth failed');
				}
				// Returning a number tells fetch-event-source how long to wait before retrying;
				// throwing stops it. We keep retrying with capped jitter until a sustained failure
				// window elapses, then pause and resume via recovery triggers.
				this.reconnectAttempts++;
				if (this.firstFailureAt === 0) {
					this.firstFailureAt = Date.now();
				}
				this.markNotReady();
				this._connectionState.set('connecting');
				this.broadcastState('connecting');

				const failingForMs = Date.now() - this.firstFailureAt;
				if (
					failingForMs >= RECONNECT_GIVE_UP_WINDOW_MS ||
					!navigator.onLine
				) {
					this.pauseReconnect();
					throw new Error('SSE reconnect paused (give-up window)');
				}
				// Clamp so the next retry (and its give-up check) lands no later than the deadline.
				const remainingToGiveUp =
					RECONNECT_GIVE_UP_WINDOW_MS - failingForMs;
				const delay = Math.min(
					this.nextBackoffDelayMs(),
					Math.max(0, remainingToGiveUp)
				);
				return delay;
			},

			onclose: () => {
				this.markNotReady();
				// fetch-event-source treats a normal stream-end as terminal; throw so it routes
				// through onerror's backoff/pause instead of silently stopping.
				this._connectionState.set('connecting');
				this.broadcastState('connecting');
				throw new Error('SSE stream closed by server');
			},
		});
	}

	/**
	 * Handles a successful socket (re)open. The socket may be open before the server can serve
	 * data, so stay `connecting` and poll readiness before going `connected` / resyncing.
	 */
	private onOpenSuccess(): void {
		this.reconnectAttempts = 0;
		this.firstFailureAt = 0;
		this.authExpired = false;
		this._serverReady.set(false);
		this._connectionState.set('connecting');
		this.broadcastState('connecting');
		this.pollReadiness();
	}

	/**
	 * Polls the readiness probe until ready, then goes `connected`, enables saves, and fires resync
	 * (when the open followed a gap). A superseded generation abandons itself. Leader only.
	 */
	private pollReadiness(attempt = 0): void {
		const pollId =
			attempt === 0 ? ++this.readinessPollId : this.readinessPollId;
		if (attempt === 0) {
			this.readinessStartedAt = Date.now();
		}
		this.http
			.get(READINESS_URL, {
				observe: 'response',
				context: new HttpContext().set(SKIP_LOADING, true),
			})
			.subscribe({
				next: () => this.onReady(pollId),
				error: () => {
					if (pollId !== this.readinessPollId) {
						return;
					}
					// Bound the readiness phase so a socket that stays open but never ready still
					// gives up and goes disconnected.
					if (
						Date.now() - this.readinessStartedAt >=
						RECONNECT_GIVE_UP_WINDOW_MS
					) {
						if (this.abortController) {
							this.abortController.abort();
							this.abortController = null;
						}
						this.pauseReconnect();
						return;
					}
					const next = attempt + 1;
					const delay = Math.min(
						READINESS_POLL_MAX_MS,
						READINESS_POLL_BASE_MS * 2 ** attempt
					);
					setTimeout(() => {
						if (pollId === this.readinessPollId) {
							this.pollReadiness(next);
						}
					}, delay);
				},
			});
	}

	/** Clears the server-ready flag without cancelling the poll loop (used on transient errors). */
	private markNotReady(): void {
		this._serverReady.set(false);
	}

	/** Cancels any running readiness poll (bumps the generation). Use on true restart/teardown. */
	private cancelReadinessPoll(): void {
		this.readinessPollId++;
	}

	private onReady(pollId: number): void {
		if (pollId !== this.readinessPollId) {
			return;
		}
		const followsGap = this.browserWasConnected;
		this._serverReady.set(true);
		this._connectionState.set('connected');
		this.broadcastState('connected');
		this.browserWasConnected = true;
		if (followsGap) {
			this.announceReestablished();
		}
	}

	/**
	 * Exponential backoff with full jitter, capped. Uses the current attempt count.
	 */
	private nextBackoffDelayMs(): number {
		const exp = Math.min(
			RECONNECT_MAX_DELAY_MS,
			RECONNECT_BASE_DELAY_MS * 2 ** (this.reconnectAttempts - 1)
		);
		// Full jitter: random in [0, exp] avoids thundering-herd reconnects across tabs/clients.
		return Math.round(Math.random() * exp);
	}

	/**
	 * Called when the give-up window elapses (or the network is offline). Stops the in-band retry
	 * loop, marks the connection disconnected, and registers recovery triggers — network back
	 * online, tab regains focus, and a periodic timer — so the leader reconnects. The periodic
	 * timer is the catch-all for cases where no DOM edge fires (degraded network, sleep/resume).
	 */
	private pauseReconnect(): void {
		this.reconnectPaused = true;
		this.abortController = null;
		this._connectionState.set('disconnected');
		this.broadcastState('disconnected');
		window.addEventListener('online', this.recoveryHandler);
		document.addEventListener('visibilitychange', this.recoveryHandler);
		if (this.periodicRetryTimer === null) {
			this.periodicRetryTimer = setInterval(
				this.recoveryHandler,
				RECONNECT_PERIODIC_RETRY_MS
			);
		}
	}

	/**
	 * Recovery trigger handler. When paused and the tab is visible and the network is up, reopens
	 * the SSE connection. Only the leader reconnects; followers ride the leader. Auth-expired
	 * connections are not auto-recovered (they need re-authentication).
	 */
	private attemptRecovery(): void {
		if (!this.reconnectPaused || !this.isLeader || this.authExpired) {
			return;
		}
		if (document.visibilityState === 'hidden') {
			return;
		}
		if (!navigator.onLine) {
			return;
		}
		this.clearRecoveryListeners();
		this.reconnectPaused = false;
		this.reconnectAttempts = 0;
		this.firstFailureAt = 0;
		this.startConnection();
	}

	private clearRecoveryListeners(): void {
		window.removeEventListener('online', this.recoveryHandler);
		document.removeEventListener('visibilitychange', this.recoveryHandler);
		if (this.periodicRetryTimer !== null) {
			clearInterval(this.periodicRetryTimer);
			this.periodicRetryTimer = null;
		}
	}

	/**
	 * Marks the connection as auth-expired: stops retrying and parks disconnected. The socket
	 * cannot be healed by reconnecting — the user must re-authenticate (typically a page reload,
	 * which restarts the app through the auth flow). The connection badge reflects `disconnected`.
	 */
	private handleAuthExpired(): void {
		this.authExpired = true;
		this.reconnectPaused = true;
		this.abortController = null;
		this.markNotReady();
		this._connectionState.set('disconnected');
		this.broadcastState('disconnected');
		this.clearRecoveryListeners();
	}

	/**
	 * Manual "Reconnect" action. Re-fetches auth (a failed startup cached the invalid-user
	 * sentinel), then reopens as leader or asks the existing leader to reconnect.
	 */
	reconnectNow(): void {
		if (this._connectionState() === 'connected') {
			return;
		}

		this.userService.refresh();

		if (!this.isLeader) {
			// No leader yet (e.g. failed startup) or we're a follower: ask any leader to reconnect
			// and also attempt leadership ourselves (no-op if the lock is held elsewhere).
			this.channel?.postMessage({
				type: 'force-reconnect',
			} as sseChannelMessage);
			this.resolveUserThenLead();
			return;
		}

		// We are the leader: reset failure state and reopen the socket immediately.
		this.clearRecoveryListeners();
		if (this.abortController) {
			this.abortController.abort();
			this.abortController = null;
		}
		this.reconnectPaused = false;
		this.authExpired = false;
		this.reconnectAttempts = 0;
		this.firstFailureAt = 0;
		this.startConnection();
	}

	/**
	 * Tells this tab and followers to resync (reset dedup, re-GET open views) after a reconnect.
	 * Coalesced within {@link RESYNC_COALESCE_WINDOW_MS} so a flapping reconnect fires once.
	 */
	private announceReestablished(): void {
		const now = Date.now();
		if (now - this.lastResyncAt < RESYNC_COALESCE_WINDOW_MS) {
			return;
		}
		this.lastResyncAt = now;
		// Mute transient error popups from warmup refetches; the indicator conveys the state.
		this.errorService.suppressHttpErrorsFor(RESYNC_ERROR_MUTE_MS);
		this._connectionReestablished.next();
		this.channel?.postMessage({
			type: 'reestablished',
		} as sseChannelMessage);
	}

	private handleArtifactMessage(data: string): void {
		try {
			const parsed = JSON.parse(data) as artifactChangeEvent;
			if (parsed.branchId && parsed.artifactIds && parsed.transactionId) {
				this._artifactEvents.next(parsed);
				// Rebroadcast to follower tabs
				this.channel?.postMessage({
					type: 'artifact',
					payload: parsed,
				} as sseChannelMessage);
			}
		} catch (e) {
			console.warn(
				'[SseEventService] Failed to parse artifactChanged message:',
				data,
				e
			);
		}
	}

	private handleBranchMessage(data: string): void {
		try {
			const parsed = JSON.parse(data) as branchChangeEvent;
			if (parsed.branchId && parsed.changeType) {
				this._branchEvents.next(parsed);
				// Rebroadcast to follower tabs
				this.channel?.postMessage({
					type: 'branch',
					payload: parsed,
				} as sseChannelMessage);
			}
		} catch (e) {
			console.warn(
				'[SseEventService] Failed to parse branchChanged message:',
				data,
				e
			);
		}
	}

	private broadcastState(state: sseConnectionState): void {
		this.channel?.postMessage({
			type: 'state',
			payload: state,
		} as sseChannelMessage);
	}

	private buildSseUrl(): string {
		const url = apiURL as string;
		if (url === '' || url === window.location.origin) {
			return '/orcs/sse/events';
		}
		return `${url}/orcs/sse/events`;
	}

	// ─── Presence helpers ──────────────────────────────────────────────

	private handlePresenceUpdate(data: string): void {
		try {
			const update = JSON.parse(data) as presenceUpdate;
			if (update.context && update.users) {
				this._presenceUpdates.next(update);
				this.channel?.postMessage({
					type: 'presenceUpdate',
					payload: update,
				} as sseChannelMessage);
			}
		} catch (e) {
			console.warn(
				'[SseEventService] Failed to parse presenceUpdate:',
				data,
				e
			);
		}
	}
}
