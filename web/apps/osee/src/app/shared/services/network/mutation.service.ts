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
import { Injectable, inject } from '@angular/core';
import { defer, Observable, tap, throwError } from 'rxjs';
import { artifactChangeType, associatedUsers } from './sse-event.service';
import { ArtifactChangeNotificationService } from '../ple_aware/ui/event/artifact-change-notification.service';
import { BranchChangeEventService } from '../ple_aware/ui/event/branch-change-event.service';
import { branchChangeType } from './sse-event.service';
import { SseEventService } from './sse-event.service';

/**
 * Describes what artifact changes a mutation produced. Returned by the caller's
 * {@code describeChange} function so the chokepoint knows what to emit.
 */
export type artifactChangeDescriptor = {
	type: 'artifact';
	branchId: string;
	artifactIds: string[];
	transactionId?: string;
	changeTypes: artifactChangeType[];
	associatedUsers?: associatedUsers[];
	/** Distinct attribute type ids changed; lets the acting tab do targeted refreshes locally. */
	changedAttributeTypeIds?: string[];
};

/**
 * Describes what branch-metadata change a mutation produced.
 */
export type branchChangeDescriptor = {
	type: 'branch';
	branchId: string;
	changeType: branchChangeType;
	/** The branch's associated artifact id, when known; mirrors the server event so the acting
	 * tab's local emit matches consumers that key on it (e.g. the workflow editor). */
	associatedArtifactId?: string;
};

/**
 * Union of change descriptors the chokepoint handles.
 */
export type changeDescriptor =
	| artifactChangeDescriptor
	| branchChangeDescriptor;

/**
 * Centralized mutation utility — the single emit site for local change notifications.
 *
 * Every mutating HTTP call in the app should route through {@link mutateAndNotify} (or the
 * pipeable {@link withLocalNotify} operator). On success, it extracts a change descriptor from the
 * response and emits the scoped local invalidation so the acting tab refreshes immediately. On
 * failure, nothing is emitted.
 *
 * This replaces per-caller `emitLocalChange` calls scattered across services. It is the client
 * chokepoint that ensures:
 * - One maintenance site for the emit logic.
 * - Works for transactional (ORCS) and non-transactional (ATS, branch) mutations uniformly.
 * - Hard to forget: all mutations should go through this service. A raw `http.post` in a mutation
 *   context that bypasses this is a code-review flag.
 *
 * If a response does not describe a change (caller returns `null` from `describeChange`), no
 * notification is emitted — useful for queries disguised as POSTs or dry-run validations.
 */
@Injectable({
	providedIn: 'root',
})
export class MutationService {
	private readonly artifactNotification = inject(
		ArtifactChangeNotificationService
	);
	private readonly branchNotification = inject(BranchChangeEventService);
	private readonly sseEventService = inject(SseEventService);

	/**
	 * Wraps an HTTP mutation call and emits a scoped local change notification on success.
	 *
	 * @param httpCall the raw HTTP observable (e.g. `this.http.post(...)`)
	 * @param describeChange extracts a change descriptor from the successful response, or returns
	 * null if the response does not represent a change (e.g. validation-only, dry run)
	 * @returns the same observable, with the local notification side-effect attached
	 *
	 * @example
	 * // Transaction (ORCS):
	 * this.mutation.mutateAndNotify(
	 *   this.http.post<transactionResult>(url, body),
	 *   (res) => ({
	 *     type: 'artifact',
	 *     branchId: body.branch,
	 *     artifactIds: res.results.ids,
	 *     transactionId: res.tx.id,
	 *     changeTypes: ['attribute_modified'],
	 *   })
	 * );
	 *
	 * @example
	 * // ATS transition. NOTE: workItemIds are ArtifactToken objects ({ id, name }), not id
	 * // strings — map to .id, not String(w), or artifactIds become "[object Object]".
	 * this.mutation.mutateAndNotify(
	 *   this.http.post<transitionResponse>(url, body),
	 *   (res) => res.transaction?.id ? {
	 *     type: 'artifact',
	 *     branchId: res.transaction.branchId ?? COMMON_BRANCH_ID,
	 *     artifactIds: res.workItemIds.map((w) => w.id),
	 *     transactionId: res.transaction.id,
	 *     changeTypes: ['attribute_modified'],
	 *   } : null
	 * );
	 *
	 * @example
	 * // Branch commit:
	 * this.mutation.mutateAndNotify(
	 *   this.http.post<commitResponse>(url, body),
	 *   () => ({ type: 'branch', branchId, changeType: 'committed' })
	 * );
	 */
	mutateAndNotify<T, D extends changeDescriptor>(
		httpCall: Observable<T>,
		describeChange: (response: T) => D | null
	): Observable<T> {
		// Chokepoint guard: block mutations while the server is not confirmed ready (e.g. still
		// warming up after a restart). Writing against a not-ready server produces transient
		// failures and phantom conflicts. `defer` evaluates readiness at subscribe time, not when
		// the observable is constructed. UI should also disable save affordances via
		// `SseEventService.serverReady`; this is the defense-in-depth backstop.
		return defer(() =>
			this.sseEventService.serverReady()
				? httpCall.pipe(this.withLocalNotify(describeChange))
				: throwError(
						() =>
							new Error(
								'Server is reconnecting and not ready to accept changes yet. Please retry in a moment.'
							)
					)
		);
	}

	/**
	 * Pipeable operator form of {@link mutateAndNotify} — attach to any observable to emit the
	 * local notification on each emission. Useful when the observable is built upstream and you
	 * just want to append the side-effect.
	 */
	withLocalNotify<T, D extends changeDescriptor>(
		describeChange: (response: T) => D | null
	) {
		return (source: Observable<T>) =>
			source.pipe(
				tap((response) => {
					const descriptor = describeChange(response);
					if (!descriptor) {
						return;
					}
					if (descriptor.type === 'artifact') {
						if (
							descriptor.artifactIds &&
							descriptor.artifactIds.length > 0
						) {
							this.artifactNotification.emitLocalChange(
								descriptor.branchId,
								descriptor.artifactIds,
								descriptor.transactionId ?? '',
								descriptor.changeTypes,
								descriptor.associatedUsers ?? [],
								descriptor.changedAttributeTypeIds ?? []
							);
						}
					} else if (descriptor.type === 'branch') {
						this.branchNotification.emitLocalChange(
							descriptor.branchId,
							descriptor.changeType,
							descriptor.associatedArtifactId
						);
					}
				})
			);
	}
}
