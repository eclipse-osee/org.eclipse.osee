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
import { checkIfUndefinedOrNull } from '@osee/shared/utils';
import { BehaviorSubject, combineLatest, from, iif, of } from 'rxjs';
import {
	concatMap,
	debounceTime,
	filter,
	groupBy,
	map,
	mergeMap,
	reduce,
	shareReplay,
	switchMap,
} from 'rxjs/operators';
import {
	Command,
	Parameter,
} from '../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { UserContextRelationsService } from '../data-services/user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class CommandGroupOptionsService {
	private _commands$ = this.userContextRelations.commands;
	private _stringToFilterCommandsBy = new BehaviorSubject<string>('');

	constructor(private userContextRelations: UserContextRelationsService) {}

	private _filteredCommandGroups$ = combineLatest([
		this._commands$,
		this._stringToFilterCommandsBy,
	]).pipe(
		switchMap(([commandGroup, filteringStr]) =>
			iif(
				() => filteringStr.length < 2,
				of(commandGroup).pipe(
					concatMap((groups) =>
						from(groups).pipe(
							groupBy((group) => group.contextGroup),
							mergeMap((groupedObs) =>
								groupedObs.pipe(
									reduce(
										(acc, curr) => {
											acc.contextGroup = groupedObs.key;
											acc.commands.push(curr);
											return acc;
										},
										{ contextGroup: '', commands: [] } as {
											contextGroup: string;
											commands: Command[];
										}
									)
								)
							)
						)
					),
					reduce(
						(acc, curr) => [...acc, curr],
						[] as {
							contextGroup: string;
							commands: Command[];
						}[]
					),
					shareReplay({ bufferSize: 1, refCount: true })
				),
				of(commandGroup).pipe(
					concatMap((groups) =>
						from(groups).pipe(
							filter((group) =>
								(group.name + ' ' + group.contextGroup)
									.toLowerCase()
									.includes(filteringStr.toLowerCase())
							),
							groupBy((group) => group.contextGroup),
							mergeMap((groupedObs) =>
								groupedObs.pipe(
									reduce(
										(acc, curr) => {
											acc.contextGroup = groupedObs.key;
											acc.commands.push(curr);
											return acc;
										},
										{ contextGroup: '', commands: [] } as {
											contextGroup: string;
											commands: Command[];
										}
									)
								)
							)
						)
					),
					reduce(
						(acc, curr) => [...acc, curr],
						[] as {
							contextGroup: string;
							commands: Command[];
						}[]
					)
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _selectedCommandObject$ = this._filteredCommandGroups$.pipe(
		debounceTime(200),
		switchMap((value) =>
			iif(
				() => value.length === 1 && value[0].commands.length === 1,
				of(value).pipe(map((value) => value[0].commands[0])),
				of({
					contextGroup: '',
					name: '',
					id: '',
					idIntValue: 0,
					idString: '',
					attributes: {
						description: '',
						'content url': '',
						'http method': '',
					},
					parameter: {
						name: '',
						id: '',
						typeAsString: '',
						idIntValue: 0,
						idString: '',
						attributes: {
							description: '',
							'default value': '',
							contentUrl: '',
							'is validator used': false,
							'validator type': '',
						},
					},
				})
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _allCommands$ = this._commands$.pipe(
		switchMap((commands) => {
			return of(commands).pipe(
				concatMap((groups) =>
					from(groups).pipe(
						groupBy((group) => group.contextGroup),
						mergeMap((groupedObs) =>
							groupedObs.pipe(
								reduce(
									(acc, curr) => {
										acc.contextGroup = groupedObs.key;
										acc.commands.push(curr);
										return acc;
									},
									{ contextGroup: '', commands: [] } as {
										contextGroup: string;
										commands: Command[];
									}
								)
							)
						)
					)
				),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as { contextGroup: string; commands: Command[] }[]
				)
			);
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get commandsParameter() {
		return this._selectedCommandObject$.pipe(
			map((commandObj) => commandObj.parameter),
			filter((parameter): parameter is Parameter =>
				checkIfUndefinedOrNull(parameter)
			)
		);
	}

	public get isParameterTypeDefined() {
		return this._selectedCommandObject$
			.pipe(map((commandObj) => commandObj.parameter))
			.pipe(
				switchMap((param) =>
					iif(
						() => param?.typeAsString !== '' && param !== null,
						of(true),
						of(false)
					)
				)
			);
	}

	public get isFilterDisabled() {
		return this._selectedCommandObject$.pipe(
			switchMap((command) =>
				iif(() => command.name === 'Filter', of(true), of(false))
			)
		);
	}

	public get stringThatFiltersCommands() {
		return this._stringToFilterCommandsBy;
	}

	public set stringToFilterCommandsBy(str: string) {
		this._stringToFilterCommandsBy.next(str);
	}

	public get allCommands() {
		return this._allCommands$;
	}

	public get filteredCommandGroups() {
		return this._filteredCommandGroups$;
	}

	public get selectedCommandObject() {
		return this._selectedCommandObject$;
	}

	public set selectedCommandObject(value) {
		this._selectedCommandObject$ = value;
	}
}
