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
import { NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { applic } from '@osee/shared/types/applicability';
import { difference } from '@osee/shared/types/change-report';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import {
	extendedFeature,
	extendedFeatureWithChanges,
} from '../../types/features/base';
import { ArrayDiffMenuComponent } from '../array-diff-menu/array-diff-menu.component';

@Component({
	selector: 'osee-plconfig-feature-menu',
	templateUrl: './feature-menu.component.html',
	styles: [],
	standalone: true,
	imports: [MatMenuModule, MatIconModule, NgIf, ArrayDiffMenuComponent],
})
export class FeatureMenuComponent {
	@Input() feature: extendedFeature | extendedFeatureWithChanges = {
		id: '',
		type: undefined,
		name: '',
		description: '',
		valueType: '',
		valueStr: '',
		defaultValue: '',
		values: [],
		productApplicabilities: [],
		multiValued: false,
		configurations: [],
		setValueStr() {},
		setProductAppStr() {},
	};
	constructor(
		private dialogService: DialogService,
		private currentBranchService: PlConfigCurrentBranchService
	) {}
	hasFeatureChanges(
		value: extendedFeature | extendedFeatureWithChanges
	): value is extendedFeatureWithChanges {
		return (value as extendedFeatureWithChanges).changes !== undefined;
	}
	displayFeatureMenu(feature: extendedFeature) {
		this.dialogService.displayFeatureMenu(feature).subscribe();
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
