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
import { Component, OnChanges, SimpleChanges, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import {
	BehaviorSubject,
	filter,
	from,
	of,
	switchMap,
	take,
	tap,
	combineLatest,
	scan,
} from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UiService, HeaderService } from '@osee/shared/services';
import { TmoService } from '../../../services/tmo.service';
import type { SetReference } from '../../../types/tmo';

@Component({
	selector: 'osee-set-dropdown',
	standalone: true,
	templateUrl: './set-dropdown.component.html',
	imports: [
		CommonModule,
		FormsModule,
		MatAutocompleteModule,
		MatButtonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatMenuModule,
		MatTableModule,
		MatTooltipModule,
	],
})
export class SetDropdownComponent implements OnChanges {
	@Input() setId: string = '';

	constructor(
		private tmoService: TmoService,
		private headerService: HeaderService,
		private ui: UiService,
		public dialog: MatDialog
	) {}

	filterText = new BehaviorSubject<string>('');
	noneOption = { name: 'None' } as SetReference;

	sets = combineLatest([this.tmoService.sets, this.filterText]).pipe(
		switchMap(([setRefs, filterText]) =>
			from(setRefs).pipe(
				filter((a) =>
					a.name.toLowerCase().includes(filterText.toLowerCase())
				),
				scan((acc, curr) => {
					acc.push(curr);
					return acc;
				}, [] as SetReference[])
			)
		)
	);

	selectedSet = combineLatest([this.sets, this.tmoService.setId]).pipe(
		switchMap(([sets, setId]) => {
			const set = sets.find((v) => v.name === setId);
			return set ? of(set) : of(this.noneOption);
		})
	);

	selectSet(set: SetReference) {
		this.tmoService.SetId = set.id;
	}

	applyFilter(text: Event) {
		const value = (text.target as HTMLInputElement).value;
		this.filterText.next(value);
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.tmoService.SetId = this.setId;
	}

	get branchId() {
		return this.tmoService.branchId;
	}

	set BranchId(id: string) {
		this.tmoService.BranchId = id;
	}
}
