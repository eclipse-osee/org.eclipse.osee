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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { CiSetDiffService } from '../../../services/ci-set-diff.service';
import { CiSetsService } from '../../../services/ci-sets.service';
import { CISet } from '../../../types';

@Component({
	selector: 'osee-set-dropdown-multi',
	standalone: true,
	imports: [
		AsyncPipe,
		MatCheckbox,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		FormsModule,
	],
	templateUrl: './set-dropdown-multi.component.html',
})
export class SetDropdownMultiComponent {
	private ciSetsService = inject(CiSetsService);
	private diffService = inject(CiSetDiffService);

	activeOnly = toSignal(this.ciSetsService.activeOnly);

	sets = this.ciSetsService.ciSets;

	selectedSets = this.diffService.selectedSets;

	setActiveOnly(event: MatCheckboxChange) {
		this.ciSetsService.ActiveOnly = event.checked;
	}

	updateSets(val: CISet[]) {
		this.diffService.SelectedSets = val;
	}

	compareSets(set1: CISet, set2: CISet) {
		return set1 && set2 ? set1.id === set2.id : false;
	}
}
