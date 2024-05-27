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
import {
	Component,
	Input,
	OnChanges,
	SimpleChanges,
	inject,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	from,
	of,
	reduce,
	switchMap,
} from 'rxjs';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { CiSetsService } from '../../../services/ci-sets.service';
import type { SetReference } from '../../../types/tmo';

@Component({
	selector: 'osee-set-dropdown',
	standalone: true,
	templateUrl: './set-dropdown.component.html',
	imports: [
		AsyncPipe,
		FormsModule,
		MatCheckbox,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
	],
})
export class SetDropdownComponent implements OnChanges {
	private ciSetsService = inject(CiSetsService);
	private ui = inject(CiDashboardUiService);

	@Input() setId = '';

	filterText = new BehaviorSubject<string>('');

	sets = combineLatest([this.ciSetsService.ciSets, this.filterText]).pipe(
		switchMap(([setRefs, filterText]) =>
			from(setRefs).pipe(
				filter((a) =>
					a.name.toLowerCase().includes(filterText.toLowerCase())
				),
				reduce((acc, curr) => [...acc, curr], [] as SetReference[])
			)
		)
	);

	activeOnly = toSignal(this.ciSetsService.activeOnly);

	selectedSet = combineLatest([this.sets, this.ui.ciSetId]).pipe(
		switchMap(([sets, setId]) => {
			if (setId === undefined || setId === '' || setId === '-1') {
				if (sets.length > 0) {
					this.selectSet(sets[0]);
					return of(sets[0]);
				}
				return of(undefined);
			}
			const set = sets.find((v) => v.id === setId);
			return set ? of(set) : of(undefined);
		})
	);

	setActiveOnly(event: MatCheckboxChange) {
		this.ciSetsService.ActiveOnly = event.checked;
	}

	selectSet(set: SetReference) {
		this.ui.routeToSet(set.id);
	}

	applyFilter(text: Event) {
		const value = (text.target as HTMLInputElement).value;
		this.filterText.next(value);
	}

	ngOnChanges(_changes: SimpleChanges): void {
		this.ui.CiSetId = this.setId;
	}
}
