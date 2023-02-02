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
import { combineLatest, iif, map, of, switchMap, take } from 'rxjs';
import { CommandGroupOptionsService } from '../data-services/commands/command-group-options.service';
import { ExecutedCommandsService } from '../data-services/execution-services/executed-commands.service';
import { ParameterDataService } from '../data-services/selected-command-data/parameter-data/parameter-data.service';
import { SelectedCommandDataService } from '../data-services/selected-command-data/selected-command-data.service';

@Injectable({
	providedIn: 'root',
})
export class CommandPaletteInputService {
	filterString = this.commandGroupOptService.stringThatFiltersCommands;
	filteredCommandGroups$ = this.commandGroupOptService.filteredCommandGroups;
	private _predictiveText = combineLatest([
		this.filteredCommandGroups$,
		this.filterString,
		this.selectedCommandDataService.selectedCommandObject,
	]).pipe(
		switchMap(([filteredCommands, filterValue]) =>
			filterValue.length >= 2 && filteredCommands.length > 0
				? of(filteredCommands).pipe(
						switchMap((filteredCommands) =>
							iif(
								() =>
									filteredCommands[0]?.commands[0].name
										.toLowerCase()
										.startsWith(filterValue.toLowerCase()),
								of(filteredCommands).pipe(
									switchMap(
										(filteredCommand) =>
											filteredCommand[0]?.commands
									),
									take(1),
									map((command) => command?.name)
								),
								of('')
							)
						)
				  )
				: of('')
		)
	);

	private _commandName = combineLatest([
		this.selectedCommandDataService.selectedCommandObject,
		this.filterString,
	]).pipe(
		switchMap(([commandObj, filterString]) =>
			iif(
				() => commandObj.name === '' || filterString.length < 2,
				of(filterString),
				of(commandObj).pipe(map((val) => val.name))
			)
		)
	);

	openCustomCommandUrl(contentUrl: URL) {
		this.parameterDataService.updateParameterStringInput(contentUrl.host);
		window.open(contentUrl.origin, '_blank');
		this.executedCommandsService.updateCommand.subscribe();
	}

	constructor(
		private commandGroupOptService: CommandGroupOptionsService,
		private selectedCommandDataService: SelectedCommandDataService,
		private parameterDataService: ParameterDataService,
		private executedCommandsService: ExecutedCommandsService
	) {}

	public get predictiveText() {
		return this._predictiveText;
	}

	public get commandName() {
		return this._commandName;
	}
}
