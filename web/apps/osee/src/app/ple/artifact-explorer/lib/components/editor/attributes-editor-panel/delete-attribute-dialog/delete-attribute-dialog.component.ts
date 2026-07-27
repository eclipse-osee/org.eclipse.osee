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
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

export type deleteAttributeDialogData = {
	/** Attributes currently on the artifact that may be deletable. */
	existingAttributes: attribute<string, ATTRIBUTETYPEID>[];
};

export type deleteAttributeDialogResult = {
	/** The specific attribute instances to delete. */
	selectedAttributes: attribute<string, ATTRIBUTETYPEID>[];
};

@Component({
	selector: 'osee-delete-attribute-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatIcon,
		MatFormField,
		MatLabel,
		MatInput,
		MatCheckbox,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<h1 mat-dialog-title>
			<div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
				<mat-icon>delete</mat-icon>
				Delete Attribute
			</div>
		</h1>
		<mat-dialog-content class="tw-pt-2">
			<mat-form-field
				appearance="outline"
				class="tw-w-full"
				subscriptSizing="dynamic">
				<mat-label>Filter attributes</mat-label>
				<input
					matInput
					[(ngModel)]="filterText"
					name="filter"
					placeholder="Type to filter..." />
			</mat-form-field>
			@if (deletableAttributes().length === 0) {
				<p class="tw-p-4 tw-text-sm tw-opacity-50">
					No attributes can be deleted. All remaining attributes are
					at their minimum required count.
				</p>
			} @else {
				<div
					class="tw-mt-2 tw-flex tw-max-h-[300px] tw-flex-col tw-gap-1 tw-overflow-y-auto">
					@for (attr of filteredAttributes(); track trackAttr(attr)) {
						<mat-checkbox
							[checked]="isSelected(attr)"
							(change)="toggleSelection(attr)"
							[disabled]="!canStillDelete(attr)">
							<span class="tw-text-sm">
								{{ attr.name ?? attr.typeId }}
								@if (getValuePreview(attr); as preview) {
									<span class="tw-ml-1 tw-opacity-60"
										>= "{{ preview }}"</span
									>
								}
							</span>
						</mat-checkbox>
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
				(click)="onCancel()">
				Cancel
			</button>
			<button
				mat-stroked-button
				class="tw-text-warning"
				[disabled]="selectedAttributes().length === 0"
				(click)="onSubmit()">
				Delete
			</button>
		</div>
	`,
})
export class DeleteAttributeDialogComponent {
	private readonly dialogRef = inject(
		MatDialogRef<DeleteAttributeDialogComponent>
	);
	private readonly data = inject<deleteAttributeDialogData>(MAT_DIALOG_DATA);

	protected readonly filterText = signal('');
	protected readonly selectedAttributes = signal<
		attribute<string, ATTRIBUTETYPEID>[]
	>([]);

	/**
	 * Attributes that can be deleted, respecting multiplicity minimums.
	 * Excludes Name (always required) and attributes at minimum count
	 * for EXACTLY_ONE / AT_LEAST_ONE multiplicities.
	 */
	protected readonly deletableAttributes = computed(() => {
		const existing = this.data.existingAttributes;

		return existing.filter((attr) => {
			// Never allow deleting Name
			if (attr.name?.toLowerCase() === 'name') {
				return false;
			}

			const multiplicityId = attr.multiplicity?.id;
			const instanceCount = existing.filter(
				(e) => e.typeId === attr.typeId
			).length;

			// EXACTLY_ONE (id=2) or AT_LEAST_ONE (id=4): minimum is 1
			if (multiplicityId === '2' || multiplicityId === '4') {
				return instanceCount > 1;
			}

			// ANY (id=1) or ZERO_OR_ONE (id=3): can always delete
			return true;
		});
	});

	protected readonly filteredAttributes = computed(() => {
		const filter = this.filterText().toLowerCase().trim();
		if (!filter) {
			return this.deletableAttributes();
		}
		return this.deletableAttributes().filter(
			(a) =>
				a.name?.toLowerCase().includes(filter) ||
				a.typeId.toLowerCase().includes(filter) ||
				String(a.value).toLowerCase().includes(filter)
		);
	});

	/**
	 * Determines if selecting one more instance of this type would still
	 * maintain the minimum multiplicity constraint.
	 */
	protected canStillDelete(
		attr: attribute<string, ATTRIBUTETYPEID>
	): boolean {
		const existing = this.data.existingAttributes;
		const selected = this.selectedAttributes();

		const multiplicityId = attr.multiplicity?.id;
		// For ANY or ZERO_OR_ONE, always allow deletion
		if (multiplicityId === '1' || multiplicityId === '3') {
			return true;
		}

		// For EXACTLY_ONE or AT_LEAST_ONE, ensure at least 1 remains
		const totalOfType = existing.filter(
			(e) => e.typeId === attr.typeId
		).length;
		const selectedOfType = selected.filter(
			(s) => s.typeId === attr.typeId
		).length;

		// If this attr is already selected, it can be unselected
		if (this.isSelected(attr)) {
			return true;
		}

		// Can select if removing it still leaves at least minimum (1)
		return totalOfType - selectedOfType > 1;
	}

	protected isSelected(attr: attribute<string, ATTRIBUTETYPEID>): boolean {
		return this.selectedAttributes().some(
			(s) => s.typeId === attr.typeId && s.gammaId === attr.gammaId
		);
	}

	protected toggleSelection(attr: attribute<string, ATTRIBUTETYPEID>) {
		const current = this.selectedAttributes();
		if (this.isSelected(attr)) {
			this.selectedAttributes.set(
				current.filter(
					(s) =>
						!(
							s.typeId === attr.typeId &&
							s.gammaId === attr.gammaId
						)
				)
			);
		} else {
			this.selectedAttributes.set([...current, attr]);
		}
	}

	protected getValuePreview(
		attr: attribute<string, ATTRIBUTETYPEID>
	): string {
		const val = String(attr.value ?? '');
		if (val.length > 40) {
			return val.substring(0, 40) + '...';
		}
		return val;
	}

	protected trackAttr(attr: attribute<string, ATTRIBUTETYPEID>): string {
		return `${attr.typeId}-${attr.gammaId}`;
	}

	protected onCancel() {
		this.dialogRef.close();
	}

	protected onSubmit() {
		const result: deleteAttributeDialogResult = {
			selectedAttributes: this.selectedAttributes(),
		};
		this.dialogRef.close(result);
	}
}
