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
import { DestroyRef, Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
	BehaviorSubject,
	Observable,
	Subject,
	filter,
	switchMap,
	take,
	takeUntil,
} from 'rxjs';
import {
	ConflictController,
	conflictChangeNotification,
	conflictControllerConfig,
} from './conflict-controller';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { AttributeConflictResolutionDialogComponent } from '../dialog/attribute-conflict-resolution-dialog.component';
import {
	attributeConflictResolutionDialogData,
	attributeConflictResolutionDialogResult,
	liveConflictUpdate,
} from '../types/attribute-conflict.types';
import {
	categorizeConflicts,
	conflictCategorization,
	conflictKeyOptions,
} from '../logic/categorize-conflicts';
import {
	mapResolutionsToOperations,
	resolutionOperations,
} from '../logic/map-resolutions-to-operations';

/**
 * Page-agnostic result of committing resolved operations. Lets the shared service
 * detect optimistic-concurrency rejections without depending on the transactions
 * layer. The page translates its own transaction result into this shape.
 */
export type conflictCommitOutcome = {
	/**
	 * Attribute gammas the server rejected because they were stale (another commit
	 * moved the value between the dialog opening and this apply). Empty when the
	 * resolution was fully applied. A non-empty list means the write did NOT take
	 * for those attributes and the user must re-resolve against the newer value.
	 */
	staleGammas: string[];
};

/**
 * Everything a page must supply to run the conflict-resolution flow. The shared
 * service owns the generic orchestration (categorize -> dialog -> apply -> commit
 * -> refresh); the page supplies only what is inherently page-specific: how to
 * read state, how to persist, and how to refresh.
 */
export type conflictResolutionConfig = {
	/** Display name of the entity being edited (shown in the dialog). */
	entityName: string;
	/** Id of the entity (needed by widgets like the markdown editor for uploads). */
	entityId: string;
	/** The attribute set the user started editing from. */
	baseAttrs: readonly attribute<string, ATTRIBUTETYPEID>[];
	/** Fetches the latest server attribute state on demand. */
	fetchServerAttrs: () => Observable<
		readonly attribute<string, ATTRIBUTETYPEID>[]
	>;
	/** Unsaved local values, keyed the same way as {@link keyOf}. */
	pendingValues: ReadonlyMap<string, string>;
	/**
	 * How pending edits are matched to base/server attrs. Default keys by instance
	 * `id`; pass `{ keyOf: (a) => a.typeId }` for editors that track edits by type.
	 */
	keyOptions?: conflictKeyOptions;
	/**
	 * Persists the resolved operations. Return the mutation observable resolving to a
	 * {@link conflictCommitOutcome}; the service inspects it for optimistic-concurrency
	 * rejections (stale gammas) before treating the resolution as applied. Not called
	 * when there is nothing to persist.
	 *
	 * The page maps its own transaction result into the outcome (e.g. the artifact/ATS
	 * pages map {@code transactionResult.failedGammas} to {@link conflictCommitOutcome.staleGammas}),
	 * keeping this shared module decoupled from the transactions layer.
	 */
	commit: (ops: resolutionOperations) => Observable<conflictCommitOutcome>;
	/**
	 * Refreshes the page's view of the entity from the server. Called after a
	 * commit (so the acting view reflects the saved values) and when there is
	 * nothing to persist. This is the shared guarantee that the local view never
	 * shows stale state after resolution.
	 */
	refresh: () => void;
	/**
	 * Clears the page's local edit/conflict state (dirty flags, pending values,
	 * conflict banner). Called once the outcome is committed, before refresh.
	 */
	clearLocalState: () => void;
	/** Reports a user-facing error message (fetch or commit failure). */
	onError: (message: string) => void;
	/** Optional: toggled true while fetching server state, false when done. */
	setResolving?: (resolving: boolean) => void;
	/**
	 * Optional stream of remote change notifications for this entity (the same stream
	 * used for detection, e.g. `changeNotification.forArtifact(...)`). When provided,
	 * the service keeps an OPEN dialog live: on a remote `attribute_modified` it
	 * re-fetches, re-categorizes, and pushes the fresh conflicts into the dialog so the
	 * user never resolves against a stale server value. Absent = static dialog.
	 */
	changes?: Observable<conflictChangeNotification>;
};

/**
 * Generic orchestrator for attribute conflict resolution, reusable by any page.
 *
 * Flow: fetch latest server state -> {@link categorizeConflicts} -> if no true
 * conflicts, commit the safe edits; otherwise open the shared dialog and, on
 * submit, map resolutions to operations, merge with the safe edits, and commit.
 * A refresh is guaranteed after every committed outcome so the acting view is
 * never left showing stale local state (the reason a page's own SSE echo, which
 * it suppresses while dirty, cannot be relied on to refresh it).
 */
@Injectable({
	providedIn: 'root',
})
export class ConflictResolutionService {
	private readonly dialog = inject(MatDialog);

	/**
	 * Preferred entry point for pages: creates a {@link ConflictController} that
	 * owns conflict detection (remote-change-while-dirty) and exposes the signals
	 * a page binds to, delegating resolve/discard here. Wire this once per editor
	 * and bind `conflicted`/`resolving` in the template.
	 */
	controller(
		config: conflictControllerConfig,
		destroyRef: DestroyRef
	): ConflictController {
		return new ConflictController(this, config, destroyRef);
	}

	/**
	 * Runs the full resolution flow. Fetches server state, categorizes, and either
	 * auto-commits safe edits or prompts the user, then commits + refreshes.
	 *
	 * Most pages should use {@link controller} instead, which also owns detection.
	 * This is exposed for consumers that manage their own detection (e.g. the
	 * artifact editor, whose parent component already owns a multi-purpose SSE
	 * subscription).
	 */
	resolve(config: conflictResolutionConfig): void {
		config.setResolving?.(true);
		config
			.fetchServerAttrs()
			.pipe(take(1))
			.subscribe({
				next: (serverAttrs) => {
					config.setResolving?.(false);
					this.categorizeAndResolve(config, serverAttrs);
				},
				error: (err) => {
					config.setResolving?.(false);
					config.onError(
						`Failed to load latest version for conflict resolution: ${
							err?.message ?? 'Unknown error'
						}`
					);
				},
			});
	}

	private categorizeAndResolve(
		config: conflictResolutionConfig,
		serverAttrs: readonly attribute<string, ATTRIBUTETYPEID>[]
	): void {
		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			config.baseAttrs,
			serverAttrs,
			config.pendingValues,
			config.keyOptions
		);

		// No true conflicts: commit the safe edits (server never touched them).
		if (conflicts.length === 0) {
			this.commitAndRefresh(config, { set: autoSaveAttrs, add: [] });
			return;
		}

		// Single source of truth for the current re-categorization. Seeded with the
		// open-time snapshot and replaced on every live update, so BOTH the dialog display
		// (conflicts + auto-resolved) and the committed operations (conflict resolutions +
		// auto-save edits) derive from the same value and cannot drift. This is what keeps
		// auto-save gammas fresh: committing an older snapshot would send stale gammas and
		// be rejected by the optimistic-concurrency guard, spuriously re-opening the dialog.
		const latest = new BehaviorSubject<conflictCategorization>({
			conflicts,
			autoSaveAttrs,
		});

		// Live-update channel: while the dialog is open, re-derive conflicts from fresh
		// server state whenever another remote change lands, and push them into the dialog.
		const liveUpdates = new Subject<liveConflictUpdate>();
		const dialogClosed = new Subject<void>();

		const data: attributeConflictResolutionDialogData = {
			conflicts,
			// Show non-conflicting edits read-only so the dialog accounts for every
			// changed field (matching what the editor flagged), not just conflicts.
			autoResolved: this.toAutoResolved(autoSaveAttrs),
			entityName: config.entityName,
			entityId: config.entityId,
			liveUpdates$: liveUpdates.asObservable(),
		};

		// Keep the open dialog current: on each remote attribute change, re-fetch and
		// re-categorize, emitting the fresh conflicts/auto-resolved to the dialog. Torn
		// down when the dialog closes. Fetch errors are swallowed here (the dialog keeps
		// showing the last good snapshot); a stale apply is still caught by the gamma guard.
		config.changes
			?.pipe(
				filter(
					(n) =>
						!n.isLocal &&
						n.changeTypes.includes('attribute_modified')
				),
				switchMap(() => config.fetchServerAttrs().pipe(take(1))),
				takeUntil(dialogClosed)
			)
			.subscribe({
				next: (freshServerAttrs) => {
					const recategorized = categorizeConflicts(
						config.baseAttrs,
						freshServerAttrs,
						config.pendingValues,
						config.keyOptions
					);
					// Update the single source of truth, then derive the dialog payload
					// from it so display and commit stay in lockstep.
					latest.next(recategorized);
					liveUpdates.next({
						conflicts: recategorized.conflicts,
						autoResolved: this.toAutoResolved(
							recategorized.autoSaveAttrs
						),
					});
				},
				error: () => {
					/* keep last good snapshot; gamma guard backstops a stale apply */
				},
			});

		this.dialog
			.open(AttributeConflictResolutionDialogComponent, {
				data,
				// Responsive: grows with the viewport within sensible bounds;
				// height is content-driven and capped so it never exceeds the viewport.
				width: '60vw',
				minWidth: '32rem',
				maxWidth: '60rem',
				maxHeight: '90vh',
				disableClose: true,
				restoreFocus: false,
			})
			.afterClosed()
			.pipe(take(1))
			.subscribe(
				(
					result: attributeConflictResolutionDialogResult | undefined
				) => {
					// Stop the live-refresh subscription regardless of outcome.
					dialogClosed.next();
					dialogClosed.complete();
					liveUpdates.complete();

					if (!result) {
						// Cancelled: leave conflict state so the user can revisit.
						latest.complete();
						return;
					}
					const { set, add } = mapResolutionsToOperations(
						result.resolutions
					);
					// Derive the auto-save set from the single source of truth so its gammas
					// match the latest server state; the conflict resolutions already carry
					// fresh gammas via the dialog's live `conflicts` signal.
					const { autoSaveAttrs: freshAutoSave } = latest.getValue();
					latest.complete();
					this.commitAndRefresh(config, {
						set: [...freshAutoSave, ...set],
						add,
					});
				}
			);
	}

	/** Maps auto-save attrs to the read-only display shape shown in the dialog. */
	private toAutoResolved(
		autoSaveAttrs: readonly attribute<string, ATTRIBUTETYPEID>[]
	) {
		return autoSaveAttrs.map((a) => ({
			name: a.name ?? a.typeId,
			localValue: `${a.value}`,
		}));
	}

	private commitAndRefresh(
		config: conflictResolutionConfig,
		ops: resolutionOperations
	): void {
		// Nothing to persist (e.g. all take-theirs): the server is already
		// authoritative, so clear local state and refresh.
		if (ops.set.length === 0 && ops.add.length === 0) {
			config.clearLocalState();
			config.refresh();
			return;
		}

		config
			.commit(ops)
			.pipe(take(1))
			.subscribe({
				next: (outcome) => {
					// Optimistic-concurrency backstop: the server rejected one or more
					// attributes because another commit moved them between the dialog
					// opening and this apply. The resolution did NOT fully take. Do not
					// report success or clear local state -- re-run the flow so the user
					// re-decides against the now-current server value (which re-fetches,
					// re-categorizes, and re-opens the dialog). Preserves the user's
					// pending edits so nothing is silently lost.
					if (outcome.staleGammas.length > 0) {
						config.onError(
							'Someone else changed this while you were resolving. ' +
								'Re-checking against the latest version...'
						);
						this.resolve(config);
						return;
					}

					// Fully applied: safe to drop local edit/conflict state and refresh.
					config.clearLocalState();
					config.refresh();
				},
				error: (err) => {
					config.onError(
						`Conflict resolution failed: ${
							err?.message ?? 'Unknown error'
						}`
					);
					// Hard failure (not a concurrency rejection): clear local state and
					// refresh so the view reflects authoritative server state.
					config.clearLocalState();
					config.refresh();
				},
			});
	}
}
