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
import {
	view,
	viewWithChanges,
} from '../../types/pl-config-applicui-branch-mapping';
import { ArrayDiffMenuComponent } from '../array-diff-menu/array-diff-menu.component';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-config-menu',
	templateUrl: './config-menu.component.html',
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
export class ConfigMenuComponent {
	private dialogService = inject(DialogService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	config = input.required<view | viewWithChanges>();
	openConfigMenu(header: string, editable: boolean) {
		this.dialogService.openEditConfigDialog(header, editable).subscribe();
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
	hasViewChanges(value: view | viewWithChanges): value is viewWithChanges {
		return (value as viewWithChanges).changes !== undefined;
	}
}
