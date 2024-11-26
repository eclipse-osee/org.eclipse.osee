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
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';
import { Observable, OperatorFunction, iif, of } from 'rxjs';
import { filter, shareReplay, switchMap, take } from 'rxjs/operators';
import { DialogService } from '../../services/dialog.service';
import { AddConfigurationDialogComponent } from '../../dialogs/add-configuration-dialog/add-configuration-dialog.component';
import { CopyConfigurationDialogComponent } from '../../dialogs/copy-configuration-dialog/copy-configuration-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import {
	PLAddConfigData,
	PLEditConfigData,
} from '../../types/pl-edit-config-data';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-plconfig-configuration-dropdown',
	templateUrl: './configuration-dropdown.component.html',
	styles: [],
	imports: [
		AsyncPipe,
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
	],
})
export class ConfigurationDropdownComponent {
	private uiStateService = inject(PlConfigUIStateService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private dialogService = inject(DialogService);
	dialog = inject(MatDialog);

	selectedBranch: Observable<string> = this.uiStateService.branchId.pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);
	configs = this.currentBranchService.views;
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');

	deleteConfig(config: { id: string; name: string }) {
		this.currentBranchService
			.deleteConfiguration(config.id)
			.pipe(take(1))
			.subscribe();
	}

	openEditDialog(configId: string) {
		this.dialogService.openEditConfigDialog(configId, true).subscribe();
	}

	addConfiguration() {
		this.dialog
			.open(AddConfigurationDialogComponent, {
				data: {
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
				},
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined) as OperatorFunction<
					PLAddConfigData | undefined,
					PLAddConfigData
				>,
				switchMap((result) =>
					iif(
						() => result !== undefined,
						this.currentBranchService
							.addConfiguration({
								name: result.title,
								description: result.description,
								copyFrom: result.copyFrom.id,
								configurationGroup: result.group.map(
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
