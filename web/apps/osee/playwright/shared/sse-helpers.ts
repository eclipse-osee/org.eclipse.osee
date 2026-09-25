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
import { Page, Request, expect } from '@ngx-playwright/test';

/**
 * Substrings of URLs that belong to the long-lived real-time streams (SSE) rather than to a
 * page's discrete data loads. These requests stay "in flight" for the life of the connection,
 * so they must be excluded from any network-idle calculation.
 *
 * OSEE opens its SSE stream via `fetchEventSource` (see `SseEventService`); the connection URL
 * contains `/events`. Presence traffic rides the same real-time channel and should likewise not
 * gate "the page finished loading".
 */
const REALTIME_URL_KEYWORDS: readonly string[] = ['/events', 'presence'];

const isRealtime = (url: string): boolean =>
	REALTIME_URL_KEYWORDS.some((keyword) => url.includes(keyword));

/**
 * Waits until a page's *non-streaming* network activity has been quiet for `idleMs`, ignoring
 * the perpetual SSE/real-time stream.
 *
 * Why this exists: Playwright's built-in `waitForLoadState('networkidle')` never resolves on an
 * SSE-connected page — the `fetchEventSource` request is counted as in flight for the life of the
 * connection, so the active-connection count never returns to zero. This tracks only discrete
 * requests (everything except {@link REALTIME_URL_KEYWORDS}) and resolves once none have been in
 * flight for `idleMs`, using a debounce reset on each request (the recommended SSE-safe pattern).
 *
 * Use this to know a page has finished its data loads (initial load, a refetch after a mutation,
 * etc.) before interacting — the SSE-friendly equivalent of `networkidle`. Prefer a concrete web
 * assertion (waiting for a specific element/value) when one cleanly expresses readiness; reach for
 * this when "all the page's data calls have gone quiet" is the real precondition and no single UI
 * signal captures it (e.g. multiple parallel refetches must all settle).
 *
 * @param page   the page to observe
 * @param idleMs quiet window with no discrete request in flight before resolving (default 750)
 * @param timeoutMs bound so a page that never fully quiesces cannot hang the test (default 30000)
 */
export async function waitForNetworkIdleIgnoringSse(
	page: Page,
	{
		idleMs = 750,
		timeoutMs = 30000,
	}: { idleMs?: number; timeoutMs?: number } = {}
): Promise<void> {
	let inFlight = 0;
	let idleTimer: ReturnType<typeof setTimeout> | undefined;
	let deadlineTimer: ReturnType<typeof setTimeout> | undefined;
	let resolveIdle: (() => void) | undefined;

	// (Re)arm the idle timer: resolve after `idleMs` of no discrete request in flight. Any new
	// request clears it via onRequest, so it only fires once the page has truly gone quiet.
	const armIdleTimer = () => {
		if (idleTimer) {
			clearTimeout(idleTimer);
		}
		idleTimer = setTimeout(() => resolveIdle?.(), idleMs);
	};

	const onRequest = (request: Request) => {
		if (isRealtime(request.url())) {
			return;
		}
		inFlight++;
		if (idleTimer) {
			clearTimeout(idleTimer);
			idleTimer = undefined;
		}
	};

	const onSettled = (request: Request) => {
		if (isRealtime(request.url())) {
			return;
		}
		// Guard against dropping below zero for requests already in flight when we attached.
		inFlight = Math.max(0, inFlight - 1);
		if (inFlight === 0) {
			armIdleTimer();
		}
	};

	page.on('request', onRequest);
	page.on('requestfinished', onSettled);
	page.on('requestfailed', onSettled);

	try {
		await new Promise<void>((resolve) => {
			resolveIdle = resolve;
			// Bound the wait so a page that never quiesces cannot hang the test.
			deadlineTimer = setTimeout(resolve, timeoutMs);
			// If nothing is in flight at attach time, start counting down immediately.
			if (inFlight === 0) {
				armIdleTimer();
			}
		});
	} finally {
		if (idleTimer) {
			clearTimeout(idleTimer);
		}
		if (deadlineTimer) {
			clearTimeout(deadlineTimer);
		}
		page.off('request', onRequest);
		page.off('requestfinished', onSettled);
		page.off('requestfailed', onSettled);
	}
}

/**
 * Waits until the page's SSE stream is live. The toolbar user-display exposes the connection state
 * as a `status` element whose accessible name is "Real-time sync active." once connected. Cross-user
 * propagation cannot be observed before this — an event committed while a tab is still connecting is
 * never delivered to it (GET-on-notify has no notify for a missed event).
 */
export async function waitForRealtimeConnected(
	page: Page,
	{ timeoutMs = 30000 }: { timeoutMs?: number } = {}
): Promise<void> {
	await expect(
		page.getByRole('status', { name: 'Real-time sync active.' })
	).toBeVisible({ timeout: timeoutMs });
}

/**
 * Waits until a page is fully ready to both act and observe real-time changes: its discrete data
 * loads have gone quiet AND its SSE stream is connected. This is the precondition for reliable
 * two-user SSE assertions — clicking or asserting before both hold is the common source of flake
 * (a click that doesn't drive the handler, or a tab that misses the event because it wasn't yet
 * subscribed). Call it on every participating page after navigation before the cross-user steps.
 */
export async function waitForPageReadyForSse(
	page: Page,
	options: { idleMs?: number; timeoutMs?: number } = {}
): Promise<void> {
	await waitForRealtimeConnected(page, { timeoutMs: options.timeoutMs });
	await waitForNetworkIdleIgnoringSse(page, options);
}
