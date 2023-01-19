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
import { Component } from '@angular/core';
import { CommandGroupOptionsService } from '../../services/data-services/command-group-options.service';
import { CommandPaletteInputService } from '../../services/command-palette-services/command-palette-input.service';

@Component({
	selector: 'osee-command-palette',
	templateUrl: './command-palette.component.html',
	styleUrls: ['./command-palette.component.sass'],
})
export class CommandPaletteComponent {
	inputValue = this.commandGroupOptService.stringThatFiltersCommands;
	filteredCommandGroups$ =
		this.commandPaletteInputService.filteredCommandGroups$;
	isFilterDisabled$ = this.commandGroupOptService.isFilterDisabled;
	_commandNameInput = this.commandPaletteInputService.commandName;
	isParamDefined$ = this.commandPaletteInputService.isParamDefined$;

	constructor(
		private commandGroupOptService: CommandGroupOptionsService,
		private commandPaletteInputService: CommandPaletteInputService
	) {}
}
