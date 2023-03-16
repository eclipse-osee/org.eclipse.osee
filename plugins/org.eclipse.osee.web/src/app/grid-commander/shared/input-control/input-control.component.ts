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
import { NgForm, FormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import {
	FloatLabelType,
	MatFormFieldControl,
	MatFormFieldModule,
} from '@angular/material/form-field';
import { tap } from 'rxjs';
import { HelperdialogComponent } from '../../command-palette/helperdialog/helperdialog.component';
import { CommandPaletteInputService } from '../../services/command-palette-services/command-palette-input.service';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { CommandFromUserHistoryService } from '../../services/data-services/selected-command-data/command-from-history/command-from-user-history.service';
import {
	Command,
	CommandGroups,
} from '../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';

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
	standalone: true,
	imports: [
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatAutocompleteModule,
		NgIf,
		NgFor,
		MatOptionModule,
		HighlightFilteredTextDirective,
		MatButtonModule,
		MatTooltipModule,
		MatIconModule,
		AsyncPipe,
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
	@Input() data!: T[];

	predictiveText = this.commandPaletteInputService.predictiveText;
	isParamDefined$ = this.parameterDataService.isParameterTypeDefined;

	fromHistory = this.commandFromUserHistoryService.fromHistory;

	helperDialogRef: MatDialogRef<HelperdialogComponent> | undefined;

	@Output('autocompleteCommandSelection')
	selectedCommandViaAutocomplete: EventEmitter<{
		selectedCommandObj: Command;
		form: NgForm;
	}> = new EventEmitter<{ selectedCommandObj: Command; form: NgForm }>();
	@Output('update') updateInput: EventEmitter<{
		input: string;
	}> = new EventEmitter<{ input: string }>();

	@Output('submitInput') submitInput: EventEmitter<{
		input: string;
		type: string;
	}> = new EventEmitter<{ input: string; type: string }>();

	constructor(
		private dialogModel: MatDialog,
		private commandPaletteInputService: CommandPaletteInputService,
		private parameterDataService: ParameterDataService,

		private commandFromUserHistoryService: CommandFromUserHistoryService
	) {}

	_onInput(e: Event) {
		let inputVal = (e.target as HTMLInputElement).value;
		if (inputVal[0] === undefined || inputVal === '') inputVal = '';
		this.input = inputVal;

		this.commandPaletteUse
			? this.updateInput.emit({
					input: this.formatInputText(inputVal),
			  })
			: this.updateInput.emit({
					input: inputVal,
			  });
	}

	_onSubmit(form: NgForm) {
		if (
			this.input === null ||
			this.input === undefined ||
			this.input === ''
		)
			return;
		if (form.form.status === 'INVALID') return;

		this.submitInput.emit({
			input: this.input,
			type: this.type,
		});
	}

	_onCommandObjSelected(commandObj: Command, inputForm: NgForm) {
		this.commandPaletteUse
			? this.selectedCommandViaAutocomplete.emit({
					selectedCommandObj: commandObj,
					form: inputForm,
			  })
			: this.updateInput.emit({
					input: commandObj.name,
			  });
	}

	_dialog(e?: Event) {
		e?.stopPropagation();
		this.dialogModel
			.open(HelperdialogComponent)
			.afterClosed()
			.pipe(tap(() => this.clearInput()))
			.subscribe();
	}

	clearInput() {
		this.updateInput.emit({
			input: '',
		});
		this.commandFromUserHistoryService.fromHistory = false;
		this.input = '';
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
}
