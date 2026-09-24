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
import { DestroyRef, Signal, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, filter } from 'rxjs';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { conflictKeyOptions } from '../logic/categorize-conflicts';
import { resolutionOperations } from '../logic/map-resolutions-to-operations';
import type {
	ConflictResolutionService,
	conflictCommitOutcome,
} from './conflict-resolution.service';

/**
 * The minimal shape of a change notification the controller needs. A superset of
 * this (e.g. {@link artifactInvalidation} from the notification service) is
 * accepted. Kept structural so the conflict-resolution module does not depend on
 * the SSE/notification layer.
 */
export type conflictChangeNotification = {
	/** What kinds of changes occurred (e.g. `attribute_modified`). */
	changeTypes: string[];
	/** True if the change originated from the current tab (local save). */
	isLocal?: boolean;
};

/**
 * Everything the page must supply for the controller to detect conflicts and run
 * resolution. This is the single, page-specific contract for adopting conflict
 * resolution on an SSE-enabled page.
 */
export type conflictControllerConfig = {
	/**
	 * Per-entity change notifications (e.g. `changeNotification.forArtifact(...)`).
	 * The controller flags a conflict when a remote (`!isLocal`) attribute change
	 * arrives while {@link hasUnsavedChanges} is true.
	 */
	changes: Observable<conflictChangeNotification>;
	/** Whether the user currently has unsaved local edits. */
	hasUnsavedChanges: () => boolean;

	/** Display name of the entity being edited (shown in the dialog). */
	entityName: () => string;
	/** Id of the entity (needed by widgets like the markdown editor for uploads). */
	entityId: () => string;
	/** The attribute set the user started editing from. */
	baseAttrs: () => readonly attribute<string, ATTRIBUTETYPEID>[];
	/** Fetches the latest server attribute state on demand. */
	fetchServerAttrs: () => Observable<
		readonly attribute<string, ATTRIBUTETYPEID>[]
	>;
	/** Unsaved local values, keyed the same way as {@link keyOptions}. */
	pendingValues: () => ReadonlyMap<string, string>;
	/** How pending edits are matched to base/server attrs (default: instance id). */
	keyOptions?: conflictKeyOptions;
	/**
	 * Persists the resolved operations; return the mutation observable resolving to a
	 * {@link conflictCommitOutcome} so the flow can detect optimistic-concurrency
	 * (stale-gamma) rejections. The page maps its own transaction result into it.
	 */
	commit: (ops: resolutionOperations) => Observable<conflictCommitOutcome>;
	/** Refreshes the page's view of the entity from the server. */
	refresh: () => void;
	/** Clears the page's local edit state (pending values, dirty flags). */
	clearLocalState: () => void;
	/** Reports a user-facing error message. */
	onError: (message: string) => void;
};

/**
 * Bundles conflict detection and resolution for a single editor. Created via
 * {@link ConflictResolutionService.controller}; owns the "remote change while
 * dirty" detection (so every page filters the SSE stream identically) and the
 * signals a page binds to (banner visibility, in-progress state), and delegates
 * the resolve/discard actions to the shared service.
 *
 * A page wires this once and binds `conflicted`/`resolving` in its template,
 * calling `resolve()`/`discard()` from the shared banner.
 */
export class ConflictController {
	private readonly _conflicted = signal(false);
	private readonly _resolving = signal(false);

	/** True when a remote change landed while the user had unsaved edits. */
	readonly conflicted: Signal<boolean> = this._conflicted.asReadonly();
	/** True while fetching latest server state / resolving. */
	readonly resolving: Signal<boolean> = this._resolving.asReadonly();

	constructor(
		private readonly service: ConflictResolutionService,
		private readonly config: conflictControllerConfig,
		destroyRef: DestroyRef
	) {
		config.changes
			.pipe(
				filter(
					(inv) =>
						!inv.isLocal &&
						inv.changeTypes.includes('attribute_modified') &&
						config.hasUnsavedChanges()
				),
				takeUntilDestroyed(destroyRef)
			)
			.subscribe(() => this._conflicted.set(true));
	}

	/**
	 * Runs the resolution flow (fetch latest, categorize, dialog, apply, commit,
	 * refresh). Clears the conflict flag when a resolution is committed.
	 */
	resolve(): void {
		this.service.resolve({
			entityName: this.config.entityName(),
			entityId: this.config.entityId(),
			baseAttrs: this.config.baseAttrs(),
			fetchServerAttrs: this.config.fetchServerAttrs,
			pendingValues: this.config.pendingValues(),
			keyOptions: this.config.keyOptions,
			// Forward the live change stream so an open dialog keeps re-deriving against
			// fresh server state (the controller already uses this stream for detection).
			changes: this.config.changes,
			commit: this.config.commit,
			refresh: this.config.refresh,
			clearLocalState: () => {
				this.config.clearLocalState();
				this._conflicted.set(false);
			},
			onError: this.config.onError,
			setResolving: (r) => this._resolving.set(r),
		});
	}

	/**
	 * Discards the user's unsaved edits and refreshes from the server, accepting
	 * the other user's changes.
	 */
	discard(): void {
		this.config.clearLocalState();
		this._conflicted.set(false);
		this.config.refresh();
	}

	/** Clears the conflict flag without discarding edits (e.g. after a manual save). */
	clearConflict(): void {
		this._conflicted.set(false);
	}
}
