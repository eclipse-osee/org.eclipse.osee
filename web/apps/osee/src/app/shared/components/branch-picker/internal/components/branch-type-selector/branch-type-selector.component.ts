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
	styles: [
		`
			:host {
				--mat-button-toggle-label-text-size: 13px;
				--mat-button-toggle-height: 32px;
				--mat-button-toggle-selected-state-background-color: var(
					--osee-primary-default
				);
				--mat-button-toggle-selected-state-text-color: var(
					--osee-background-background
				);
				--mat-button-toggle-text-color: var(
					--osee-foreground-secondary-text
				);
				--mat-button-toggle-shape: 4px;
			}
		`,
	],
	imports: [MatButtonToggleGroup, MatButtonToggle],
})
export class BranchTypeSelectorComponent implements OnInit {
	private routerState = inject(BranchRoutedUIService);

	branchType = '';

	ngOnInit(): void {
		this.routerState.type.subscribe((value) => {
			this.branchType = value;
		});
	}

	selectType(event: MatButtonToggleChange) {
		this.routerState.branchType = event.value as
			| 'working'
			| 'baseline'
			| '';
	}
}
