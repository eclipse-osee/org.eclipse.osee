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
import { BehaviorSubject, combineLatest, from, iif, of } from 'rxjs';
import {
	concatMap,
	filter,
	groupBy,
	mergeMap,
	reduce,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs/operators';
import { Command } from '../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { UserContextRelationsService } from '../context-relations/user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class CommandGroupOptionsService {
	private _commands$ = this.userContextRelations.commands;
	private _stringToFilterCommandsBy = new BehaviorSubject<string>('');

	constructor(private userContextRelations: UserContextRelationsService) {}

	commandsSortFunction(commands: Command[]) {
		return commands.sort((a, b) =>
			a.name > b.name ? 1 : b.name > a.name ? -1 : 0
		);
	}

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
											this.commandsSortFunction(
												acc.commands
											);
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
											this.commandsSortFunction(
												acc.commands
											);
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

	public get stringThatFiltersCommands() {
		return this._stringToFilterCommandsBy;
	}

	public set stringToFilterCommandsBy(str: string) {
		this._stringToFilterCommandsBy.next(str);
	}
	public get filteredCommandGroups() {
		return this._filteredCommandGroups$;
	}

	public get allCommands() {
		return this._allCommands$;
	}
}
