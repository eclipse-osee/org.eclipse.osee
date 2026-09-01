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
import { Component, OnInit, inject } from '@angular/core';
import {
	MatButtonToggle,
	MatButtonToggleChange,
	MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { BranchRoutedUIService } from '@osee/shared/services';

@Component({
	selector: 'osee-branch-type-selector',
	templateUrl: './branch-type-selector.component.html',
	imports: [MatButtonToggleGroup, MatButtonToggle],
})
export class BranchTypeSelectorComponent implements OnInit {
	private routerState = inject(BranchRoutedUIService);

	branchType = '';

	ngOnInit(): void {
		this.routerState.type.subscribe((value) => {
			this.branchType = value;
		});
		// Default to baseline if no type is set
		if (!this.branchType) {
			this.routerState.branchType = 'baseline';
		}
	}

	selectType(event: MatButtonToggleChange) {
		this.routerState.branchType = event.value as
			| 'working'
			| 'baseline'
			| '';
	}
}
