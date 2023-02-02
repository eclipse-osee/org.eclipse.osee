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
	combineLatest,
	debounceTime,
	iif,
	map,
	of,
	shareReplay,
	switchMap,
} from 'rxjs';
import { Command } from '../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { CommandGroupOptionsService } from '../commands/command-group-options.service';
import { UserContextRelationsService } from '../context-relations/user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class SelectedCommandDataService {
	private _filteredCommandGroups$ =
		this.commandGroupOptionsService.filteredCommandGroups;

	constructor(
		private commandGroupOptionsService: CommandGroupOptionsService,
		private userContextRelations: UserContextRelationsService
	) {}

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
							'is validator used': false,
							'validator type': '',
						},
					},
				} as Command)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get selectedCommandNotEmpty() {
		return this._selectedCommandObject$.pipe(
			switchMap((command) =>
				iif(
					() => command.name === '' && command.id === '',
					of(),
					of(true)
				)
			)
		);
	}
	public get contextData() {
		return combineLatest([
			this._selectedCommandObject$,
			this.userContextRelations.contexts,
		]).pipe(
			switchMap(([selectedCommand, contexts]) =>
				contexts.filter(
					(context) =>
						context.indexOf(selectedCommand.contextGroup) === 0
				)
			)
		);
	}

	public get isCustomCommand() {
		return this._selectedCommandObject$.pipe(
			switchMap((command) =>
				iif(
					() =>
						command.attributes['custom command'] !== undefined &&
						command.attributes['custom command'] !== null &&
						command.attributes['custom command'],
					of(true),
					of(false)
				)
			)
		);
	}

	public get isFilterEnabled() {
		return this._selectedCommandObject$.pipe(
			switchMap((command) =>
				iif(() => command.name === 'Filter', of(true), of(false))
			)
		);
	}

	public get displayCreateNewCommandform() {
		return this._selectedCommandObject$.pipe(
			switchMap((command) =>
				iif(
					() => command.name === 'Create New Command',
					of(true),
					of(false)
				)
			)
		);
	}

	public get selectedCommandObject() {
		return this._selectedCommandObject$;
	}

	public set selectedCommandObject(value) {
		this._selectedCommandObject$ = value;
	}
}
