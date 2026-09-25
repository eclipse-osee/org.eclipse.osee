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
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	SseEventService,
	WebLocksService,
	presenceUpdate,
} from '@osee/shared/services/network';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { UserPresenceService } from './user-presence.service';

/**
 * Behavioral spec for UserPresenceService. Presence leadership follows SSE-connection leadership,
 * so leadership is forced via the SSE stub's `isConnectionLeader` signal; the heartbeat POST is
 * asserted via HttpTestingController; timers (debounce + interval) are driven with fake timers. The
 * WebLocks stub models the per-tab liveness locks the departed-tab prune queries.
 *
 * These tests pin the OBSERVABLE contract (what the leader POSTs, what a follower does on unload,
 * how SSE updates fan out) so the coordination internals can be refactored safely.
 */

const HEARTBEAT_URL = `${apiURL}/orcs/sse/presence/heartbeat`;
const TAB_LOCK_PREFIX = 'osee-presence-tab-';
/** Mirrors the service's HEARTBEAT_INTERVAL_MS. */
const HEARTBEAT_INTERVAL_MS = 15_000;

/**
 * Controllable Web Locks stub for the per-tab liveness locks the prune queries. `activeLockNames`
 * models the origin's currently active locks (held + pending); this tab's own per-tab lock is added
 * automatically on requestLock, and tests add/remove entries to simulate OTHER tabs being alive or
 * departing. Leadership is NOT modeled here -- it follows the SSE stub's `isConnectionLeader`.
 */
class WebLocksStub {
	/** Set true to model an environment WITHOUT Web Locks (query returns nothing). */
	simulateNoLocks = false;
	get isSupported() {
		return !this.simulateNoLocks;
	}
	readonly activeLockNames = new Set<string>();

	requestLock(name: string, whileHeld: () => Promise<void>): boolean {
		if (name.startsWith(TAB_LOCK_PREFIX)) {
			this.activeLockNames.add(name);
		}
		void whileHeld();
		return true;
	}

	async queryActiveLockNames(): Promise<string[]> {
		return this.simulateNoLocks ? [] : [...this.activeLockNames];
	}
}

type presenceInternals = {
	handlePresenceChannelMessage(msg: unknown): void;
	pruneDepartedTabs(): Promise<void>;
	allTabContexts: Map<string, Set<string>>;
	tabId: string;
	isPresenceLeader: boolean;
};

/** Minimal DestroyRef stub for watchContext; collects teardown callbacks (unused in these tests). */
function destroyRefStub() {
	const noop = (): void => undefined;
	return { onDestroy: (_cb: () => void) => noop } as unknown as never;
}

describe('UserPresenceService', () => {
	let presenceUpdates$: Subject<presenceUpdate>;
	let webLocks: WebLocksStub;
	let http: HttpTestingController;

	function configure(isLeader: boolean) {
		presenceUpdates$ = new Subject<presenceUpdate>();
		webLocks = new WebLocksStub();

		// Presence leadership follows SSE-connection leadership: the leader is the tab that owns
		// the SSE connection (and thus the live sinkId). Drive it via this signal.
		const sseStub: Partial<SseEventService> = {
			serverReady: signal(true),
			isConnectionLeader: signal(isLeader),
			sseConnectionId: '7',
			presenceUpdates$: presenceUpdates$,
		};

		TestBed.configureTestingModule({
			providers: [
				UserPresenceService,
				provideHttpClient(),
				provideHttpClientTesting(),
				{ provide: SseEventService, useValue: sseStub },
				{ provide: WebLocksService, useValue: webLocks },
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		http = TestBed.inject(HttpTestingController);
	}

	/**
	 * Injects the service and flushes the leadership effect so `startLeading()` runs (the effect
	 * reads `isConnectionLeader` and starts/stops the heartbeat loop). Mirrors how the effect fires
	 * during change detection in the app.
	 */
	function injectService(): UserPresenceService {
		const service = TestBed.inject(UserPresenceService);
		TestBed.tick(); // flush the leadership effect
		return service;
	}

	beforeEach(() => {
		TestBed.resetTestingModule();
		vi.useFakeTimers();
	});

	afterEach(() => {
		// Drain any heartbeats a test didn't explicitly assert on, then verify none leaked.
		http.match(HEARTBEAT_URL).forEach((r) => r.flush({}));
		http.verify();
		vi.useRealTimers();
	});

	function api(service: UserPresenceService): presenceInternals {
		return service as unknown as presenceInternals;
	}

	it('leader heartbeats its own watched context', () => {
		configure(true);
		const service = injectService();
		const ctx = signal('branchA/artifact1');
		service.watchContext(ctx, destroyRefStub());
		TestBed.tick(); // run the watchContext effect so localContexts is populated

		// Leader sends an initial heartbeat on becoming leader, then again (debounced) on the watch.
		vi.advanceTimersByTime(1000);
		// Coalesce any queued heartbeats; assert the latest carries the context + numeric sinkId.
		const reqs = http.match(HEARTBEAT_URL);
		expect(reqs.length).toBeGreaterThan(0);
		const last = reqs[reqs.length - 1];
		expect(
			(last.request.body as { contexts: string[] }).contexts
		).toContain('branchA/artifact1');
		expect((last.request.body as { sinkId: number }).sinkId).toBe(7);
		reqs.forEach((r) => r.flush({}));
	});

	it('a follower does NOT send a heartbeat', () => {
		configure(false);
		const service = injectService();
		const ctx = signal('branchA/artifact1');
		service.watchContext(ctx, destroyRefStub());
		vi.advanceTimersByTime(1000);
		http.expectNone(HEARTBEAT_URL);
	});

	it('leader with no context of its own still heartbeats a followers announced context', () => {
		// The leader tab watches nothing itself (e.g. it is on the ACTRA world page) but must still
		// report a follower's context -- this is the whole reason presence runs in every tab.
		configure(true);
		const service = injectService();
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		vi.advanceTimersByTime(1000);
		const reqs = http.match(HEARTBEAT_URL);
		const last = reqs[reqs.length - 1];
		expect(
			(last.request.body as { contexts: string[] }).contexts
		).toContain('branchB/artifact9');
		reqs.forEach((r) => r.flush({}));
	});

	it('an idle leader (empty aggregate) sends no heartbeat', () => {
		// Leader watches nothing and no follower has announced -> nothing to report -> no POST.
		configure(true);
		injectService();
		vi.advanceTimersByTime(HEARTBEAT_INTERVAL_MS * 2);
		http.expectNone(HEARTBEAT_URL);
	});

	it('an unchanged re-announce does not trigger an extra heartbeat', () => {
		configure(true);
		const service = injectService();
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		vi.advanceTimersByTime(1000);
		http.match(HEARTBEAT_URL).forEach((r) => r.flush({}));

		// Same tab re-announces the SAME contexts (as the periodic request-announce would elicit).
		// The aggregate is unchanged, so no new heartbeat must be scheduled.
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		vi.advanceTimersByTime(1000);
		http.expectNone(HEARTBEAT_URL);
	});

	it('leader drops a context when the owning follower announces it is gone', () => {
		configure(true);
		const service = injectService();
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		// Let the non-empty heartbeat go out first (real sequence: viewing, then leaving), so the
		// subsequent empty transition is a genuine "clear" the idle gate must still send.
		vi.advanceTimersByTime(1000);
		http.match(HEARTBEAT_URL).forEach((r) => r.flush({}));

		// Follower now announces an empty set (left the context / closing).
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: [],
		});
		vi.advanceTimersByTime(1000);
		const reqs = http.match(HEARTBEAT_URL);
		const last = reqs[reqs.length - 1];
		expect(
			(last.request.body as { contexts: string[] }).contexts
		).not.toContain('branchB/artifact9');
		reqs.forEach((r) => r.flush({}));
	});

	it('drops a departed tab whose per-tab lock is gone (reliable prune)', async () => {
		configure(true);
		const service = injectService();
		await Promise.resolve(); // let the leader startup prune settle
		// A follower is alive: it holds its per-tab lock and has announced a context.
		webLocks.activeLockNames.add(`${TAB_LOCK_PREFIX}other-tab`);
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		expect(api(service).allTabContexts.has('other-tab')).toBe(true);

		// The tab closes: the browser releases its per-tab lock (no farewell message needed).
		webLocks.activeLockNames.delete(`${TAB_LOCK_PREFIX}other-tab`);

		// The lock-based prune drops the departed tab.
		await api(service).pruneDepartedTabs();
		expect(api(service).allTabContexts.has('other-tab')).toBe(false);
	});

	it('keeps a tab whose per-tab lock is still active (held or pending)', async () => {
		configure(true);
		const service = injectService();
		await Promise.resolve();
		webLocks.activeLockNames.add(`${TAB_LOCK_PREFIX}live-tab`);
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'live-tab',
			contexts: ['branchB/artifact9'],
		});

		await api(service).pruneDepartedTabs();
		expect(api(service).allTabContexts.has('live-tab')).toBe(true);
	});

	it('does not prune when the lock query returns nothing (cannot distinguish gone from no-data)', async () => {
		configure(true);
		const service = injectService();
		await Promise.resolve();
		// A follower announced, but its lock is not in the (simulated empty) query result.
		api(service).handlePresenceChannelMessage({
			type: 'announce',
			tabId: 'other-tab',
			contexts: ['branchB/artifact9'],
		});
		webLocks.simulateNoLocks = true; // query yields [] -> prune must be a no-op

		await api(service).pruneDepartedTabs();
		expect(api(service).allTabContexts.has('other-tab')).toBe(true);
	});

	it('best-effort immediate leave: pagehide announces empty over the channel', () => {
		configure(false);
		const service = injectService();
		const posted: unknown[] = [];
		(
			service as unknown as {
				presenceChannel: { postMessage(m: unknown): void };
			}
		).presenceChannel.postMessage = (m: unknown) => posted.push(m);

		window.dispatchEvent(new Event('pagehide'));

		expect(posted).toContainEqual({
			type: 'announce',
			tabId: api(service).tabId,
			contexts: [],
		});
	});

	it('leader sends an authenticated keepalive leave for its sinkId on pagehide', () => {
		configure(true);
		injectService();
		// Flush the leader's startup heartbeat so it doesn't leak into the assertion below.
		vi.advanceTimersByTime(1000);
		http.match(HEARTBEAT_URL).forEach((r) => r.flush({}));

		const fetchSpy = vi
			.spyOn(globalThis, 'fetch')
			.mockResolvedValue(new Response(null, { status: 200 }));
		try {
			window.dispatchEvent(new Event('pagehide'));

			expect(fetchSpy).toHaveBeenCalledTimes(1);
			const [url, init] = fetchSpy.mock.calls[0];
			expect(String(url)).toContain('/orcs/sse/presence/leave');
			expect(init?.keepalive).toBe(true);
			expect(JSON.parse(init?.body as string)).toEqual({ sinkId: 7 });
		} finally {
			fetchSpy.mockRestore();
		}
	});

	it('a follower does NOT send a server leave on pagehide (it owns no connection/sinkId)', () => {
		configure(false);
		injectService();
		const fetchSpy = vi
			.spyOn(globalThis, 'fetch')
			.mockResolvedValue(new Response(null, { status: 200 }));
		try {
			window.dispatchEvent(new Event('pagehide'));
			expect(fetchSpy).not.toHaveBeenCalled();
		} finally {
			fetchSpy.mockRestore();
		}
	});

	it('fans out an SSE presence update to a watcher, excluding self', () => {
		configure(false); // follower: no heartbeat noise
		const service = injectService();
		const ctx = signal('branchA/artifact1');
		const handle = service.watchContext(ctx, destroyRefStub());
		// Flush the watchContext effect so the context is registered before the SSE update.
		TestBed.tick();

		presenceUpdates$.next({
			context: 'branchA/artifact1',
			users: [
				{ userId: '0', userName: 'me' }, // self (MockUserResponse.id === '0')
				{ userId: '42', userName: 'Other' },
			],
		});

		const others = handle.users();
		expect(others).toHaveLength(1);
		expect(others[0].userId).toBe('42');
	});
});
