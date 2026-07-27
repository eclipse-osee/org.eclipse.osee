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

export type addAttributeDialogData = {
	/** All attribute types valid for this artifact type. */
	allAttributeTypes: attribute<string, ATTRIBUTETYPEID>[];
	/** Attributes currently on the artifact. */
	existingAttributes: attribute<string, ATTRIBUTETYPEID>[];
};

export type addAttributeDialogResult = {
	/** The attribute types selected to add, repeated per requested count. */
	selectedTypes: attribute<string, ATTRIBUTETYPEID>[];
};

@Component({
	selector: 'osee-add-attribute-dialog',
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
				<mat-icon>add_circle</mat-icon>
				Add Attribute
			</div>
		</h1>
		<mat-dialog-content class="tw-pt-2">
			<mat-form-field
				appearance="outline"
				class="tw-w-full"
				subscriptSizing="dynamic">
				<mat-label>Filter attribute types</mat-label>
				<input
					matInput
					[(ngModel)]="filterText"
					name="filter"
					placeholder="Type to filter..." />
			</mat-form-field>
			@if (filteredTypes().length === 0) {
				<p class="tw-p-4 tw-text-sm tw-opacity-50">
					No attribute types available to add.
				</p>
			} @else {
				<div
					class="tw-mt-2 tw-flex tw-max-h-[300px] tw-flex-col tw-gap-1 tw-overflow-y-auto tw-pr-2">
					@for (type of filteredTypes(); track type.typeId) {
						<div
							class="tw-flex tw-items-center tw-gap-2">
							<mat-checkbox
								[checked]="isSelected(type)"
								(change)="toggleSelection(type)">
								<span class="tw-text-sm">{{
									type.name ?? type.typeId
								}}</span>
								@if (getMultiplicityLabel(type); as label) {
									<span
										class="tw-ml-1 tw-text-xs tw-opacity-50"
										>({{ label }})</span
									>
								}
							</mat-checkbox>
							@if (isUnlimited(type) && isSelected(type)) {
								<input
									type="number"
									min="1"
									max="50"
									[ngModel]="getCount(type)"
									(ngModelChange)="setCount(type, $event)"
									[name]="'count-' + type.typeId"
									(keydown)="limitDigits($event)"
									class="tw-w-12 tw-rounded tw-border tw-border-solid tw-border-background-hover tw-bg-transparent tw-px-1 tw-py-0.5 tw-text-center tw-text-sm" />
							}
						</div>
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
				[disabled]="selectedCount() === 0"
				(click)="onSubmit()">
				Add
			</button>
		</div>
	`,
})
export class AddAttributeDialogComponent {
	private readonly dialogRef = inject(
		MatDialogRef<AddAttributeDialogComponent>
	);
	private readonly data = inject<addAttributeDialogData>(MAT_DIALOG_DATA);

	protected readonly filterText = signal('');

	/** Map of typeId → { type, count } for selected items. */
	protected readonly selections = signal<
		Map<string, { type: attribute<string, ATTRIBUTETYPEID>; count: number }>
	>(new Map());

	/** Total number of attributes that will be added. */
	protected readonly selectedCount = computed(() => {
		let total = 0;
		for (const entry of this.selections().values()) {
			total += entry.count;
		}
		return total;
	});

	/**
	 * Attribute types that can have a new instance added.
	 */
	protected readonly addableTypes = computed(() => {
		const all = this.data.allAttributeTypes;
		const existing = this.data.existingAttributes;

		return all.filter((type) => {
			if (type.name?.toLowerCase() === 'name') {
				return false;
			}

			const instanceCount = existing.filter(
				(e) => e.typeId === type.typeId
			).length;

			const multiplicityId = type.multiplicity?.id;

			// EXACTLY_ONE (id=2) or ZERO_OR_ONE (id=3): max is 1
			if (multiplicityId === '2' || multiplicityId === '3') {
				return instanceCount < 1;
			}
			// ANY (id=1) or AT_LEAST_ONE (id=4): unlimited
			return true;
		});
	});

	protected readonly filteredTypes = computed(() => {
		const filter = this.filterText().toLowerCase().trim();
		if (!filter) {
			return this.addableTypes();
		}
		return this.addableTypes().filter(
			(t) =>
				t.name?.toLowerCase().includes(filter) ||
				t.typeId.toLowerCase().includes(filter)
		);
	});

	protected isSelected(type: attribute<string, ATTRIBUTETYPEID>): boolean {
		return this.selections().has(type.typeId);
	}

	protected isUnlimited(type: attribute<string, ATTRIBUTETYPEID>): boolean {
		const id = type.multiplicity?.id;
		return id === '1' || id === '4';
	}

	protected toggleSelection(type: attribute<string, ATTRIBUTETYPEID>) {
		const current = new Map(this.selections());
		if (current.has(type.typeId)) {
			current.delete(type.typeId);
		} else {
			current.set(type.typeId, { type, count: 1 });
		}
		this.selections.set(current);
	}

	protected getCount(type: attribute<string, ATTRIBUTETYPEID>): number {
		return this.selections().get(type.typeId)?.count ?? 1;
	}

	protected setCount(
		type: attribute<string, ATTRIBUTETYPEID>,
		count: number
	) {
		const clamped = Math.max(1, Math.min(50, Math.floor(count || 1)));
		const current = new Map(this.selections());
		const entry = current.get(type.typeId);
		if (entry) {
			current.set(type.typeId, { ...entry, count: clamped });
			this.selections.set(current);
		}
	}

	protected limitDigits(event: KeyboardEvent) {
		const input = event.target as HTMLInputElement;
		const allowedKeys = [
			'Backspace',
			'Delete',
			'ArrowLeft',
			'ArrowRight',
			'ArrowUp',
			'ArrowDown',
			'Tab',
		];
		if (allowedKeys.includes(event.key)) {
			return;
		}
		// Block non-numeric keys
		if (!/^\d$/.test(event.key)) {
			event.preventDefault();
			return;
		}
		// Block if already at 2 digits and not selecting text
		if (
			input.value.length >= 2 &&
			input.selectionStart === input.selectionEnd
		) {
			event.preventDefault();
		}
	}

	protected getMultiplicityLabel(
		type: attribute<string, ATTRIBUTETYPEID>
	): string {
		switch (type.multiplicity?.id) {
			case '1':
				return 'Optional, Unlimited';
			case '2':
				return 'Required, Exactly One';
			case '3':
				return 'Optional, Max 1';
			case '4':
				return 'Required, Unlimited';
			default:
				return '';
		}
	}

	protected onCancel() {
		this.dialogRef.close();
	}

	protected onSubmit() {
		const selectedTypes: attribute<string, ATTRIBUTETYPEID>[] = [];
		for (const entry of this.selections().values()) {
			for (let i = 0; i < entry.count; i++) {
				selectedTypes.push(entry.type);
			}
		}
		const result: addAttributeDialogResult = { selectedTypes };
		this.dialogRef.close(result);
	}
}
