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
import { AsyncPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { XResultData } from '@osee/shared/types';
import { Observable, OperatorFunction, of } from 'rxjs';
import { filter, shareReplay, switchMap, take } from 'rxjs/operators';
import { AddFeatureConstraintDialogComponent } from '../../dialogs/add-feature-constraint-dialog/add-feature-constraint-dialog.component';
import { AddFeatureDialogComponent } from '../../dialogs/add-feature-dialog/add-feature-dialog.component';
import { EditFeatureDialogComponent } from '../../dialogs/edit-feature-dialog/edit-feature-dialog.component';
import { ViewFeatureConstraintsDialogComponent } from '../../dialogs/view-feature-constraints-dialog/view-feature-constraints-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { trackableFeature } from '../../types/features/base';
import { defaultBaseFeature } from '../../types/features/feature';
import {
	defaultFeatureConstraint,
	featureConstraintData,
} from '../../types/pl-config-feature-constraints';
import {
	PLAddFeatureData,
	PLEditFeatureData,
	modifyFeature,
	writeFeature,
} from '../../types/pl-config-features';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-feature-dropdown',
	templateUrl: './feature-dropdown.component.html',
	styles: [],
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
		AsyncPipe,
	],
})
export class FeatureDropdownComponent {
	private uiStateService = inject(PlConfigUIStateService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	dialog = inject(MatDialog);

	selectedBranch: Observable<string> = this.uiStateService.branchId.pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	features = this.currentBranchService.features;

	deleteFeature(feature: trackableFeature) {
		this.currentBranchService
			.deleteFeature(feature.id)
			.pipe(take(1))
			.subscribe((response: XResultData) => {
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
}
