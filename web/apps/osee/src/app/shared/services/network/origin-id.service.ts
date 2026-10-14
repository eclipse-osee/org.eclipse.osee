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
 * Holds a stable per-tab origin id, minted once when the tab loads.
 *
 * The id is:
 * - Sent on every outgoing request via the {@code originId} HTTP interceptor
 *   (as the {@code X-Origin-Id} header).
 * - Echoed back by the server on the resulting SSE change event.
 *
 * The originating tab recognizes its own echo by matching the event's {@code originId}
 * against this id and ignores it (client-side self-dedup), so the server never needs to
 * exclude any connection. Every other tab/browser/user treats the event as external and
 * refreshes as normal.
 *
 * Per-tab (not per-connection): survives SSE leader handoff, and is known before any request
 * is sent — so self-recognition is order-independent and free of any echo-beats-response race.
 */
@Injectable({
	providedIn: 'root',
})
export class OriginIdService {
	private readonly _originId: string = this.mint();

	/** The stable origin id for this tab. */
	get originId(): string {
		return this._originId;
	}

	private mint(): string {
		if (
			typeof crypto !== 'undefined' &&
			typeof crypto.randomUUID === 'function'
		) {
			return crypto.randomUUID();
		}
		// Fallback for environments without crypto.randomUUID (e.g. older test runners).
		return `origin-${Date.now()}-${Math.random().toString(36).slice(2)}`;
	}
}
