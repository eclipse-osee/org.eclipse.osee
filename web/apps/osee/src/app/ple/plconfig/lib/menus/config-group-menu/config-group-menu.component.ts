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
import { Component, computed, inject, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { applic } from '@osee/applicability/types';
import { difference } from '@osee/shared/types/change-report';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { configGroup } from '../../types/pl-config-configurations';
import { ArrayDiffMenuComponent } from '../array-diff-menu/array-diff-menu.component';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-config-group-menu',
	templateUrl: './config-group-menu.component.html',
	styles: [],
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
		ArrayDiffMenuComponent,
	],
})
export class ConfigGroupMenuComponent {
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	private dialogService = inject(DialogService);
	private currentBranchService = inject(PlConfigCurrentBranchService);

	group = input.required<configGroup>();
	openConfigMenu(header: string, editable: boolean) {
		this.dialogService
			.openEditConfigGroupDialog(header, editable)
			.subscribe();
	}
	viewDiff(open: boolean, value: difference, header: string) {
		let current = value.currentValue as string | number | applic;
		let prev = value.previousValue as string | number | applic;
		if (prev === null) {
			prev = '';
		}
		if (current === null) {
			current = '';
		}
		this.currentBranchService.sideNav = {
			opened: open,
			field: header,
			currentValue: current,
			previousValue: prev,
			transaction: value.transactionToken,
		};
	}
	hasGroupChanges(value: configGroup): value is configGroup {
		return (value as configGroup).changes !== undefined;
	}
}
