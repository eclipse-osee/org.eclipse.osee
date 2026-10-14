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
import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatSnackBar } from '@angular/material/snack-bar';
import { map, switchMap } from 'rxjs';
import {
	branchChangeEvent,
	branchChangeType,
} from '@osee/shared/services/network';
import { BranchChangeEventService } from '../event/branch-change-event.service';
import { CurrentBranchInfoService } from '../../httpui/current-branch-info.service';
import { UiService } from '../ui.service';
import { BranchRoutedUIService } from './branch-routed-ui.service';

/**
 * Declarative reaction to a lifecycle event on the app's *currently selected* branch.
 *
 * Two optional effects; a reaction supplies whichever apply:
 * - `navigateTo` — where to move the app's branch selection. Return a branch id to select it (e.g.
 *   the rebaseline successor), an empty string `''` to CLEAR the selection (no branch, not COMMON),
 *   or `undefined` to skip navigation entirely and leave the current selection.
 * - `notify` — a user-facing message to snackbar, given the last-known branch name. Return null to
 *   stay silent.
 */
type selectedBranchReaction = {
	navigateTo?: (event: branchChangeEvent) => string | undefined;
	notify?: (branchName: string, event: branchChangeEvent) => string | null;
};

/**
 * Owns the app's reactions when the **currently selected** branch changes in a way that requires
 * the whole app to respond — the branch was rebaselined away, or deleted/purged out from under the
 * user. Centralizes what were ad-hoc `AppComponent` subscriptions so new cases are a single table
 * entry rather than another one-off subscription.
 *
 * Scope note: this reacts to the *selected* branch only (keyed on {@link UiService.id}). Per-tab
 * reactions (e.g. closing artifact-explorer tabs on a deleted branch) are keyed on each tab's own
 * branch and live with those tabs, not here.
 *
 * Root-provided and always-on; call {@link initialize} once from the app root.
 */
@Injectable({ providedIn: 'root' })
export class SelectedBranchLifecycleService {
	private readonly uiService = inject(UiService);
	private readonly branchChangeEvent = inject(BranchChangeEventService);
	private readonly branchedRouter = inject(BranchRoutedUIService);
	private readonly currentBranchInfo = inject(CurrentBranchInfoService);
	private readonly snackBar = inject(MatSnackBar);
	private readonly destroyRef = inject(DestroyRef);

	private initialized = false;

	/**
	 * Last-known name of the selected branch. Captured continuously so a deleted/purged branch —
	 * which can no longer be re-GET (404) — can still be named in the notice.
	 */
	private readonly currentBranchName = toSignal(
		this.currentBranchInfo.currentBranch.pipe(map((b) => b?.name ?? '')),
		{ initialValue: '' }
	);

	/**
	 * The reaction table: what the app does when the selected branch undergoes each change type.
	 * Add a case here — not a new subscription — to handle a new selected-branch lifecycle event.
	 */
	private readonly reactions: Partial<
		Record<branchChangeType, selectedBranchReaction>
	> = {
		// Update-from-parent swap: follow the selected branch to its successor so the hierarchy and
		// all selected-branch views stay on the live branch.
		rebaselined: {
			navigateTo: (event) => event.newBranchId,
		},
		// Branch gone: a re-GET would 404, so CLEAR the selection (empty branch, not COMMON)
		// and explain why. Falling back to COMMON silently swapped the user onto an unrelated
		// branch; clearing leaves them on no branch so they consciously pick the next one.
		deleted: {
			navigateTo: () => '',
			notify: (name) =>
				`${name ? `"${name}"` : 'The branch you were viewing'} was deleted. Clearing the branch selection.`,
		},
		purged: {
			navigateTo: () => '',
			notify: (name) =>
				`${name ? `"${name}"` : 'The branch you were viewing'} was purged. Clearing the branch selection.`,
		},
	};

	/**
	 * Wires the selected-branch lifecycle reactions. Idempotent — safe to call once from the app
	 * root. `switchMap` off {@link UiService.id} re-subscribes whenever the selected branch changes,
	 * so a reaction that navigates (flipping the id) automatically drops the old branch's watch.
	 */
	initialize(): void {
		if (this.initialized) {
			return;
		}
		this.initialized = true;

		this.uiService.id
			.pipe(
				switchMap((selectedBranchId) =>
					this.branchChangeEvent.forBranch(selectedBranchId)
				),
				takeUntilDestroyed(this.destroyRef)
			)
			// react() looks up the reaction table and no-ops on an unwatched change type, so no
			// pre-filter is needed here.
			.subscribe((event) => this.react(event));
	}

	private react(event: branchChangeEvent): void {
		const reaction = this.reactions[event.changeType];
		if (!reaction) {
			return;
		}

		if (reaction.notify) {
			const message = reaction.notify(this.currentBranchName(), event);
			if (message) {
				this.snackBar.open(message, 'Dismiss', { duration: 8000 });
			}
		}

		if (reaction.navigateTo) {
			const target = reaction.navigateTo(event);
			// undefined => skip (leave selection as-is); '' => clear the selection;
			// any id => select that branch. Note '' is intentionally NOT skipped.
			if (target !== undefined) {
				this.branchedRouter.branchId = target;
			}
		}
	}
}
