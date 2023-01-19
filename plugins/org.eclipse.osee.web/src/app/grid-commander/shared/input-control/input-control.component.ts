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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import {
	MatFormFieldControl,
	FloatLabelType,
} from '@angular/material/form-field';
import { tap } from 'rxjs';
import { HelperdialogComponent } from '../../command-palette/helperdialog/helperdialog.component';
import {
	Command,
	CommandGroups,
} from '../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { CommandPaletteInputService } from '../../services/command-palette-services/command-palette-input.service';
import { CommandGroupOptionsService } from '../../services/data-services/command-group-options.service';
import { ParameterStringActionService } from '../../services/parameter-services/parameter-string-action.service';

@Component({
	selector: 'osee-input-control',
	templateUrl: './input-control.component.html',
	styleUrls: ['./input-control.component.sass'],
	providers: [
		{
			provide: MatFormFieldControl,
			useExisting: InputControlComponent,
			multi: true,
		},
	],
})
export class InputControlComponent<T extends CommandGroups> {
	@Input() input!: string | null;
	@Input() label!: string;
	@Input() placeHolder!: string;
	@Input() displayHelpIcon!: boolean;
	@Input() floatLabelControl: FloatLabelType = 'auto';
	@Input() type: string = 'text';
	@Input() patternValidator!: string;
	@Input() required: boolean = false;
	@Input() error!: string;

	@Input() commandPaletteUse!: boolean;
	@Input() usePredictiveText!: boolean;

	predictiveText = this.commandPaletteInputService.predictiveText;
	isParamDefined$ = this.commandPaletteInputService.isParamDefined$;

	helperDialogRef: MatDialogRef<HelperdialogComponent> | undefined;

	@Output('update') updateInput: EventEmitter<{
		input: string;
	}> = new EventEmitter<{ input: string }>();

	@Output('submitInput') submitInput: EventEmitter<{
		input: string;
	}> = new EventEmitter<{ input: string }>();

	@Input() data!: T[];

	constructor(
		private dialogModel: MatDialog,
		private commandPaletteInputService: CommandPaletteInputService,
		private commandGroupOptionsService: CommandGroupOptionsService,
		private parameterStringActionService: ParameterStringActionService
	) {}

	_onInput(e: Event) {
		let inputVal = (e.target as HTMLInputElement).value;
		if (inputVal[0] === undefined || inputVal === '') inputVal = '';

		this.commandPaletteUse
			? (this.commandGroupOptionsService.stringToFilterCommandsBy =
					this.formatInputText(inputVal))
			: this.update(inputVal);
	}

	formatInputText(input: string) {
		return input
			.split(' ')
			.map((val) => {
				if (!val[0]) return '';
				return val[0]?.toUpperCase() + val?.slice(1).toLowerCase();
			})
			.join(' ');
	}

	update(value: string) {
		this.input = value;
		this.updateInput.emit({
			input: this.input,
		});
	}

	clearInput() {
		this.commandPaletteUse
			? (this.commandGroupOptionsService.stringToFilterCommandsBy = '')
			: this.update('');
		this.parameterStringActionService.fromHistory.next(false);
	}

	_onSubmit(form: NgForm) {
		if (
			this.input === null ||
			this.input[0] === undefined ||
			this.input === ''
		)
			return;
		if (form.status === 'INVALID') return;
		if (this.type === 'url') {
			if (!this.isValidUrl(`https://${this.input.trim()}`)) {
				return;
			}
		}
		this.submitInput.emit({
			input: this.input,
		});
	}

	isValidUrl(url: string) {
		try {
			new URL(url);
			return true;
		} catch (err) {
			return false;
		}
	}

	_dialog(e?: Event) {
		e?.stopPropagation();
		this.dialogModel
			.open(HelperdialogComponent)
			.afterClosed()
			.pipe(tap(() => this.clearInput()))
			.subscribe();
	}

	_onCommandObjSelected(commandObj: Command) {
		if (commandObj.name === 'Help') {
			this._dialog();
		}

		this.commandPaletteUse
			? (this.commandGroupOptionsService.stringToFilterCommandsBy =
					commandObj.name)
			: this.update(commandObj.name);
	}
}
