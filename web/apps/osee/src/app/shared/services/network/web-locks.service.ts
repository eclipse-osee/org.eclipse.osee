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
import { Injectable } from '@angular/core';

/**
 * Thin wrapper over the Web Locks API used for cross-tab leader election (the
 * single SSE connection and the presence heartbeat leader).
 *
 * Centralizes the capability check in one place so consumers never touch
 * `navigator.locks` directly: the API is unavailable on non-secure (http://)
 * contexts, older browsers, and unit-test DOMs, where calling it throws. Callers
 * check {@link isSupported} to drive user-facing degradation and use
 * {@link requestLock} to acquire a lock only when it is available.
 */
@Injectable({
	providedIn: 'root',
})
export class WebLocksService {
	/** Whether the Web Locks API is available in this environment. */
	readonly isSupported =
		typeof navigator !== 'undefined' && !!navigator.locks?.request;

	/**
	 * Acquires the named lock and holds it for the lifetime of `whileHeld`'s
	 * returned promise (resolve it to release). No-ops when the API is
	 * unavailable.
	 *
	 * @returns true if the lock request was issued, false if unsupported.
	 */
	requestLock(name: string, whileHeld: () => Promise<void>): boolean {
		if (!this.isSupported) {
			return false;
		}
		navigator.locks.request(name, whileHeld);
		return true;
	}

	/**
	 * Returns the names of all locks this origin currently HOLDS or is WAITING for (held + pending).
	 * The browser releases a lock the instant the tab holding it goes away (clean close, crash, or
	 * kill), so a name absent from this set means the owning tab is truly gone -- a reliable
	 * cross-tab liveness signal, unlike a best-effort unload message.
	 * <p>
	 * Pending is included deliberately: a per-tab lock briefly sits in `pending` between request and
	 * grant (a tab just starting up). Counting only `held` would treat that live-but-starting tab as
	 * gone. Since each tab's per-tab lock name is unique, a name appearing in `held` OR `pending`
	 * always means a live tab -- never a spurious "departed" reading. Empty array when unavailable.
	 */
	async queryActiveLockNames(): Promise<string[]> {
		if (!this.isSupported || !navigator.locks.query) {
			return [];
		}
		const snapshot = await navigator.locks.query();
		const names = [...(snapshot.held ?? []), ...(snapshot.pending ?? [])]
			.map((lock) => lock.name)
			.filter((name): name is string => !!name);
		return names;
	}
}
