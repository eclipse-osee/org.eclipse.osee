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
import { Injectable, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
	Observable,
	debounceTime,
	filter,
	groupBy,
	map,
	mergeMap,
	share,
	toArray,
} from 'rxjs';
import {
	OriginIdService,
	OriginAwareEventStream,
	SseEventService,
	branchChangeEvent,
	branchChangeType,
} from '@osee/shared/services/network';

export { branchChangeEvent, branchChangeType };

/**
 * Service that consumes branch-change SSE events and exposes filtered
 * observables for branch metadata changes (rename, archive, commit, etc.).
 *
 * Components that need to react to branch state changes subscribe here
 * instead of polling via `uiService.update`.
 */
@Injectable({
	providedIn: 'root',
})
export class BranchChangeEventService {
	private readonly sseEventService = inject(SseEventService);
	private readonly originIdService = inject(OriginIdService);
	private readonly destroyRef = inject(DestroyRef);

	private initialized = false;

	/**
	 * Generic self-dedup/echo-suppression stream for branch changes. Drops this tab's own echoes
	 * (by originId) and suppresses duplicate delivery (by branchId/changeType). See
	 * {@link OriginAwareEventStream}.
	 */
	private readonly stream = new OriginAwareEventStream<branchChangeEvent>({
		myOriginId: () => this.originIdService.originId,
		getOriginId: (event) => event.originId,
		// Branch events have no stable per-event id (no transaction id), so permanent duplicate
		// suppression stays off — a genuine later change (e.g. state_changed after an earlier one)
		// must still get through. Self-echo drop by originId still applies. Burst coalescing (a
		// single op producing same-(branch,type) events across the topic + desktop-bridge paths) is
		// done downstream in `branchChanges$`, where we can keep the richest event rather than the
		// last (payloads differ — only the topic copy carries associatedArtifactId).
		suppressDuplicates: false,
	});

	/** Coalescing window (ms) for a burst of same-(branch, type) events from one operation. */
	private static readonly BURST_COALESCE_MS = 300;

	/**
	 * Observable of all branch change events. A single logical operation — especially a DTC delete
	 * (setState + archive + terminal) — surfaces the same (branchId, changeType) multiple times
	 * within milliseconds, both on the SSE topic path and the desktop-bridge relay. Coalesce each
	 * such burst to a single emission per (branchId, changeType), keeping the **richest** event in
	 * the window (prefer one carrying `associatedArtifactId`, which only the topic copy has and the
	 * workflow editor needs) rather than the last. GET-on-notify makes the small delay imperceptible;
	 * a genuinely later change (seconds apart) forms a new burst and is delivered normally. The
	 * acting tab's own change comes via `emitLocal` (bypasses this) for immediate R1 refresh.
	 */
	readonly branchChanges$: Observable<branchChangeEvent> =
		this.stream.output.pipe(
			// Group by (branchId, changeType); each group auto-completes after the coalesce window
			// of inactivity (which also bounds group accumulation over a long session).
			groupBy((event) => `${event.branchId}:${event.changeType}`, {
				duration: (group) =>
					group.pipe(
						debounceTime(BranchChangeEventService.BURST_COALESCE_MS)
					),
			}),
			// toArray() collects the group's whole burst and emits it once, on group completion.
			// A group only exists after receiving >=1 event, so the array is never empty.
			mergeMap((group) =>
				group.pipe(
					toArray(),
					map((burst) => this.richestEvent(burst))
				)
			),
			// Coalesce once for all derived observables (forBranch, listAffectingChanges$, etc.),
			// not per subscriber.
			share()
		);

	/** Picks the most informative event in a coalesced burst: prefer one with associatedArtifactId. */
	private richestEvent(burst: branchChangeEvent[]): branchChangeEvent {
		return (
			burst.find((event) => !!event.associatedArtifactId) ??
			burst[burst.length - 1]
		);
	}

	/**
	 * Emits after the SSE connection is re-established (on this tab or the leader). Branch change
	 * events may have been missed during the disconnect window, and GET-on-notify has no notify for
	 * a missed event, so consumers of open branch-scoped views should merge this into their refetch
	 * trigger and re-GET current branch state. Fires on both leader and follower tabs.
	 */
	readonly resync$: Observable<void> =
		this.sseEventService.connectionReestablished$;

	/**
	 * Returns an observable that emits when a specific branch's metadata changes.
	 */
	forBranch(branchId: string): Observable<branchChangeEvent> {
		return this.branchChanges$.pipe(
			filter((event) => event.branchId === branchId)
		);
	}

	/**
	 * Returns an observable filtered to a specific branch and change type.
	 */
	forBranchAndType(
		branchId: string,
		changeType: branchChangeType
	): Observable<branchChangeEvent> {
		return this.branchChanges$.pipe(
			filter(
				(event) =>
					event.branchId === branchId &&
					event.changeType === changeType
			)
		);
	}

	/**
	 * Emits when the given branch is rebaselined (update-from-parent). The event's
	 * `newBranchId` is the working branch going forward — the old `branchId` is now
	 * deleted/rebaselined. Consumers refetch, and those that track a branch id should re-point
	 * to `newBranchId`.
	 *
	 * @param oldBranchId the branch id currently held by the consumer (the retired working branch)
	 */
	forRebaseline(oldBranchId: string): Observable<branchChangeEvent> {
		return this.forBranchAndType(oldBranchId, 'rebaselined');
	}

	/**
	 * Change types that add, remove, or alter which branches appear in a branch list/selector.
	 * List views merge {@link listAffectingChanges$} into their load trigger to refresh reactively
	 * (a branch they aren't already watching by id can appear or disappear).
	 */
	private static readonly LIST_AFFECTING_TYPES: ReadonlySet<branchChangeType> =
		new Set<branchChangeType>([
			'created',
			'deleted',
			'purged',
			'renamed',
			'archived',
			'unarchived',
			'type_changed',
			'rebaselined',
		]);

	/**
	 * Emits on any branch change that could alter the contents of a branch list/selector (create,
	 * delete, purge, rename, archive/unarchive, type change, rebaseline). Unlike {@link forBranch},
	 * this is not keyed on a specific branch id, so it catches branches the consumer is not yet
	 * watching (e.g. a newly created branch). Consumers should GET their current list on emit.
	 */
	readonly listAffectingChanges$: Observable<branchChangeEvent> =
		this.branchChanges$.pipe(
			filter((event) =>
				BranchChangeEventService.LIST_AFFECTING_TYPES.has(
					event.changeType
				)
			)
		);

	/**
	 * Initializes the branch event subscription from the SSE transport layer.
	 * Safe to call multiple times — will no-op after first initialization.
	 *
	 * Call this from the app root (AppComponent) alongside the artifact notification init.
	 */
	initialize(): void {
		if (this.initialized) {
			return;
		}
		this.initialized = true;

		// Keep the shared stream active regardless of downstream subscribers.
		this.stream.output
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe();

		// Feed SSE branch events through the stream (drops this tab's own echoes by originId).
		this.sseEventService.branchChanges$
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((event) => {
				this.stream.next(event);
			});

		// NOTE: deliberately does NOT reset the stream on `connectionReestablished$` (unlike the
		// artifact-change notification service). This stream is configured with
		// `suppressDuplicates: false` -- it keeps no recent-key set to go stale across a
		// reconnect, so there is nothing to reset. Do not "fix" this with a no-op reset.
	}

	/**
	 * Emits a local branch change event for the current tab after a successful branch mutation, so
	 * the acting tab's UI updates immediately without waiting for the SSE round-trip (R1).
	 *
	 * Sibling tabs and other clients learn of the change from the server broadcast (relayed to
	 * siblings by the leader tab); the acting tab ignores that echo via {@code originId}, so no
	 * dedicated cross-tab local broadcast is needed.
	 *
	 * @param branchId the branch that was modified
	 * @param changeType what kind of branch change occurred
	 * @param associatedArtifactId the branch's associated artifact id, when known. Mirrors the
	 * server event's field so the acting tab's own emit matches consumers that key on it (e.g. the
	 * workflow editor awaiting a branch created for its artifact).
	 */
	emitLocalChange(
		branchId: string,
		changeType: branchChangeType,
		associatedArtifactId?: string
	): void {
		this.stream.emitLocal({ branchId, changeType, associatedArtifactId });
	}
}
