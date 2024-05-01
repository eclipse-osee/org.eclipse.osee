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
import { Injectable } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	iif,
	map,
	Observable,
	of,
	switchMap,
	tap,
} from 'rxjs';
import { executedCommand } from '../../../../types/grid-commander-types/executedCommand';
import {
	Command,
	Parameter,
} from '../../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { RowObj } from '../../../../types/grid-commander-types/table-data-types';
import { userHistory } from '../../../../types/grid-commander-types/userHistory';
import { DataTableService } from '../../../datatable-services/datatable.service';
import { UserHistoryService } from '../../history/user-history.service';
import { ParameterDataService } from '../parameter-data/parameter-data.service';
import { SelectedCommandDataService } from '../selected-command-data.service';

@Injectable({
	providedIn: 'root',
})
export class CommandFromUserHistoryService {
	parameter$ = this.parameterDataService.parameter$;
	userHistory$ = this.userHistoryService.userHistory$;
	selectedCommand$ = this.selectedCommandDataService.selectedCommandObject;

	private _selectedCommandFromHistoryTableId = new BehaviorSubject<string>(
		''
	);
	private _fromHistory = new BehaviorSubject<boolean>(false);

	private _selectedExecutedCommandObject$: Observable<executedCommand> =
		combineLatest([
			this._selectedCommandFromHistoryTableId,
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

	private _executedCommandDetails$ = combineLatest([
		this._selectedCommandFromHistoryTableId,
		this.parameter$,
		this.userHistory$,
	]).pipe(
		switchMap(([id, parameter, userHistory]) =>
			iif(
				() => id !== '' && id !== undefined,
				of(this._selectedExecutedCommandObject$).pipe(
					switchMap((commandObject) => commandObject),
					map((command) =>
						this.createExecutedCmdDetailsWithId(command)
					)
				),
				of(parameter)
					.pipe(
						switchMap((parameter) =>
							this.buildParameterizeCommand(parameter)
						)
					)
					.pipe(
						switchMap((builtParameterizedCommand) =>
							iif(
								() =>
									this.matchesArtifactFromUserHistory(
										userHistory,
										builtParameterizedCommand
									),
								of(builtParameterizedCommand).pipe(
									switchMap((parameterizedCommandObj) =>
										this.createExecutedCmdDetails(
											userHistory,
											parameterizedCommandObj
										)
									)
								),
								of({
									id: '',
									executionFrequency: 0,
									commandTimestamp: 0,
								})
							)
						)
					)
			)
		)
	);

	parameterValueFromHistory$ = combineLatest([
		this.selectedExecutedCommandObject,
		this._fromHistory,
	]).pipe(
		switchMap(([commandHistoryObj, fromHistory]) =>
			iif(
				() => fromHistory === true,
				of(commandHistoryObj).pipe(
					map((commandFromHistory) =>
						commandFromHistory.parameterizedCommand
							.split('Value:')[1]
							.trim()
					)
				),
				of('').pipe()
			)
		)
	);

	constructor(
		private dataTableService: DataTableService,
		private selectedCommandDataService: SelectedCommandDataService,
		private parameterDataService: ParameterDataService,
		private userHistoryService: UserHistoryService
	) {}

	matchesArtifactFromUserHistory(
		usersHistory: userHistory,
		executedCommandObject: Partial<executedCommand>
	) {
		if (
			this.filterUserHistory(usersHistory, executedCommandObject)
				.length === 1
		) {
			this.fromHistory = true;
			return true;
		}
		this.fromHistory = false;
		return false;
	}

	filterUserHistory(
		usersHistory: userHistory,
		executedCommandObject: Partial<executedCommand>
	) {
		return usersHistory.data.filter(
			(historyObj) =>
				historyObj[1] === executedCommandObject.name &&
				historyObj[2] ===
					(executedCommandObject.parameterizedCommand &&
						executedCommandObject.parameterizedCommand
							.replace(/["{}]+/g, '')
							.replace('name:', 'Parameter: ')
							.replace('parameterValue:', ' Value: '))
		);
	}

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

	createExecutedCmdDetailsWithId(
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

	createExecutedCmdDetails(
		usersHistory: userHistory,
		executedCommandObject: Partial<executedCommand>
	) {
		let commandFromHistory = this.filterUserHistory(
			usersHistory,
			executedCommandObject
		).flat();

		return of({
			id: commandFromHistory[0],
			executionFrequency: this.incrementExFrequency(
				Number(commandFromHistory[3])
			),
			commandTimestamp: new Date().getTime(),
		} satisfies Partial<executedCommand>);
	}

	buildParameterizeCommand(parameter: Parameter) {
		const paramCommand =
			this.parameterDataService.parameterStringInput.pipe(
				switchMap((userInput) =>
					this.createParameterizedCommandObj(parameter, userInput)
				)
			);
		return combineLatest([this.selectedCommand$, paramCommand]).pipe(
			switchMap(([command, paramCommand]) =>
				this.createNewExecutedCommandObj(command, paramCommand)
			)
		);
	}

	//transform to parameterizedCommand object
	createParameterizedCommandObj(parameter: Parameter, paramValue: string) {
		return of(`{ name: ${parameter.name}, parameterValue: ${paramValue} }`);
	}

	//transform to exCommandObj object to build artifact
	createNewExecutedCommandObj(
		command: Command,
		parameterizedCmdObj: string,
		favorite: boolean = false,
		validated: boolean = true
	) {
		return of({
			name: command.name,
			executionFrequency: 1,
			commandTimestamp: new Date().getTime(),
			parameterizedCommand: parameterizedCmdObj,
			favorite: favorite,
			isValidated: validated,
		} satisfies Partial<executedCommand>);
	}

	incrementExFrequency(timesExecuted: number) {
		return timesExecuted + 1;
	}

	public get selectedExecutedCommandObject() {
		return this._selectedExecutedCommandObject$;
	}

	public get selectedCommandFromHistoryTableId() {
		return this._selectedCommandFromHistoryTableId.value;
	}

	public set selectedCommandFromHistoryTableId(value: string) {
		this._selectedCommandFromHistoryTableId.next(value);
	}

	public get executedCommandDetails() {
		return this._executedCommandDetails$;
	}

	public get fromHistory() {
		return this._fromHistory.value;
	}

	public get fromHistoryAsObservable() {
		return this._fromHistory.asObservable();
	}

	public set fromHistory(value: boolean) {
		this._fromHistory.next(value);
	}
}
