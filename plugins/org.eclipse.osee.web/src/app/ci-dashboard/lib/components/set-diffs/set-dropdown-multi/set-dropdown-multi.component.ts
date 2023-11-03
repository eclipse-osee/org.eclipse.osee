/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	MatCheckboxChange,
	MatCheckboxModule,
} from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiSetsService } from '../../../services/ci-sets.service';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { SetReference } from '../../../types';
import { CiSetDiffService } from '../../../services/ci-set-diff.service';

@Component({
	selector: 'osee-set-dropdown-multi',
	standalone: true,
	imports: [
		CommonModule,
		MatCheckboxModule,
		MatFormFieldModule,
		MatSelectModule,
		FormsModule,
	],
	templateUrl: './set-dropdown-multi.component.html',
})
export class SetDropdownMultiComponent {
	constructor(
		private ciSetsService: CiSetsService,
		private diffService: CiSetDiffService
	) {}

	activeOnly = toSignal(this.ciSetsService.activeOnly);

	sets = this.ciSetsService.ciSets;

	selectedSets = this.diffService.selectedSets;

	setActiveOnly(event: MatCheckboxChange) {
		this.ciSetsService.ActiveOnly = event.checked;
	}

	updateSets(val: SetReference[]) {
		this.diffService.SelectedSets = val;
	}

	compareSets(set1: SetReference, set2: SetReference) {
		return set1 && set2 ? set1.id === set2.id : false;
	}
}
