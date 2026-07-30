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
		<h1 mat-dialog-title>
			<div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
				<mat-icon>history</mat-icon>
				Transaction {{ data.txId }}
			</div>
		</h1>
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
									class="tw-rounded tw-bg-background-hover tw-px-1.5 tw-py-0.5 tw-text-[10px] tw-font-normal tw-normal-case">
									Not revertible
								</span>
							}
						</div>
						<div class="tw-grid tw-grid-cols-2 tw-gap-4">
							<div>
								<div
									class="tw-mb-1 tw-text-xs tw-font-bold tw-text-primary">
									Before:
								</div>
								<pre
									class="tw-max-h-48 tw-overflow-auto tw-whitespace-pre-wrap tw-rounded tw-bg-background-hover tw-p-2 tw-text-xs"
									>{{
										change.baselineVersion.value ||
											'(empty)'
									}}</pre
								>
							</div>
							<div>
								<div
									class="tw-mb-1 tw-text-xs tw-font-bold tw-text-primary">
									After:
								</div>
								<pre
									class="tw-max-h-48 tw-overflow-auto tw-whitespace-pre-wrap tw-rounded tw-bg-background-hover tw-p-2 tw-text-xs"
									>{{
										change.currentVersion.value || '(empty)'
									}}</pre
								>
							</div>
						</div>
					</div>
				}
			</div>
			@if (revertableCount() > 0) {
				<div
					class="tw-mt-4 tw-rounded tw-bg-background-hover tw-px-3 tw-py-2 tw-text-xs tw-opacity-80">
					<mat-icon class="tw-mr-1 tw-align-middle tw-text-sm"
						>info</mat-icon
					>
					@if (canRevert()) {
						Reverting creates a new transaction that sets this
						attribute back to its
						<strong>Before</strong> value. The current value is
						overwritten, but the full transaction history is
						preserved.
					} @else {
						Revert is only available for transactions with a single
						attribute change. This transaction has
						{{ revertableCount() }} attribute changes.
					}
				</div>
			}
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end"
			class="tw-gap-2">
			<button
				mat-stroked-button
				class="tw-text-foreground-text"
				mat-dialog-close>
				Cancel
			</button>
			<span
				[matTooltip]="
					revertableCount() === 0
						? 'No attribute changes to revert.'
						: revertableCount() > 1
							? 'Revert is only supported for single attribute changes.'
							: 'Creates a new transaction restoring this attribute to its previous value.'
				"
				matTooltipPosition="above">
				<button
					mat-stroked-button
					[disabled]="!canRevert()"
					(click)="revertChanges()">
					<mat-icon>undo</mat-icon>
					Revert
				</button>
			</span>
		</div>
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

	/** Revert is only allowed for single attribute changes. */
	protected canRevert = computed(() => this.revertableCount() === 1);

	/** Whether a change item is an attribute change (not relation/artifact/tuple). */
	isAttributeChange(change: artifactHistoryEntry): boolean {
		const ct = change.changeType;
		if (!ct) return false;
		if (typeof ct === 'string') {
			return ct === 'AttributeChange' || ct === '222';
		}
		if (typeof ct === 'object') {
			const obj = ct as Record<string, unknown>;
			return (
				obj['name'] === 'AttributeChange' || String(obj['id']) === '222'
			);
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
	 * Revert a single attribute change in this transaction by setting it
	 * back to its before value using a new modify transaction.
	 * Only allowed when there is exactly one revertable attribute change.
	 */
	revertChanges() {
		const attrChanges = this.data.changes.filter(
			(c) =>
				this.isAttributeChange(c) &&
				c.baselineVersion?.value !== undefined
		);

		if (attrChanges.length !== 1) return;

		const change = attrChanges[0];
		const message = `Revert this attribute change from transaction ${this.data.txId}?\n\nThis will create a new transaction setting the value back to:\n"${change.baselineVersion.value || '(empty)'}"`;

		if (!confirm(message)) return;

		const attr: attribute<string, ATTRIBUTETYPEID> = {
			id: this.extractId(change.itemId) as `${number}`,
			typeId: this.extractId(change.itemTypeId) as ATTRIBUTETYPEID,
			gammaId: '-1' as `${number}`,
			value: change.baselineVersion.value ?? '',
		};

		this.currentTxService
			.modifyArtifactAndMutate(
				`Reverting attribute change from tx ${this.data.txId}`,
				this.data.artifactId as `${number}`,
				applicabilitySentinel,
				{ set: [attr] }
			)
			.pipe(take(1))
			.subscribe(() => {
				this.dialogRef.close('restored');
			});
	}
}
