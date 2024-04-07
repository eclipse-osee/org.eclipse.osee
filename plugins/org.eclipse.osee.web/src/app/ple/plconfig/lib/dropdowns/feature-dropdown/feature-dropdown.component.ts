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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { response } from '@osee/shared/types';
import { Observable, of, OperatorFunction } from 'rxjs';
import { filter, shareReplay, switchMap, take } from 'rxjs/operators';
import { AddFeatureDialogComponent } from '../../dialogs/add-feature-dialog/add-feature-dialog.component';
import { EditFeatureDialogComponent } from '../../dialogs/edit-feature-dialog/edit-feature-dialog.component';
import { ViewFeatureConstraintsDialogComponent } from '../../dialogs/view-feature-constraints-dialog/view-feature-constraints-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { trackableFeature } from '../../types/features/base';
import { defaultBaseFeature } from '../../types/features/feature';
import {
	modifyFeature,
	PLAddFeatureData,
	PLEditFeatureData,
	writeFeature,
} from '../../types/pl-config-features';
import { AddFeatureConstraintDialogComponent } from '../../dialogs/add-feature-constraint-dialog/add-feature-constraint-dialog.component';
import {
	defaultFeatureConstraint,
	featureConstraintData,
} from '../../types/pl-config-feature-constraints';

@Component({
	selector: 'osee-plconfig-feature-dropdown',
	templateUrl: './feature-dropdown.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
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
