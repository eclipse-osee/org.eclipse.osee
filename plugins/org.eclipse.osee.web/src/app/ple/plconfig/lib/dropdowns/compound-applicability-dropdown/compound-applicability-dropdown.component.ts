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
import { Component, ViewContainerRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { Observable, of, OperatorFunction } from 'rxjs';
import { filter, shareReplay, switchMap, take } from 'rxjs/operators';
import { AddCompoundApplicabilityDialogComponent } from '../../dialogs/add-compound-applicability-dialog/add-compound-applicability-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { trackableFeature } from '../../types/features/base';
import {
	defaultCompoundApplicability,
	PLAddCompoundApplicabilityData,
} from '../../types/pl-config-compound-applicabilities';
import { tap } from 'rxjs';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

@Component({
	selector: 'osee-compound-applicability-dropdown',
	templateUrl: './compound-applicability-dropdown.component.html',
	styles: [],
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
export class CompoundApplicabilityDropdownComponent {
	selectedBranch: Observable<string> = this.uiStateService.branchId.pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);

	editable = this.currentBranchService.branchApplicEditable;
	features = this.currentBranchService.branchApplicFeatures;

	equalsSymbol = '=';

	constructor(
		private currentBranchService: PlConfigCurrentBranchService,
		private uiStateService: PlConfigUIStateService,
		public dialog: MatDialog,
		private viewContainerRef: ViewContainerRef
	) {}

	toggleMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.toggleMenu();
	}

	isCompoundApplic(name: string) {
		return name.includes(' | ') || name.includes(' & ');
	}

	addCompApplic() {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					of({
						currentBranch: branchId,
						compoundApplicability:
							new defaultCompoundApplicability(),
					}).pipe(
						take(1),
						switchMap((dialogData) =>
							this.dialog
								.open(AddCompoundApplicabilityDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										| PLAddCompoundApplicabilityData
										| undefined,
										PLAddCompoundApplicabilityData
									>,
									switchMap((result) =>
										this.currentBranchService
											.addCompoundApplicability(
												result.compoundApplicability
													.name
											)
											.pipe(take(1))
									)
								)
						)
					)
				)
			)
			.subscribe();
	}

	deleteCompApplic(feature: trackableFeature) {
		this.currentBranchService
			.deleteCompoundApplicability(feature.id)
			.pipe(
				filter((resp) => resp != undefined),
				tap((response) => {
					if (response.success) {
						this.uiStateService.updateReqConfig = true;
					}
				})
			)
			.subscribe();
	}
}
