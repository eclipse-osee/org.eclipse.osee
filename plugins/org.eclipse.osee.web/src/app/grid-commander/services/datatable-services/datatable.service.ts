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
import { BehaviorSubject, combineLatest, from, iif, of, Subject } from 'rxjs';
import {
	ResponseColumnSchema,
	ResponseTableData,
	RowObj,
} from '../../types/grid-commander-types/table-data-types';
import {
	concatMap,
	filter,
	map,
	reduce,
	scan,
	shareReplay,
	switchMap,
	takeUntil,
} from 'rxjs/operators';
import { HistoryTableService } from './history/history-table.service';

@Injectable({
	providedIn: 'root',
})
export class DataTableService {
	//Flags for Action Icons to be displayed: --flags updated from the service that brings in the data to be used in the data table
	canEditableViaActionIcon = new BehaviorSubject<boolean>(false);
	canAddRowViaActionIcon = new BehaviorSubject<boolean>(false);
	canHideViaActionIcon = new BehaviorSubject<boolean>(false);
	multiRowDeleteActionIcon = new BehaviorSubject<boolean>(false);

	/**BS of array of the columns that have been hidden --
	 * IF YOU NEED TO DO SOMETHING TO DATA TABLE AND COLUMNS MAY BE HIDDEN
	 * USE THE _displayedColumns Observable to get the correct columns **/
	hiddenColumns = new BehaviorSubject<string[]>([
		'Artifact Id',
		'Validated',
		'Favorite',
	]);
	_combinedDataTable = new BehaviorSubject<ResponseTableData>({
		tableOptions: { columns: [] },
		data: [],
	});
	_permanentColumns = new BehaviorSubject<ResponseColumnSchema[]>([
		{
			name: 'Action',
			type: 'Action',
		},
	]);

	done = new Subject();

	//TODO: will eventually need to determine how to pull in data of different types (not history) to render an appropriate data table with characteristics related to the data type/functionality
	private combinedDataTableData =
		this.historyTableService.combinedHistoryData.pipe(
			shareReplay({ bufferSize: 1, refCount: true }),
			takeUntil(this.done)
		);

	//creates observable of the data array from mock data
	private _tableData = this.combinedDataTableData.pipe(
		map((val) => val.data)
	);

	private _columns = this.combinedDataTableData.pipe(
		map((val) => val.tableOptions.columns)
	);

	//creates observable of the array of column objects adds the select column and action column to array
	//TODO: check if history schema and don't add the select if History
	private _columnSchema = combineLatest([
		this._columns,
		this._permanentColumns,
	]).pipe(
		switchMap(([colFromAPI, permCols]) => of([...colFromAPI, ...permCols])),
		takeUntil(this.done)
	);

	//creates observable of the keys of the columns for data table
	private _tableColumns = this.columnSchema.pipe(
		map((col) => col.map((column) => column.name))
	);

	private _displayedColumns = combineLatest([
		this.hiddenColumns,
		this._tableColumns,
	]).pipe(
		switchMap(([hiddenCols, tableCols]) =>
			iif(
				() => hiddenCols.length === 0,
				of(tableCols).pipe(),
				from(tableCols).pipe(
					filter((col) => !hiddenCols.includes(col)),
					scan((acc, curr) => [...acc, curr], [] as string[])
				)
			)
		),
		takeUntil(this.done)
	);

	private _displayedTabledata = combineLatest([
		this._tableData,
		this._tableColumns,
	]).pipe(
		switchMap(([rowData, colData]) =>
			combineLatest([of(rowData), of(colData)]).pipe(
				concatMap(([rows, columnHeaders]) =>
					from(rows).pipe(
						concatMap((row) =>
							from(row).pipe(
								reduce((acc, curr, i) => {
									return {
										...acc,
										[columnHeaders[i]]: curr,
									};
								}, {} as RowObj)
							)
						),
						reduce(
							(acc, current) => [...acc, current],
							[] as RowObj[]
						)
					)
				)
			)
		)
	);

	private _columnOptions = this.columnSchema.pipe(
		map((col) =>
			col.map((column) => column.name).filter((col) => col !== 'Action')
		)
	);

	updateHiddenColumns(value: string[]) {
		this.hiddenColumns.next(value);
	}

	constructor(private historyTableService: HistoryTableService) {}

	//will be used to set the combinedDataTable BS to the data that is returned from API
	public set combinedDataTableResponseData(tabularData: ResponseTableData) {
		this._combinedDataTable.next(tabularData);
	}

	public get displayedTableData() {
		return this._displayedTabledata;
	}

	public get tableData() {
		return this._tableData;
	}

	public set tableDataVal(arrData: string[][]) {
		this._tableData = of(arrData);
	}

	public get columnSchema() {
		return this._columnSchema;
	}

	public set columnSchemaVals(data: ResponseColumnSchema[]) {
		this._columnSchema = of(data);
	}

	public get tableCols() {
		return this._tableColumns;
	}

	public get displayedCols() {
		return this._displayedColumns;
	}

	public set displayedColumns(arrCols: string[]) {
		this._displayedColumns = of(arrCols);
	}

	public get columnOptions() {
		return this._columnOptions;
	}

	public set doneFx(val: unknown) {
		this.done.next(val);
	}
}
