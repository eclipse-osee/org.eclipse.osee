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
import {
	BehaviorSubject,
	combineLatest,
	map,
	Observable,
	shareReplay,
} from 'rxjs';
import {
	ResponseColumnSchema,
	ResponseTableData,
} from '../../types/grid-commander-types/table-data-types';
import { UserHistoryService } from '../data-services/user-history.service';

@Injectable({
	providedIn: 'root',
})
export class HistoryTableService {
	_combinedDataTable = new BehaviorSubject<ResponseTableData>({
		tableOptions: { columns: [] },
		data: [],
	});
	combinedDataTable$ = this._combinedDataTable.asObservable();

	private datatableColumns: Observable<ResponseColumnSchema[]> =
		this.userHistoryService.userHistory$.pipe(
			map((history) => this.sortColumnHeaders(history.columns)),
			map((columns) => this.modifyColumns(columns))
		);

	private datatableHistoryData = this.userHistoryService.userHistory$.pipe(
		map((history) => this.modifyParameterizedCommand(history.data)),
		map((data) => this.sortRowData(data)),
		map((data) => this.modifyDateFormat(data))
	);

	combinedHistoryData: Observable<ResponseTableData> = combineLatest([
		this.datatableColumns,
		this.datatableHistoryData,
	]).pipe(
		map(([cols, data]) => this.createTableOptionsObject(cols, data)),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	constructor(private userHistoryService: UserHistoryService) {}

	sortColumnHeaders(cols: ResponseColumnSchema[]) {
		const importOrder = [
			'Artifact Id',
			'Name',
			'Parameterized Command',
			'Execution Frequency',
			'Command Timestamp',
			'Is Validated',
			'Favorite',
		];
		const sortByColumnName: { [key: string]: number } = importOrder.reduce(
			(obj, name, index) => {
				return {
					...obj,
					[name]: index,
				};
			},
			{}
		);

		cols.sort(
			(a, b) => sortByColumnName[a.name] - sortByColumnName[b.name]
		);
		return cols;
	}

	modifyColumns(cols: ResponseColumnSchema[]) {
		cols.forEach((column) => {
			switch (column.name) {
				case 'Name':
					return (column.name = 'Command');
				case 'Parameterized Command':
					return (column.name = 'Parameters');
				case 'Command Timestamp':
					return (column.name = 'Last Used');
				case 'Is Validated':
					return (column.name = 'Validated');
				case 'Execution Frequency':
					return (column.name = 'Times Used');
				default:
					return column.name;
			}
		});
		return cols;
	}

	modifyParameterizedCommand(data: string[][]) {
		data.forEach((row) => {
			const string = row[2];
			row[2] = string
				/**using Regex here allows us to find all double quotes and curly braces and removes them from the string by replacing them with an empty string*/
				.replace(/["{}]+/g, '')
				.replace('name:', 'Parameter: ')
				.replace('parameterValue:', ' Value: ');
		});
		return data;
	}

	modifyDateFormat(data: string[][]) {
		data.forEach((row) => {
			const string = row[4].split(' ');
			row[4] = [string[1], string[2], string[5]].join(' ');
		});
		return data;
	}

	sortRowData(data: string[][]) {
		//sort by date
		data.sort((a, b) => (Date.parse(a[4]) <= Date.parse(b[4]) ? -1 : 1));
		//sort by favorite
		data.sort((a, b) => (a[6] >= b[6] ? -1 : 1));
		return data;
	}

	createTableOptionsObject(
		cols: ResponseColumnSchema[],
		data: string[][]
	): ResponseTableData {
		return { tableOptions: { columns: cols }, data };
	}
}
