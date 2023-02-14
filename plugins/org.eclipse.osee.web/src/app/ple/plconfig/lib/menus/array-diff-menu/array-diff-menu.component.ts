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
import { NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';

@Component({
	selector: 'osee-plconfig-array-diff-menu',
	templateUrl: './array-diff-menu.component.html',
	styleUrls: ['./array-diff-menu.component.sass'],
	standalone: true,
	imports: [NgIf, NgFor, MatMenuModule],
})
export class ArrayDiffMenuComponent {
	@Input() array: difference[] = [];
	constructor(private currentBranchService: PlConfigCurrentBranchService) {}

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
