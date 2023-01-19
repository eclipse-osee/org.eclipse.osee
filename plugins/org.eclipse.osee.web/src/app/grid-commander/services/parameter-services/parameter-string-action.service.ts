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
	switchMap,
	combineLatest,
	map,
	Observable,
	iif,
	of,
} from 'rxjs';
import { executedCommand } from '../../types/grid-commander-types/executedCommand';
import { RowObj } from '../../types/grid-commander-types/table-data-types';
import { DataTableService } from '../datatable-services/datatable.service';

@Injectable({
	providedIn: 'root',
})
export class ParameterStringActionService {
	private _selectedCommandId = new BehaviorSubject<string>('');
	private _fromHistory = new BehaviorSubject<Boolean>(false);

	private _selectedExecutedCommandObject: Observable<executedCommand> =
		combineLatest([
			this._selectedCommandId,
			this.dataTableService.displayedTableData,
		]).pipe(
			switchMap(([commandId, tableData]) =>
				tableData.filter(
					(rowData) => rowData['Artifact Id'] === commandId
				)
			),
			map((commandAsRowObject) =>
				this.convertToExecutedCommandType(commandAsRowObject)
			)
		);

	private _executedCommandObject = this._selectedCommandId.pipe(
		switchMap((id) =>
			iif(
				() => id !== '' && id !== undefined,
				of(this._selectedExecutedCommandObject).pipe(
					switchMap((commandObject) => commandObject),
					map((command) => this.createArtifactForExecution(command))
				),
				of({
					id: '',
					executionFrequency: 0,
					commandTimestamp: 0,
				}).pipe()
			)
		)
	);

	constructor(private dataTableService: DataTableService) {}

	convertToExecutedCommandType(commandFromHistory: RowObj): executedCommand {
		return {
			id: commandFromHistory['Artifact Id'].toString(),
			name: commandFromHistory.Command.toString(),
			executionFrequency: Number(commandFromHistory['Times Used']),
			commandTimestamp: commandFromHistory['Last Used'].toString(),
			parameterizedCommand: commandFromHistory.Parameters.toString(),
			favorite: commandFromHistory.Favorite === 'true' ? true : false,
			isValidated: commandFromHistory.Validated === 'true' ? true : false,
		};
	}

	createArtifactForExecution(
		command: executedCommand
	): Partial<executedCommand> {
		const newExFrequency = this.incrementExFrequency(
			command.executionFrequency
		);
		if (command.id === undefined)
			return {
				id: '',
				executionFrequency: newExFrequency,
				commandTimestamp: new Date().getTime(),
			};

		return {
			id: command.id,
			executionFrequency: newExFrequency,
			commandTimestamp: new Date().getTime(),
		};
	}

	incrementExFrequency(timesExecuted: number) {
		return timesExecuted + 1;
	}

	public get selectedExecutedCommandObject() {
		return this._selectedExecutedCommandObject;
	}

	public get selectedCommandId() {
		return this._selectedCommandId.value;
	}

	public set selectedCommandId(value: string) {
		this._selectedCommandId.next(value);
	}

	public get executedCommandObject() {
		return this._executedCommandObject;
	}

	public get fromHistory() {
		return this._fromHistory;
	}

	public set fromHistory(value) {
		this._fromHistory = value;
	}
}
