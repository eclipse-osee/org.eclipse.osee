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
import { RetryConfig } from 'rxjs';
import { timer } from 'rxjs';

/**
 * Number of retry attempts for a reconnect-driven refetch before the error is
 * allowed to surface. A transient warm-up failure recovers within a few tries; a
 * genuine, persistent server error should surface after a few attempts, not retry
 * forever.
 */
export const RESYNC_REFETCH_RETRIES = 4;
/** Base backoff delay; the first retry waits this long. */
export const RESYNC_REFETCH_BASE_DELAY_MS = 500;
/** Upper bound on the exponential backoff delay. */
export const RESYNC_REFETCH_MAX_DELAY_MS = 5_000;

/**
 * Shared RxJS {@link RetryConfig} for reconnect-driven refetches (SSE resync/
 * `repeat`-based streams). The server may still be warming up when it comes back,
 * so a GET can transiently fail (e.g. 500). Retries a bounded number of times with
 * exponential backoff, base-delay first.
 *
 * `attempt` is 1-based (first retry = 1), so the exponent uses `attempt - 1` to make
 * the first retry wait the base delay (500ms), then 1s, 2s, ... capped at the max.
 * Callers still apply their own `catchError` fallback (the last-known value) so the
 * error never escapes into `repeat`/`toSignal`, which would kill the stream.
 */
export function resyncRefetchConfig(): RetryConfig {
	return {
		count: RESYNC_REFETCH_RETRIES,
		delay: (_err, attempt) =>
			timer(
				Math.min(
					RESYNC_REFETCH_MAX_DELAY_MS,
					RESYNC_REFETCH_BASE_DELAY_MS * 2 ** (attempt - 1)
				)
			),
	};
}
