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
import {
	Observable,
	Subject,
	debounceTime,
	filter,
	groupBy,
	merge,
	mergeMap,
	share,
} from 'rxjs';

/**
 * Configuration for a {@link OriginAwareEventStream}.
 *
 * @typeParam T the raw event type flowing through the stream
 */
export type originAwareEventStreamConfig<T> = {
	/** Returns the origin id of the tab that this client represents (stable per tab). */
	myOriginId: () => string;
	/** Extracts the origin id carried on an event, or undefined/null when none. */
	getOriginId: (event: T) => string | undefined | null;
	/**
	 * Returns a stable identity key for the event, used to suppress duplicate delivery of the
	 * same logical event (e.g. SSE reconnect replay, transport loopback) and, when debouncing is
	 * enabled, to group events by target.
	 *
	 * Required when {@link originAwareEventStreamConfig.suppressDuplicates} is true or
	 * {@link originAwareEventStreamConfig.debounceMs} > 0. Omit for event families with no stable
	 * per-event identity (e.g. branch notifications, which have no transaction id) and leave
	 * duplicate suppression disabled — those consumers GET current state and are idempotent.
	 */
	getEventKey?: (event: T) => string;
	/**
	 * Whether to suppress duplicate delivery via the recent-key set. Default true. Set false for
	 * event families lacking a stable per-event id, where a bounded key set could wrongly suppress
	 * a genuinely new event that happens to share a key. Requires {@link getEventKey} when true.
	 */
	suppressDuplicates?: boolean;
	/**
	 * Optional per-key debounce window in milliseconds. When > 0, rapid events sharing the same
	 * key are coalesced and only the latest is emitted after the window. Default 0 (disabled).
	 */
	debounceMs?: number;
	/**
	 * Maximum number of recent event keys retained for duplicate suppression. Bounds memory;
	 * oldest entries are evicted first. Default 500.
	 */
	maxRecentKeys?: number;
};

const DEFAULT_MAX_RECENT_KEYS = 500;

/**
 * Reusable primitive that implements the client-side rules of the real-time change-propagation
 * architecture for any event type:
 *
 * <ul>
 *   <li><b>Self-echo drop:</b> the server broadcasts every change to all connections including the
 *       originator; an event whose {@code originId} equals this tab's own id is this tab's own echo
 *       and is dropped (the acting tab already refreshed locally via {@link emitLocal}).</li>
 *   <li><b>Duplicate-delivery suppression:</b> the same logical event arriving twice at this tab
 *       (reconnect replay, transport loopback) is suppressed via a bounded recent-key set. This is
 *       NOT ordering/freshness — consumers GET current state on notify, so order is irrelevant.</li>
 *   <li><b>Optional per-key debounce:</b> coalesces rapid events sharing a key into one.</li>
 *   <li><b>Local emit:</b> {@link emitLocal} injects the acting tab's own change immediately for
 *       R1 latency; it bypasses self-echo/dedup and is delivered as-is.</li>
 * </ul>
 *
 * Not an Angular service — services compose it (see {@code ArtifactChangeNotificationService},
 * {@code BranchChangeEventService}). Wire {@link input} to the raw SSE stream, expose
 * {@link output} to consumers, call {@link emitLocal} after local mutations, and {@link reset} on
 * reconnect.
 *
 * @typeParam T the event type
 */
export class OriginAwareEventStream<T> {
	private readonly myOriginId: () => string;
	private readonly getOriginId: (event: T) => string | undefined | null;
	private readonly getEventKey?: (event: T) => string;
	private readonly debounceMs: number;
	private readonly maxRecentKeys: number;
	private readonly suppressDuplicates: boolean;

	private readonly recentKeys = new Set<string>();

	/** Raw events entering the stream (from SSE, cross-tab relay, or local emit). */
	private readonly _input = new Subject<T>();
	/**
	 * Locally originated events (the acting tab's own change), merged into the output bypassing
	 * the self-echo/dedup filters. Declared before the constructor so it exists when build() runs.
	 */
	private readonly _localEmissions = new Subject<T>();
	/** Filtered events leaving the stream, ready for consumers. */
	private readonly _output: Observable<T>;

	constructor(config: originAwareEventStreamConfig<T>) {
		this.myOriginId = config.myOriginId;
		this.getOriginId = config.getOriginId;
		this.getEventKey = config.getEventKey;
		this.debounceMs = config.debounceMs ?? 0;
		this.maxRecentKeys = config.maxRecentKeys ?? DEFAULT_MAX_RECENT_KEYS;
		this.suppressDuplicates = config.suppressDuplicates ?? true;

		if (
			(this.suppressDuplicates || this.debounceMs > 0) &&
			!this.getEventKey
		) {
			throw new Error(
				'OriginAwareEventStream: getEventKey is required when suppressDuplicates is true or debounceMs > 0'
			);
		}

		this._output = this.build();
	}

	/** The de-duplicated, self-echo-filtered output stream for consumers to subscribe to. */
	get output(): Observable<T> {
		return this._output;
	}

	/**
	 * Feeds a raw event (from the SSE transport) into the stream. Own-echo and duplicate events
	 * are filtered out downstream.
	 */
	next(event: T): void {
		this._input.next(event);
	}

	/**
	 * Injects a locally originated event (the acting tab's own change) directly into the output
	 * for immediate refresh (R1). Bypasses self-echo and duplicate filtering — the caller is
	 * asserting this is a fresh local change. The subsequent server echo of the same change is
	 * dropped by self-echo filtering (matching origin id).
	 */
	emitLocal(event: T): void {
		this._localEmissions.next(event);
	}

	/**
	 * Clears duplicate-suppression state. Call on SSE reconnection: the client does a full GET to
	 * hydrate fresh state and may have missed events, so previously seen keys must not suppress
	 * post-reconnect events.
	 */
	reset(): void {
		this.recentKeys.clear();
	}

	private build(): Observable<T> {
		let filtered = this._input.pipe(
			// Drop this tab's own echo — it already refreshed via emitLocal().
			filter((event) => {
				const originId = this.getOriginId(event);
				return !originId || originId !== this.myOriginId();
			})
		);

		if (this.suppressDuplicates) {
			const getKey = this.getEventKey!;
			filtered = filtered.pipe(
				filter((event) => this.isFirstSeen(getKey(event)))
			);
		}

		const deduped =
			this.debounceMs > 0
				? filtered.pipe(
						groupBy((event) => this.getEventKey!(event)),
						mergeMap((group) =>
							group.pipe(debounceTime(this.debounceMs))
						)
					)
				: filtered;

		// Local emissions bypass the filters and merge into the output as-is. share() makes the
		// output multicast so the dedup/self-echo filters run exactly once regardless of how many
		// consumers subscribe.
		return merge(this._localEmissions, deduped).pipe(share());
	}

	/**
	 * Returns true if the key has not been seen recently (and records it), false if it is a
	 * duplicate. Evicts the oldest key when the bound is reached (Set preserves insertion order).
	 */
	private isFirstSeen(key: string): boolean {
		if (this.recentKeys.has(key)) {
			return false;
		}
		this.recentKeys.add(key);
		if (this.recentKeys.size > this.maxRecentKeys) {
			const oldest = this.recentKeys.values().next().value;
			if (oldest !== undefined) {
				this.recentKeys.delete(oldest);
			}
		}
		return true;
	}
}
