/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of, OperatorFunction } from 'rxjs';
import { filter, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { trackableFeature } from '../../types/features/base';
import { defaultBaseFeature } from '../../types/features/feature';
import {
	modifyFeature,
	PLEditFeatureData,
	writeFeature,
	PLAddFeatureData,
} from '../../types/pl-config-features';
import { response } from '@osee/shared/types';
import { AddFeatureDialogComponent } from '../../dialogs/add-feature-dialog/add-feature-dialog.component';
import { EditFeatureDialogComponent } from '../../dialogs/edit-feature-dialog/edit-feature-dialog.component';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { AddFeatureConstraintDialogComponent } from './../../dialogs/add-feature-constraint-dialog/add-feature-constraint-dialog.component';
import {
	defaultFeatureConstraint,
	featureConstraintData,
} from './../../types/pl-config-feature-constraints';
import { ViewFeatureConstraintsDialogComponent } from '../../dialogs/view-feature-constraints-dialog/view-feature-constraints-dialog.component';

@Component({
	selector: 'osee-plconfig-feature-dropdown',
	templateUrl: './feature-dropdown.component.html',
	styleUrls: ['./feature-dropdown.component.sass'],
	standalone: true,
	imports: [
		MatIconModule,
		MatMenuModule,
		MatFormFieldModule,
		NgFor,
		NgIf,
		AsyncPipe,
	],
})
export class FeatureDropdownComponent {
	selectedBranch: Observable<string> = this.uiStateService.branchId.pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);
	editable = this.currentBranchService.branchApplicEditable;
	features = this.currentBranchService.branchApplicFeatures;
	featureConstraints =
		this.currentBranchService.applicsWithFeatureConstraints;

	constructor(
		private uiStateService: PlConfigUIStateService,
		private currentBranchService: PlConfigCurrentBranchService,
		public dialog: MatDialog
	) {}

	deleteFeature(feature: trackableFeature) {
		this.currentBranchService
			.deleteFeature(feature.id)
			.pipe(take(1))
			.subscribe((response: response) => {
				if (response.success) {
					this.uiStateService.updateReqConfig = true;
				}
			});
	}

	openEditDialog(feature: trackableFeature) {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					of({
						currentBranch: branchId,
						editable: true,
						feature: new modifyFeature(feature, '', ''),
					}).pipe(
						switchMap((dialogData) =>
							this.dialog
								.open(EditFeatureDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										PLEditFeatureData | undefined,
										PLEditFeatureData
									>,
									switchMap((result) =>
										this.currentBranchService
											.modifyFeature(result.feature)
											.pipe(take(1))
									)
								)
						)
					)
				)
			)
			.subscribe();
	}

	addFeature() {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					of({
						currentBranch: branchId,
						feature: new writeFeature(new defaultBaseFeature()),
					}).pipe(
						take(1),
						switchMap((dialogData) =>
							this.dialog
								.open(AddFeatureDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										PLAddFeatureData | undefined,
										PLAddFeatureData
									>,
									switchMap((result) =>
										this.currentBranchService
											.addFeature(result.feature)
											.pipe(take(1))
									)
								)
						)
					)
				)
			)
			.subscribe();
	}

	addFeatureConstraint() {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap(() =>
					of({
						featureConstraint: defaultFeatureConstraint,
					}).pipe(
						take(1),
						switchMap((dialogData) =>
							this.dialog
								.open(AddFeatureConstraintDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										featureConstraintData | undefined,
										featureConstraintData
									>,
									switchMap((result) =>
										this.currentBranchService
											.addFeatureConstraint(result)
											.pipe(take(1))
									)
								)
						)
					)
				)
			)
			.subscribe();
	}

	editFeatureConstraints() {
		this.dialog.open(ViewFeatureConstraintsDialogComponent);
	}

	toggleMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.toggleMenu();
	}

	isCompoundApplic(name: string) {
		if (name.includes(' | ') || name.includes(' & ')) {
			return false;
		}
		return true;
	}
}
