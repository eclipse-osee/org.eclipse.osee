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
import { NgIf, AsyncPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import {
	configGroup,
	configGroupWithChanges,
} from '../../types/pl-config-configurations';
import { ArrayDiffMenuComponent } from '../array-diff-menu/array-diff-menu.component';

@Component({
	selector: 'osee-plconfig-config-group-menu',
	templateUrl: './config-group-menu.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatMenuModule,
		MatIconModule,
		NgIf,
		AsyncPipe,
		ArrayDiffMenuComponent,
	],
})
export class ConfigGroupMenuComponent {
	constructor(
		private dialogService: DialogService,
		private uiStateService: PlConfigUIStateService,
		private currentBranchService: PlConfigCurrentBranchService
	) {}
	_editable = this.uiStateService.editable;
	@Input() group: configGroup | configGroupWithChanges = {
		name: '',
		description: '',
		id: '',
		configurations: [],
	};
	openConfigMenu(header: string, editable: string) {
		this.dialogService.openConfigMenu(header, editable).subscribe();
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
	hasGroupChanges(
		value: configGroup | configGroupWithChanges
	): value is configGroupWithChanges {
		return (value as configGroupWithChanges).changes !== undefined;
	}
}
