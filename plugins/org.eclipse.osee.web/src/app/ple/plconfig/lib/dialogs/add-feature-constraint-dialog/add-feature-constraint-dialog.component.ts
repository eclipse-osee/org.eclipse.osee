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
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Component, Inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Observable, of, map } from 'rxjs';
import { NamedId } from '@osee/shared/types';
import { featureConstraintData } from '../../types/pl-config-feature-constraints';
import { ApplicabilityListUIService } from '@osee/shared/services';

@Component({
	selector: 'osee-add-feature-constraint-dialog',
	standalone: true,
	imports: [
		CommonModule,
		MatFormFieldModule,
		MatSlideToggleModule,
		FormsModule,
		MatDialogModule,
		MatOptionModule,
		MatSelectModule,
		MatButtonModule,
		MatDividerModule,
		MatInputModule,
		MatAutocompleteModule,
	],
	templateUrl: './add-feature-constraint-dialog.component.html',
	styles: [],
})
export class AddFeatureConstraintDialogComponent implements OnDestroy {
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

	constructor(
		public dialogRef: MatDialogRef<AddFeatureConstraintDialogComponent>,
		private currentBranchService: PlConfigCurrentBranchService,
		@Inject(MAT_DIALOG_DATA) public data: featureConstraintData,
		private applicListUIService: ApplicabilityListUIService
	) {}

	// Search for conflicts in existing configurations
	findConstraintConflicts() {
		if (
			this.data.featureConstraint.applicability1.id != '' &&
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
		this.data.featureConstraint.applicability1.id = '';
	}

	// Prevents case where user selects option, alters input text, then tries to submit form
	clearApp2ID() {
		this.data.featureConstraint.applicability2.id = '';
	}

	filter(value: string, isChild: Boolean): void {
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
		this.data.featureConstraint.applicability2.id = '';
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

	findMatch(isChild: Boolean): void {
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
						item?.id || '';
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
							item?.id || '';
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
							item?.id || '';
					});
			}
		}
	}

	ngOnDestroy() {
		this.data.featureConstraint.applicability1.id = '';
		this.data.featureConstraint.applicability1.name = '';
		this.data.featureConstraint.applicability2.id = '';
		this.data.featureConstraint.applicability2.name = '';
	}
}
