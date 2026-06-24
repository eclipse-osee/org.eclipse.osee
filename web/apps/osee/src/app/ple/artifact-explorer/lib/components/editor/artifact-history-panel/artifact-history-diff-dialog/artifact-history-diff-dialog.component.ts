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
	computed,
	inject,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { CurrentTransactionService } from '@osee/transactions/services';
import { take } from 'rxjs';
import { artifactHistoryEntry } from '../../../../types/artifact-history';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { applicabilitySentinel } from '@osee/applicability/types';

export type ArtifactHistoryDiffDialogData = {
	txId: string;
	comment: string;
	date: string;
	changes: artifactHistoryEntry[];
	branchId: string;
	artifactId: string;
};

@Component({
	selector: 'osee-artifact-history-diff-dialog',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
		MatIcon,
		MatTooltip,
	],
	template: `
		<h2 mat-dialog-title class="tw-flex tw-items-center tw-gap-2">
			<mat-icon>history</mat-icon>
			Transaction {{ data.txId }}
		</h2>
		<mat-dialog-content>
			@if (data.comment || data.date) {
				<div
					class="tw-mb-3 tw-rounded tw-bg-background-hover tw-px-3 tw-py-2 tw-text-xs tw-opacity-80">
					@if (data.date) {
						<span class="tw-font-bold">Date:</span>
						{{ data.date }}
					}
					@if (data.comment) {
						<span class="tw-ml-3 tw-font-bold">Comment:</span>
						{{ data.comment }}
					}
				</div>
			}
			<div class="tw-flex tw-flex-col tw-gap-4">
				@for (change of data.changes; track $index) {
					<div
						class="tw-rounded tw-border tw-p-3"
						[class.tw-opacity-50]="!isAttributeChange(change)">
						<div
							class="tw-mb-2 tw-flex tw-items-center tw-gap-2 tw-text-xs tw-font-bold tw-uppercase tw-opacity-60">
							<span>
								{{ displayStr(change.changeType) }} —
								{{ displayStr(change.itemTypeId) }}
							</span>
							@if (!isAttributeChange(change)) {
								<span
									class="tw-rounded tw-bg-background-hover tw-px-1.5 tw-py-0.5 tw-text-[10px] tw-normal-case tw-font-normal">
									Not revertible
								</span>
							}
						</div>
						<div class="tw-grid tw-grid-cols-2 tw-gap-4">
							<div>
								<div
									class="tw-mb-1 tw-text-xs tw-font-bold tw-text-warning">
									Before:
								</div>
								<pre
									class="tw-max-h-48 tw-overflow-auto tw-whitespace-pre-wrap tw-rounded tw-bg-background-hover tw-p-2 tw-text-xs">{{
									change.baselineVersion.value || '(empty)'
								}}</pre>
							</div>
							<div>
								<div
									class="tw-mb-1 tw-text-xs tw-font-bold tw-text-primary">
									After:
								</div>
								<pre
									class="tw-max-h-48 tw-overflow-auto tw-whitespace-pre-wrap tw-rounded tw-bg-background-hover tw-p-2 tw-text-xs">{{
									change.currentVersion.value || '(empty)'
								}}</pre>
							</div>
						</div>
					</div>
				}
			</div>
		</mat-dialog-content>
		<mat-dialog-actions align="end">
			<button mat-button mat-dialog-close>Close</button>
			<button
				mat-button
				class="tw-bg-primary tw-text-white disabled:tw-bg-background-hover disabled:tw-text-inherit disabled:tw-opacity-50"
				[disabled]="revertableCount() === 0"
				[matTooltip]="
					revertableCount() === 0
						? 'No attribute changes to revert (relation changes are not supported)'
						: 'Reverts attribute values only. Relation changes cannot be reverted.'
				"
				(click)="revertChanges()">
				<mat-icon>undo</mat-icon>
				Revert attribute changes ({{ revertableCount() }})
			</button>
		</mat-dialog-actions>
	`,
})
export class ArtifactHistoryDiffDialogComponent {
	private dialogRef = inject(
		MatDialogRef<ArtifactHistoryDiffDialogComponent>
	);
	private currentTxService = inject(CurrentTransactionService);
	private uiService = inject(UiService);
	data = inject<ArtifactHistoryDiffDialogData>(MAT_DIALOG_DATA);

	/** Number of attribute changes that can be reverted. */
	protected revertableCount = computed(
		() =>
			this.data.changes.filter(
				(c) =>
					this.isAttributeChange(c) &&
					c.baselineVersion?.value !== undefined
			).length
	);

	/** Whether a change item is an attribute change (not relation/artifact/tuple). */
	isAttributeChange(change: artifactHistoryEntry): boolean {
		const ct = change.changeType;
		if (!ct) return false;
		if (typeof ct === 'string') {
			return ct === 'AttributeChange' || ct === '222';
		}
		if (typeof ct === 'object') {
			const obj = ct as Record<string, unknown>;
			return obj['name'] === 'AttributeChange' || String(obj['id']) === '222';
		}
		return false;
	}

	/** Safely display a value that might be an object with .name or .id, or a string. */
	displayStr(val: unknown): string {
		if (!val) return '';
		if (typeof val === 'string') return val;
		if (typeof val === 'object') {
			const obj = val as Record<string, unknown>;
			return (
				(obj['name'] as string) ??
				(obj['id'] as string) ??
				JSON.stringify(val)
			);
		}
		return String(val);
	}

	/** Extract a string ID from a value that may be a string, number, or {id: ...} object. */
	private extractId(val: unknown): string {
		if (!val) return '-1';
		if (typeof val === 'string') return val;
		if (typeof val === 'number') return String(val);
		if (typeof val === 'object') {
			const obj = val as Record<string, unknown>;
			const id = obj['id'];
			if (id !== undefined) return String(id);
		}
		return String(val);
	}

	/**
	 * Revert attribute changes in this transaction group by setting each
	 * changed attribute back to its baseline (before) value using a normal
	 * modify transaction. Relation and artifact-level changes are skipped.
	 */
	revertChanges() {
		const attrChanges = this.data.changes.filter(
			(c) =>
				this.isAttributeChange(c) &&
				c.baselineVersion?.value !== undefined
		);
		const skipped =
			this.data.changes.length - attrChanges.length;

		let message =
			`Revert ${attrChanges.length} attribute change(s) from transaction ${this.data.txId}?`;
		if (skipped > 0) {
			message += `\n\n${skipped} non-attribute change(s) (relations, etc.) will be skipped — only attribute values can be reverted.`;
		}

		if (!confirm(message)) return;

		// Build attribute set list from the baseline (before) values.
		const attrs: attribute<string, ATTRIBUTETYPEID>[] =
			attrChanges.map((c) => ({
				id: this.extractId(c.itemId) as `${number}`,
				typeId: this.extractId(c.itemTypeId) as ATTRIBUTETYPEID,
				gammaId: '-1' as `${number}`,
				value: c.baselineVersion.value ?? '',
			}));

		if (attrs.length === 0) return;

		this.currentTxService
			.modifyArtifactAndMutate(
				`Reverting ${attrChanges.length} attribute change(s) from tx ${this.data.txId}`,
				this.data.artifactId as `${number}`,
				applicabilitySentinel,
				{ set: attrs }
			)
			.pipe(take(1))
			.subscribe(() => {
				this.dialogRef.close('restored');
			});
	}
}
