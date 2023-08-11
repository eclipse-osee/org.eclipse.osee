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
import { iif, Observable, of, OperatorFunction } from 'rxjs';
import { filter, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { ConfigGroup } from '../../types/pl-config-applicui-branch-mapping';
import {
	PLAddConfigData,
	PLEditConfigData,
} from '../../types/pl-edit-config-data';
import { EditConfigurationDialogComponent } from '../../dialogs/edit-config-dialog/edit-config-dialog.component';
import { editConfiguration } from '../../types/pl-config-configurations';
import { AddConfigurationDialogComponent } from '../../dialogs/add-configuration-dialog/add-configuration-dialog.component';
import { CopyConfigurationDialogComponent } from '../../dialogs/copy-configuration-dialog/copy-configuration-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
	selector: 'osee-plconfig-configuration-dropdown',
	templateUrl: './configuration-dropdown.component.html',
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
export class ConfigurationDropdownComponent {
	selectedBranch: Observable<string> = this.uiStateService.branchId.pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);
	configs = this.currentBranchService.branchApplicViews;
	editable = this.currentBranchService.branchApplicEditable;

	constructor(
		private uiStateService: PlConfigUIStateService,
		private currentBranchService: PlConfigCurrentBranchService,
		public dialog: MatDialog
	) {}

	deleteConfig(config: { id: string; name: string }) {
		this.currentBranchService
			.deleteConfiguration(config.id)
			.pipe(take(1))
			.subscribe();
	}

	openEditDialog(
		config: {
			id: string;
			name: string;
			description: string;
			hasFeatureApplicabilities: boolean;
		},
		productApplicabilities?: string[],
		groups?: ConfigGroup[]
	) {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					of(
						new PLEditConfigData(
							branchId,
							config,
							undefined,
							productApplicabilities,
							true,
							groups
						)
					).pipe(
						take(1),
						switchMap((dialogData) =>
							this.dialog
								.open(EditConfigurationDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										PLEditConfigData | undefined,
										PLEditConfigData
									>,
									switchMap((result) =>
										iif(
											() => result !== undefined,
											of<editConfiguration>({
												...result.currentConfig,
												copyFrom:
													(result.copyFrom.id &&
														result.copyFrom.id) ||
													'',
												configurationGroup:
													result.group.map(
														(a) => a.id
													),
												productApplicabilities:
													result.productApplicabilities ||
													[],
											}).pipe(
												take(1),
												switchMap((request) =>
													this.currentBranchService
														.editConfigurationDetails(
															request
														)
														.pipe(take(1))
												)
											),
											of(undefined)
										)
									)
								)
						)
					)
				)
			)
			.subscribe();
	}

	addConfiguration() {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					of<PLAddConfigData>({
						currentBranch: branchId?.toString(),
						copyFrom: {
							id: '0',
							name: '',
							description: '',
							hasFeatureApplicabilities: false,
							productApplicabilities: [],
						},
						title: '',
						description: '',
						group: [],
						productApplicabilities: [],
					}).pipe(
						take(1),
						switchMap((dialogData) =>
							this.dialog
								.open(AddConfigurationDialogComponent, {
									data: dialogData,
									minWidth: '60%',
								})
								.afterClosed()
								.pipe(
									take(1),
									filter(
										(val) => val !== undefined
									) as OperatorFunction<
										PLAddConfigData | undefined,
										PLAddConfigData
									>,
									switchMap((result) =>
										iif(
											() => result !== undefined,
											this.currentBranchService
												.addConfiguration({
													name: result.title,
													description:
														result.description,
													copyFrom:
														result.copyFrom.id,
													configurationGroup:
														result.group.map(
															(a) => a.id
														),
													productApplicabilities:
														result.productApplicabilities,
												})
												.pipe(take(1)),
											of(undefined)
										)
									)
								)
						)
					)
				)
			)
			.subscribe();
	}
	copyConfiguration() {
		this.selectedBranch
			.pipe(
				take(1),
				switchMap((branchId) =>
					this.dialog
						.open(CopyConfigurationDialogComponent, {
							data: {
								currentConfig: { id: '', name: '' },
								currentBranch: branchId,
							},
							minWidth: '60%',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter(
								(val) => val !== undefined
							) as OperatorFunction<
								PLEditConfigData | undefined,
								PLEditConfigData
							>,
							switchMap((dialog) =>
								iif(
									() => dialog !== undefined,
									this.currentBranchService
										.editConfigurationDetails({
											...dialog.currentConfig,
											copyFrom:
												(dialog.copyFrom &&
													dialog.copyFrom.id) ||
												'',
											configurationGroup:
												dialog.currentConfig.groups.map(
													(g) => g.id
												),
										})
										.pipe(take(1)),
									of(undefined)
								)
							)
						)
				)
			)
			.subscribe();
	}

	toggleMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.toggleMenu();
	}
	closeMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.closeMenu();
	}
	openMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.openMenu();
	}
}
