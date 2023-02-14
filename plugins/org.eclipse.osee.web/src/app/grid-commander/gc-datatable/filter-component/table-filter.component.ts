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
import { Component, OnDestroy } from '@angular/core';
import { combineLatest, iif, map, of, switchMap } from 'rxjs';
import { FilterService } from '../../services/datatable-services/filter/filter.service';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { ColumnFilterComponent } from './column-filter/column-filter.component';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgIf, NgFor, AsyncPipe, TitleCasePipe } from '@angular/common';

@Component({
	selector: 'osee-table-filter-component',
	templateUrl: './table-filter.component.html',
	styleUrls: ['./table-filter.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		MatFormFieldModule,
		MatSelectModule,
		FormsModule,
		MatChipsModule,
		NgFor,
		MatIconModule,
		MatOptionModule,
		ColumnFilterComponent,
		AsyncPipe,
		TitleCasePipe,
	],
})
export class TableFilterComponent implements OnDestroy {
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

	constructor(private filterService: FilterService) {}

	ngOnDestroy(): void {
		this.filterService.updateColumnsToFilter([]);
	}
	onSelectColumnToFilterBy(event: MatSelectChange) {
		event.value.length === 0
			? this.filterService.updateColumnsToFilter([])
			: this.filterService.updateColumnsToFilter(event.value);
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
