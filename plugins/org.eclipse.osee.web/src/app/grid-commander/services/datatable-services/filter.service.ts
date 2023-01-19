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
import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, from, of } from 'rxjs';
import {
	concatMap,
	filter,
	map,
	reduce,
	startWith,
	switchMap,
} from 'rxjs/operators';
import { DataTableService } from './datatable.service';

@Injectable({
	providedIn: 'root',
})
export class FilterService {
	//this creates the filtering string that will be used in the filtering function -- getFilterPredicate() in gc-datatable.component.ts
	private _filterString = new BehaviorSubject<string>('');

	private _columnsNotUsedForFilter = ['action'];
	private _userInput = new BehaviorSubject<string>('');
	private _selectedColumnsToFilter = new BehaviorSubject<string[]>([]);

	private _rowObjProperties = this.dataTableService.tableCols.pipe(
		switchMap((columns) =>
			of(columns).pipe(
				concatMap((columns) =>
					from(columns).pipe(
						startWith('all'),
						filter(
							(val) =>
								!this._columnsNotUsedForFilter.includes(
									val.toLowerCase().trim()
								)
						)
					)
				),
				reduce((acc, curr) => [...acc, curr], [] as string[])
			)
		)
	);

	private _columnOptionsForFilter = this.dataTableService.displayedCols.pipe(
		switchMap((columns) =>
			of(columns).pipe(
				concatMap((columns) =>
					from(columns).pipe(
						startWith('all'),
						filter(
							(val) =>
								!this._columnsNotUsedForFilter.includes(
									val.toLowerCase().trim()
								)
						)
					)
				),
				reduce((acc, curr) => [...acc, curr], [] as string[])
			)
		)
	);

	private _indicesOfSelectedColumns = combineLatest([
		this.selectedColumnsToFilter,
		this.rowObjPropertiesForFilter,
	]).pipe(
		map(([selectedColumn, colArray]) =>
			selectedColumn.map((col) => colArray.indexOf(col).toString())
		)
	);

	private _constructFilterString = combineLatest([
		this.rowObjPropertiesForFilter,
		this.indicesOfSelectedColumns,
		this.userInput,
	]).pipe(
		switchMap(([properties, selectedColumns, input]) =>
			of().pipe(
				startWith(new Array<string>(properties.length).fill('')),
				map((emptyArr) => {
					selectedColumns.forEach((index) => {
						if (emptyArr[Number(index)] !== undefined) {
							emptyArr[Number(index)] = input;
						}
					});
					return emptyArr;
				}),
				map((arr) =>
					arr.map((val) => (val === '' ? (val = '_') : val)).join('$')
				)
			)
		)
	);

	updateValuesToFilter(filterValues: string) {
		this._filterString.next(filterValues);
	}

	updateUserInput(input: string) {
		this._userInput.next(input);
	}

	updateColumnsToFilter(columns: string[]) {
		this.selectedColumnsToFilter.next(columns);
	}

	constructor(private dataTableService: DataTableService) {}

	public get selectedColumnsToFilter() {
		return this._selectedColumnsToFilter;
	}

	public get indicesOfSelectedColumns() {
		return this._indicesOfSelectedColumns;
	}

	public get userInput() {
		return this._userInput;
	}

	public get filterValues() {
		return this._filterString;
	}

	public get constructFilterString() {
		return this._constructFilterString;
	}

	public get rowObjPropertiesForFilter() {
		return this._rowObjProperties;
	}

	public get columnOptionsForFilter() {
		return this._columnOptionsForFilter;
	}
}
