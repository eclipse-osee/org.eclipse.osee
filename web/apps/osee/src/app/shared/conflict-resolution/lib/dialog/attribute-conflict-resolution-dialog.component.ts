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
	ChangeDetectionStrategy,
	Component,
	DestroyRef,
	computed,
	inject,
	signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
	attributeConflict,
	attributeConflictResolutionDialogData,
	attributeConflictResolutionDialogResult,
	conflictResolutionAction,
	liveConflictUpdate,
	resolvedConflict,
} from '../types/attribute-conflict.types';
import { AttributeValueEditorComponent } from '../components/attribute-value-editor.component';

type conflictState = {
	action: conflictResolutionAction;
	manualValue: string;
	/**
	 * True when a further remote change updated this conflict's server value while the
	 * dialog was open, so its selection was reset to the safe default. Cleared once the
	 * user interacts with the row. Surfaced in the UI so the change is not silent.
	 */
	serverChanged?: boolean;
};

@Component({
	selector: 'osee-attribute-conflict-resolution-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatIcon,
		MatRadioGroup,
		MatRadioButton,
		AttributeValueEditorComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './attribute-conflict-resolution-dialog.component.html',
})
export class AttributeConflictResolutionDialogComponent {
	private readonly dialogRef = inject(
		MatDialogRef<AttributeConflictResolutionDialogComponent>
	);
	private readonly destroyRef = inject(DestroyRef);
	protected readonly data =
		inject<attributeConflictResolutionDialogData>(MAT_DIALOG_DATA);

	/**
	 * The current set of interactive conflicts. Seeded from the dialog data and
	 * updated in place when the service pushes a re-categorized snapshot (a further
	 * remote change landed while the dialog was open). The template renders from this
	 * signal, not the static {@link data}, so the shown server values stay current.
	 */
	protected readonly conflicts = signal<attributeConflict[]>(
		this.data.conflicts
	);

	/** The current non-conflicting edits (read-only display), kept current with live updates. */
	protected readonly autoResolved = signal(this.data.autoResolved);

	/** The current staged additions (read-only display), kept current with live updates. */
	protected readonly stagedAdds = signal(this.data.stagedAdds);

	/** The current converged edits (same value both users), kept current with live updates. */
	protected readonly converged = signal(this.data.converged);

	/**
	 * The complete set of changes that will be saved without a decision, shown
	 * read-only: value edits the server did not touch, plus new instances staged
	 * while conflicted that the server did not also add. Each is tagged so the user
	 * can tell an edit from a newly added attribute in one place, rather than
	 * splitting them across two near-identical sections.
	 */
	protected readonly otherChanges = computed(() => [
		...this.autoResolved().map((c) => ({ ...c, added: false })),
		...this.stagedAdds().map((c) => ({ ...c, added: true })),
	]);

	/**
	 * Resolution state per conflict, aligned by index with {@link conflicts}. The safe
	 * default accepts the server truth: `take-theirs` for a value conflict,
	 * `accept-deletion` for a delete conflict.
	 */
	protected readonly states = signal<conflictState[]>(
		this.data.conflicts.map((c) => this.defaultState(c))
	);

	constructor() {
		// React to further remote changes while the dialog is open: reconcile the new
		// server snapshot into the displayed conflicts, preserving the user's in-progress
		// selections for rows whose server value did NOT change, and re-defaulting (with a
		// visible flag) any row whose server value did change so it can't be applied stale.
		this.data.liveUpdates$
			?.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((update) => this.applyLiveUpdate(update));
	}

	/** Stable identity for reconciling live updates: explicit key or base instance id. */
	private keyOfConflict(conflict: attributeConflict): string {
		return conflict.conflictKey ?? conflict.baseAttr.id;
	}

	/** Safe default resolution state for a conflict (accept server truth). */
	private defaultState(conflict: attributeConflict): conflictState {
		return {
			action: conflict.serverDeleted ? 'accept-deletion' : 'take-theirs',
			manualValue: conflict.localValue,
		};
	}

	/**
	 * Merges a freshly re-categorized snapshot into the open dialog. Conflicts are keyed
	 * by their base attribute id. For a conflict still present:
	 * - if its server value/existence is unchanged, the user's current selection is kept;
	 * - if it changed, the row is reset to the safe default and flagged `serverChanged`.
	 * New conflicts are appended (safe default); resolved-away conflicts are dropped.
	 */
	private applyLiveUpdate(update: liveConflictUpdate) {
		const prevConflicts = this.conflicts();
		const prevStates = this.states();
		const prevByKey = new Map(
			prevConflicts.map((c, i) => [
				this.keyOfConflict(c),
				{ c, state: prevStates[i] },
			])
		);

		const nextStates = update.conflicts.map((next) => {
			const prev = prevByKey.get(this.keyOfConflict(next));
			if (!prev) {
				// Newly conflicting attribute -> safe default.
				return this.defaultState(next);
			}
			const serverUnchanged =
				prev.c.serverDeleted === next.serverDeleted &&
				prev.c.serverAttr?.value === next.serverAttr?.value &&
				prev.c.serverAttr?.gammaId === next.serverAttr?.gammaId;
			if (serverUnchanged) {
				// Keep the user's in-progress decision as-is.
				return prev.state;
			}
			// Server value moved again -> reset this row and flag it so the change is visible.
			return { ...this.defaultState(next), serverChanged: true };
		});

		this.conflicts.set(update.conflicts);
		this.autoResolved.set(update.autoResolved);
		this.stagedAdds.set(update.stagedAdds);
		this.converged.set(update.converged);
		this.states.set(nextStates);
	}

	/** Whether all conflicts have a valid resolution. */
	protected readonly allResolved = computed(() => {
		return this.states().every((s) => {
			if (s.action === 'manual') {
				return s.manualValue.trim().length > 0;
			}
			return true;
		});
	});

	protected setAction(index: number, action: conflictResolutionAction) {
		this.states.update((arr) => {
			const next = [...arr];
			// Clear the server-changed flag: the user has now acted on the new value.
			next[index] = { ...next[index], action, serverChanged: false };
			return next;
		});
	}

	protected setManualValue(index: number, value: string) {
		this.states.update((arr) => {
			const next = [...arr];
			next[index] = {
				...next[index],
				manualValue: value,
				serverChanged: false,
			};
			return next;
		});
	}

	protected getState(index: number): conflictState {
		return this.states()[index];
	}

	protected onCancel() {
		this.dialogRef.close(undefined);
	}

	protected onSubmit() {
		// Build from the live `conflicts` signal (not the static data) so resolutions
		// carry the freshest serverAttr/gamma after any live re-categorize.
		const resolutions: resolvedConflict[] = this.conflicts().map(
			(conflict, i) => this.buildResolution(conflict, this.states()[i])
		);
		const result: attributeConflictResolutionDialogResult = { resolutions };
		this.dialogRef.close(result);
	}

	private buildResolution(
		conflict: attributeConflict,
		state: conflictState
	): resolvedConflict {
		switch (state.action) {
			case 'take-yours':
				return {
					conflict,
					action: 'take-yours',
					resolvedValues: [conflict.localValue],
				};
			case 'take-theirs':
				return {
					conflict,
					action: 'take-theirs',
					resolvedValues: [],
				};
			case 'take-both':
				return {
					conflict,
					action: 'take-both',
					resolvedValues: [
						conflict.serverAttr?.value ?? '',
						conflict.localValue,
					],
				};
			case 'manual':
				return {
					conflict,
					action: 'manual',
					resolvedValues: [state.manualValue],
				};
			case 're-add':
				return {
					conflict,
					action: 're-add',
					resolvedValues: [conflict.localValue],
				};
			case 'accept-deletion':
				return {
					conflict,
					action: 'accept-deletion',
					resolvedValues: [],
				};
		}
	}
}
