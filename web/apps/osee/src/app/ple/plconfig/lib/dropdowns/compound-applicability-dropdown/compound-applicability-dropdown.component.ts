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
import { Component, ViewContainerRef, computed, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { Observable, OperatorFunction, of, tap } from 'rxjs';
import { filter, shareReplay, switchMap, take } from 'rxjs/operators';
import { AddCompoundApplicabilityDialogComponent } from '../../dialogs/add-compound-applicability-dialog/add-compound-applicability-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { trackableFeature } from '../../types/features/base';
import {
	PLAddCompoundApplicabilityData,
	defaultCompoundApplicability,
} from '../../types/pl-config-compound-applicabilities';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';

@Component({
	selector: 'osee-compound-applicability-dropdown',
	templateUrl: './compound-applicability-dropdown.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		MatMenuItem,
		MatMenuTrigger,
		MatMenu,
		MatMenuContent,
		MatIcon,
	],
})
export class CompoundApplicabilityDropdownComponent {
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
