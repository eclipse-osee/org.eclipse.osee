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
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';
import { OperatorFunction, iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { AddConfigurationGroupDialogComponent } from '../../dialogs/add-configuration-group-dialog/add-configuration-group-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { addCfgGroup } from '../../types/pl-config-cfggroups';

@Component({
	selector: 'osee-plconfig-configuration-group-dropdown',
	templateUrl: './configuration-group-dropdown.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
		AsyncPipe,
	],
})
export class ConfigurationGroupDropdownComponent {
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	cfgGroups = this.currentBranchService.cfgGroups;
	constructor(
		private currentBranchService: PlConfigCurrentBranchService,
		public dialog: MatDialog
	) {}
	public addConfigurationGroup() {
		this.dialog
			.open(AddConfigurationGroupDialogComponent, {
				data: {
					title: '',
				},
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined) as OperatorFunction<
					addCfgGroup | undefined,
					addCfgGroup
				>,
				switchMap((result) =>
					iif(
						() => result !== undefined,
						this.currentBranchService
							.addConfigurationGroup({
								name: result.title,
								description: result.description,
							})
							.pipe(take(1)),
						of(undefined)
					)
				)
			)
			.subscribe();
	}
	deleteGroup(id: string) {
		this.currentBranchService
			.deleteConfigurationGroup(id)
			.pipe(take(1))
			.subscribe((response) => {});
	}
	toggleMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.toggleMenu();
	}
}
