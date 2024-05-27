/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { NamedId } from '@osee/shared/types';
import { Observable, map, of } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { featureConstraintData } from '../../types/pl-config-feature-constraints';

@Component({
	selector: 'osee-add-feature-constraint-dialog',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatSlideToggle,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
	templateUrl: './add-feature-constraint-dialog.component.html',
	styles: [],
})
export class AddFeatureConstraintDialogComponent {
	dialogRef =
		inject<MatDialogRef<AddFeatureConstraintDialogComponent>>(MatDialogRef);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	data = inject<featureConstraintData>(MAT_DIALOG_DATA);
	private applicListUIService = inject(ApplicabilityListUIService);

	constraintIsCompApplic = false;
	preview = '';
	hidePreview = true;

	applic$ = this.applicListUIService.applic.pipe(
		map((items) =>
			items.filter(
				(item) =>
					!item.name.includes('Base') &&
					!item.name.includes('Config =') &&
					!item.name.includes('ConfigurationGroup =') &&
					!item.name.includes('|') &&
					!item.name.includes('&')
			)
		)
	);
	compApplic$ = this.applicListUIService.applic.pipe(
		map((items) =>
			items.filter(
				(item) =>
					!item.name.includes('Base') &&
					!item.name.includes('Config =') &&
					!item.name.includes('ConfigurationGroup =') &&
					(item.name.includes('|') || item.name.includes('&'))
			)
		)
	);

	conflict$: Observable<string[]> = of([]);

	// Applics to be filtered based on user text input
	filteredChildApplic$: Observable<NamedId[]> = this.applic$;
	filteredParentApplic$: Observable<NamedId[]> = this.applic$;
	filteredParentCompApplic$: Observable<NamedId[]> = this.compApplic$;

	// Search for conflicts in existing configurations
	findConstraintConflicts() {
		if (
			this.data.featureConstraint.applicability1.id != '-1' &&
			this.data.featureConstraint.applicability2.id
		) {
			this.conflict$ =
				this.currentBranchService.getFeatureConstraintConflicts(
					this.data.featureConstraint.applicability1.id,
					this.data.featureConstraint.applicability2.id
				);
		}
	}

	// Prevents case where user selects option, alters input text, then tries to submit form
	clearApp1ID() {
		this.data.featureConstraint.applicability1.id = '-1';
	}

	// Prevents case where user selects option, alters input text, then tries to submit form
	clearApp2ID() {
		this.data.featureConstraint.applicability2.id = '-1';
	}

	filter(value: string, isChild: boolean): void {
		const filterValue = value.toLowerCase();

		if (isChild) {
			this.filteredChildApplic$ = this.applic$.pipe(
				map((items) =>
					items.filter((item) =>
						item.name.toLowerCase().includes(filterValue)
					)
				)
			);
		} else {
			if (this.constraintIsCompApplic) {
				this.filteredParentCompApplic$ = this.compApplic$.pipe(
					map((items) =>
						items.filter((item) =>
							item.name.toLowerCase().includes(filterValue)
						)
					)
				);
			} else {
				this.filteredParentApplic$ = this.applic$.pipe(
					map((items) =>
						items.filter((item) =>
							item.name.toLowerCase().includes(filterValue)
						)
					)
				);
			}
		}
	}

	toggleIsCompoundApplic(): void {
		this.constraintIsCompApplic = !this.constraintIsCompApplic;
		this.data.featureConstraint.applicability2.id = '-1';
		this.data.featureConstraint.applicability2.name = '';
		this.hidePreview = true;
	}

	toggleHidePreview(): void {
		this.hidePreview = !this.hidePreview;
	}

	closePreview(): void {
		this.hidePreview = true;
	}

	onCancelClick(): void {
		this.dialogRef.close();
	}

	findMatch(isChild: boolean): void {
		if (isChild) {
			this.applic$
				.pipe(
					map((items) =>
						items.find(
							(item) =>
								item.name ===
								this.data.featureConstraint.applicability1.name
						)
					)
				)
				.subscribe((item) => {
					this.data.featureConstraint.applicability1.id =
						item?.id || '-1';
				});
		} else {
			if (!this.constraintIsCompApplic) {
				this.applic$
					.pipe(
						map((items) =>
							items.find(
								(item) =>
									item.name ===
									this.data.featureConstraint.applicability2
										.name
							)
						)
					)
					.subscribe((item) => {
						this.data.featureConstraint.applicability2.id =
							item?.id || '-1';
					});
			} else {
				this.compApplic$
					.pipe(
						map((items) =>
							items.find(
								(item) =>
									item.name ===
									this.data.featureConstraint.applicability2
										.name
							)
						)
					)
					.subscribe((item) => {
						this.data.featureConstraint.applicability2.id =
							item?.id || '-1';
					});
			}
		}
	}
}
