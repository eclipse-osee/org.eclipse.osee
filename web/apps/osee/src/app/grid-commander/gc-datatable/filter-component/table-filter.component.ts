/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe, TitleCasePipe } from '@angular/common';
import { Component, OnDestroy, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatChipListbox,
	MatChipOption,
	MatChipRemove,
} from '@angular/material/chips';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import {
	MatSelect,
	MatSelectChange,
	MatSelectTrigger,
} from '@angular/material/select';
import { combineLatest, iif, map, of, switchMap } from 'rxjs';
import { FilterService } from '../../services/datatable-services/filter/filter.service';
import { ColumnFilterComponent } from './column-filter/column-filter.component';

@Component({
	selector: 'osee-table-filter-component',
	templateUrl: './table-filter.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		ColumnFilterComponent,
		AsyncPipe,
		TitleCasePipe,
		MatFormField,
		MatLabel,
		MatSelect,
		MatSelectTrigger,
		MatChipListbox,
		MatChipOption,
		MatChipRemove,
		MatIcon,
		MatOption,
	],
})
export class TableFilterComponent implements OnDestroy {
	private filterService = inject(FilterService);

	selectedColsToFilter = this.filterService.selectedColumnsToFilter;
	indiciesOfSelectedColumns$ = this.filterService.indicesOfSelectedColumns;
	filterColumnOptions = this.filterService.columnOptionsForFilter;

	//this observable will enable/disable options of mat-select (inside of 'selectedColToFilter())
	_filterType$ = this.selectedColsToFilter.pipe(
		switchMap((columns) =>
			iif(
				() => columns.length === 0,
				of(columns),
				of(columns).pipe(
					switchMap((columns) =>
						iif(
							() => columns.indexOf('all') > -1,
							of('all'),
							of('other')
						)
					)
				)
			)
		)
	);
	_filterInputLabelObs$ = combineLatest([
		this.selectedColsToFilter,
		of('Filter by: '),
	]).pipe(
		switchMap(([columns, labelString]) =>
			iif(
				() => columns.length === 0,
				of(labelString),
				of(columns).pipe(
					map((columns) => labelString + columns.join(', '))
				)
			)
		)
	);

	ngOnDestroy(): void {
		this.filterService.updateColumnsToFilter([]);
	}
	onSelectColumnToFilterBy(event: MatSelectChange) {
		if (event.value.length === 0) {
			this.filterService.updateColumnsToFilter([]);
		} else {
			this.filterService.updateColumnsToFilter(event.value);
		}
	}

	removeColFromFilter(selectedCol: string) {
		this.selectedColsToFilter.next(
			this.selectedColsToFilter.value.filter((val) => val !== selectedCol)
		);
	}

	updateColFilterStrings(e: { input: string }) {
		this.filterService.updateUserInput(e.input);
	}
}
