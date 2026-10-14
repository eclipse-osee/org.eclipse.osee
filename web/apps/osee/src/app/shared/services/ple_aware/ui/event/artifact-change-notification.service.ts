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
import { Observable, filter } from 'rxjs';
import {
	OriginIdService,
	OriginAwareEventStream,
	SseEventService,
	artifactChangeType,
	associatedUsers,
} from '@osee/shared/services/network';

/**
 * Notification payload delivered to subscribers of a specific artifact.
 */
export type artifactInvalidation = {
	/** The artifact ID that was changed. */
	artifactId: string;
	/** The branch on which the change occurred. */
	branchId: string;
	/** The transaction ID of the commit. */
	transactionId: string;
	/** What kinds of changes occurred. */
	changeTypes: artifactChangeType[];
	/**
	 * User references from the changed user-valued attributes, grouped by attribute type.
	 * Lets a consumer decide relevance to a specific user (e.g. "assigned to me") without
	 * a server round-trip. Empty when the change touched no user-valued attributes.
	 */
	associatedUsers: associatedUsers[];
	/**
	 * Distinct attribute type ids changed in the transaction. Lets a consumer refetch only when a
	 * specific attribute type changed (e.g. a hierarchy label on a Name change) rather than on
	 * every `attribute_modified`. Empty when the change touched no attribute values.
	 */
	changedAttributeTypeIds: string[];
	/** True if this change originated from the current tab (local save). */
	isLocal?: boolean;
	/**
	 * Origin id of the tab that initiated the change (from the SSE event); used internally for
	 * self-echo dedup. Undefined for locally emitted invalidations.
	 */
	originId?: string;
};

/**
 * Bridges the WebSocket artifact-change stream into targeted per-artifact
 * invalidation signals.
 *
 * Instead of triggering a global page refresh, this service:
 * 1. Filters out self-originated events (same branch + same session)
 * 2. Emits per-artifact invalidation events that individual components subscribe to
 * 3. Components decide whether to auto-refresh or show a conflict warning
 *    based on their local dirty state
 *
 * For components that just need broad "something changed on my branch" awareness
 * (e.g., tree views, lists), `forBranch$` provides a branch-level stream.
 */
@Injectable({
	providedIn: 'root',
})
export class ArtifactChangeNotificationService {
	private readonly sseEventService = inject(SseEventService);
	private readonly originIdService = inject(OriginIdService);
	private readonly destroyRef = inject(DestroyRef);

	private initialized = false;

	/**
	 * Generic self-dedup/echo-suppression stream operating on per-artifact invalidations. Drops
	 * this tab's own echoes (by originId) and suppresses duplicate delivery (by
	 * branchId/artifactId/transactionId). See {@link OriginAwareEventStream}.
	 */
	private readonly stream = new OriginAwareEventStream<artifactInvalidation>({
		myOriginId: () => this.originIdService.originId,
		getOriginId: (inv) => inv.originId,
		getEventKey: (inv) =>
			`${inv.branchId}/${inv.artifactId}/${inv.transactionId}`,
	});

	/**
	 * Observable of per-artifact invalidation events (one per artifact per commit).
	 * Subscribe to this if you need to react to changes on specific artifacts.
	 */
	readonly artifactInvalidations$: Observable<artifactInvalidation> =
		this.stream.output;

	/**
	 * Emits after the SSE connection is re-established (on this tab or the leader). Consumers of
	 * open views should merge this into their refetch trigger and do a full GET, because change
	 * events may have been missed during the disconnect window (GET-on-notify has no notify for a
	 * missed event). Fires on both leader and follower tabs.
	 */
	readonly resync$: Observable<void> =
		this.sseEventService.connectionReestablished$;

	/**
	 * Returns an observable that emits when a specific artifact on a specific
	 * branch is changed by another user. Use this in editor components to
	 * know when to re-fetch or show a stale-data warning.
	 */
	forArtifact(
		branchId: string,
		artifactId: string
	): Observable<artifactInvalidation> {
		return this.artifactInvalidations$.pipe(
			filter(
				(inv) =>
					inv.branchId === branchId && inv.artifactId === artifactId
			)
		);
	}

	/**
	 * Returns an observable that emits when any artifact on the given branch
	 * is changed by another user. Useful for tree views or lists that show
	 * multiple artifacts.
	 */
	forBranch(branchId: string): Observable<artifactInvalidation> {
		return this.artifactInvalidations$.pipe(
			filter((inv) => inv.branchId === branchId)
		);
	}

	/**
	 * Returns an observable that emits when a change on the given branch modified the given
	 * attribute type (matched against the event's `changedAttributeTypeIds`). Use this for views
	 * that must react to a specific attribute changing — e.g. a hierarchy tree refreshing a node
	 * label when its Name attribute changes — without reacting to every `attribute_modified`.
	 */
	forChangedAttributeType(
		branchId: string,
		attributeTypeId: string
	): Observable<artifactInvalidation> {
		return this.artifactInvalidations$.pipe(
			filter(
				(inv) =>
					inv.branchId === branchId &&
					inv.changedAttributeTypeIds.includes(attributeTypeId)
			)
		);
	}

	/**
	 * Returns an observable that emits when structural changes (create, delete,
	 * relation add/remove) occur on the given branch. Does NOT emit for
	 * attribute-only modifications. Use this for hierarchy trees that only
	 * need to refresh when the structure changes.
	 */
	structuralChangesForBranch(
		branchId: string
	): Observable<artifactInvalidation> {
		return this.artifactInvalidations$.pipe(
			filter(
				(inv) =>
					inv.branchId === branchId &&
					inv.changeTypes.some(
						(t) =>
							t === 'artifact_created' ||
							t === 'artifact_deleted' ||
							t === 'relation_added' ||
							t === 'relation_deleted'
					)
			)
		);
	}

	/**
	 * Returns an observable that emits when a change on the given branch references the given
	 * user (art-id encoded) through any user-valued attribute — e.g. a workflow now assigned to
	 * that user. Generic and reusable for any "does this concern user X?" feature (world, reviews,
	 * watched items). Does NOT cover the "was mine, now removed" case, since the removed user is
	 * no longer referenced; consumers detect that via their own current-list membership.
	 */
	forAssociatedUser(
		branchId: string,
		userId: string
	): Observable<artifactInvalidation> {
		return this.artifactInvalidations$.pipe(
			filter(
				(inv) =>
					inv.branchId === branchId &&
					inv.associatedUsers.some(
						(group) =>
							group.encoding === 'artId' &&
							group.userIds.includes(userId)
					)
			)
		);
	}

	/**
	 * Initializes the WebSocket connection and starts processing events.
	 * Safe to call multiple times — will no-op after first initialization.
	 *
	 * Call this from the app root (AppComponent).
	 */
	initialize(): void {
		if (this.initialized) {
			return;
		}
		this.initialized = true;

		this.sseEventService.connect();

		// Keep the shared dedup stream active regardless of downstream subscribers.
		this.stream.output
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe();

		// Fan each incoming SSE artifact-change event out to one invalidation per affected
		// artifact, then feed them through the dedup/self-echo stream.
		this.sseEventService.artifactChanges$
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((event) => {
				for (const artifactId of event.artifactIds) {
					this.stream.next({
						artifactId,
						branchId: event.branchId,
						transactionId: event.transactionId,
						changeTypes: event.changeTypes ?? [
							'attribute_modified',
						],
						associatedUsers: event.associatedUsers ?? [],
						changedAttributeTypeIds:
							event.changedAttributeTypeIds ?? [],
						originId: event.originId ?? undefined,
					});
				}
			});

		// On reconnection the client does a full GET to hydrate fresh state, and events may have
		// been missed; reset duplicate suppression so post-reconnect events are processed.
		this.sseEventService.connectionReestablished$
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe(() => {
				this.stream.reset();
			});
	}

	/**
	 * Emits a local change event for artifacts modified by the current tab.
	 * Called by `performMutation()` after a successful commit.
	 * This feeds the same `artifactInvalidations$` stream that SSE events use,
	 * so consumers react identically to local and remote changes.
	 *
	 * Only the acting tab refreshes immediately. Sibling tabs and other clients learn of the
	 * change from the server broadcast (relayed to siblings by the leader tab). The acting tab
	 * ignores that echo via {@code originId}, so no dedicated cross-tab local broadcast is needed.
	 *
	 * @param branchId the branch where the change was committed
	 * @param artifactIds the affected artifact IDs
	 * @param transactionId the committed transaction ID
	 * @param changeTypes what kinds of changes occurred
	 * @param associatedUsers user references from the changed attributes (optional); lets the
	 * acting tab's own views decide relevance immediately without waiting for the server echo
	 * @param changedAttributeTypeIds distinct attribute type ids changed (optional); lets the acting
	 * tab's own views do targeted refreshes without waiting for the server echo
	 */
	emitLocalChange(
		branchId: string,
		artifactIds: string[],
		transactionId: string,
		changeTypes: artifactChangeType[],
		associatedUsers: associatedUsers[] = [],
		changedAttributeTypeIds: string[] = []
	): void {
		for (const artifactId of artifactIds) {
			this.stream.emitLocal({
				artifactId,
				branchId,
				transactionId,
				changeTypes,
				associatedUsers,
				changedAttributeTypeIds,
				isLocal: true,
			});
		}
	}
}
