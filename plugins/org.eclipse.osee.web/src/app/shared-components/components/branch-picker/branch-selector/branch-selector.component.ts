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
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { HttpLoadingService } from '../../../../services/http-loading.service';
import { MatOptionLoadingComponent } from '../../../mat-option-loading/mat-option-loading/mat-option-loading.component';
import { BranchListService } from '../../../services/branch-list.service';
import { BranchRoutedUIService } from '../../../services/branch-routed-ui.service';

@Component({
	selector: 'osee-branch-selector',
	templateUrl: './branch-selector.component.html',
	styleUrls: ['./branch-selector.component.sass'],
	standalone: true,
	imports: [
		MatFormFieldModule,
		FormsModule,
		AsyncPipe,
		NgIf,
		MatSelectModule,
		MatOptionLoadingComponent,
		MatOptionModule,
		MatProgressSpinnerModule,
		NgClass,
	],
})
export class BranchSelectorComponent {
	selectedBranchType = this.routeState.type;
	selectedBranchId = '';
	options = this.branchListingService.branches;
	loading = this.loadingService.isLoading;
	constructor(
		private routeState: BranchRoutedUIService,
		private branchListingService: BranchListService,
		private loadingService: HttpLoadingService
	) {
		this.routeState.id.subscribe((val) => {
			this.selectedBranchId = val;
		});
	}

	selectBranch(event: MatSelectChange) {
		this.routeState.branchId = event.value;
	}
}
