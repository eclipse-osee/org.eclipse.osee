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
import { NgForm } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { tap } from 'rxjs';
import { CommandPaletteInputService } from '../../services/command-palette-services/command-palette-input.service';
import { CommandGroupOptionsService } from '../../services/data-services/commands/command-group-options.service';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { SelectedCommandDataService } from '../../services/data-services/selected-command-data/selected-command-data.service';
import { Command } from '../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { HelperdialogComponent } from '../helperdialog/helperdialog.component';
import { ParameterTypesComponent } from '../../parameter-types/parameter-types.component';
import { TableFilterComponent } from '../../gc-datatable/filter-component/table-filter.component';
import { InputControlComponent } from '../../shared/input-control/input-control.component';
import { NgIf, AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-command-palette',
	templateUrl: './command-palette.component.html',
	styleUrls: ['./command-palette.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		InputControlComponent,
		TableFilterComponent,
		ParameterTypesComponent,
		AsyncPipe,
	],
})
export class CommandPaletteComponent {
	filteredCommandGroups$ =
		this.commandPaletteInputService.filteredCommandGroups$;
	isFilterEnabled$ = this.selectedCommandDataService.isFilterEnabled;
	_commandNameInput = this.commandPaletteInputService.commandName;
	isParamDefined$ = this.parameterDataService.isParameterTypeDefined;
	helperDialogRef: MatDialogRef<HelperdialogComponent> | undefined;

	constructor(
		private dialogModel: MatDialog,
		private commandGroupOptionsService: CommandGroupOptionsService,
		private selectedCommandDataService: SelectedCommandDataService,
		private parameterDataService: ParameterDataService,
		private commandPaletteInputService: CommandPaletteInputService
	) {}

	_dialog(e?: Event) {
		e?.stopPropagation();
		this.dialogModel
			.open(HelperdialogComponent)
			.afterClosed()
			.pipe(tap(() => this.clearInput()))
			.subscribe();
	}

	_onInput(e: { input: string }) {
		this.commandGroupOptionsService.stringToFilterCommandsBy = e.input;
	}

	_onCommandObjSelected(e: { selectedCommandObj: Command; form: NgForm }) {
		if (e.selectedCommandObj.name === 'Help') {
			this._dialog();
			e.form.reset();
			return;
		}
		this.commandGroupOptionsService.stringToFilterCommandsBy =
			e.selectedCommandObj.name;

		if (
			e.selectedCommandObj.attributes['custom command'] &&
			e.selectedCommandObj.attributes['content url']
		) {
			this.commandPaletteInputService.openCustomCommandUrl(
				new URL(
					e.selectedCommandObj.attributes['content url'].trim(),
					`https://${e.selectedCommandObj.attributes[
						'content url'
					].trim()}`
				)
			);
			e.form.reset();
		}
	}

	clearInput() {
		this.commandGroupOptionsService.stringToFilterCommandsBy = '';
	}
}
