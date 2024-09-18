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
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { plconfigTableEntry } from '../../types/pl-config-table';
import { ArrayDiffMenuComponent } from '../array-diff-menu/array-diff-menu.component';

@Component({
	selector: 'osee-plconfig-feature-menu',
	templateUrl: './feature-menu.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
		ArrayDiffMenuComponent,
	],
})
export class FeatureMenuComponent {
	feature = input.required<plconfigTableEntry>();
	constructor(
		private dialogService: DialogService,
		private currentBranchService: PlConfigCurrentBranchService
	) {}
	hasFeatureChanges(value: plconfigTableEntry): value is plconfigTableEntry {
		return (value as plconfigTableEntry).changes !== undefined;
	}
	displayFeatureMenu(feature: string) {
		this.dialogService.displayFeatureDialog(feature).subscribe();
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
}
