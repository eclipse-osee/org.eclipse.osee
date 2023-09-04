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
import { Component, OnDestroy } from '@angular/core';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { ExecutedCommandsService } from '../../services/data-services/execution-services/executed-commands.service';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { CommandFromUserHistoryService } from '../../services/data-services/selected-command-data/command-from-history/command-from-user-history.service';
import { SelectedCommandDataService } from '../../services/data-services/selected-command-data/selected-command-data.service';
import { InputControlComponent } from '../../shared/input-control/input-control.component';
import { NgIf, AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-parameter-string',
	templateUrl: './parameter-string.component.html',
	styles: [],
	standalone: true,
	imports: [NgIf, InputControlComponent, AsyncPipe],
})
export class ParameterStringComponent implements OnDestroy {
	isCustomCommand$ = this.selectedCommandDataService.isCustomCommand;
	parameter$ = this.parameterDataService.parameter$;
	userPrompt$ = this.parameterDataService.userPrompt$;
	inputPlaceholder$ = this.parameterDataService.inputPlaceholder$;
	isValidatorUsed$ = this.parameterDataService.isValidatorUsed$;
	patternValidator$ = this.parameterDataService.patternValidator$;

	displayInParamStringComponent$ = combineLatest([
		this.parameter$,
		this.userPrompt$,
		this.inputPlaceholder$,
		this.isValidatorUsed$,
		this.patternValidator$,
		this.isCustomCommand$,
	]).pipe(
		map(
			([
				parameter,
				prompt,
				placeholder,
				validatorUsed,
				patternValidator,
				isCustomCommand,
			]) => ({
				parameter,
				prompt,
				placeholder,
				validatorUsed,
				patternValidator,
				isCustomCommand,
			})
		)
	);

	parameterValueFromHistory =
		this.commandFromUserHistoryService.parameterValueFromHistory$;

	constructor(
		private commandFromUserHistoryService: CommandFromUserHistoryService,
		private parameterDataService: ParameterDataService,
		private selectedCommandDataService: SelectedCommandDataService,
		private executedCommandsService: ExecutedCommandsService
	) {}

	ngOnDestroy(): void {
		this.executedCommandsService.doneFx = '';
	}

	_onInput(e: { input: string }) {
		let inputVal = e.input;
		if (inputVal[0] === undefined || inputVal === '') {
			this.commandFromUserHistoryService.fromHistory = false;
			this.commandFromUserHistoryService.selectedCommandFromHistoryTableId =
				'';
			return;
		}
	}

	_onSubmit(e: { input: string; type: string }) {
		if (e.type === 'url') {
			const url: URL = new URL(
				e.input.trim(),
				`https://${e.input.trim()}`
			);
			this.parameterDataService.updateParameterStringInput(url.host);
			this.executedCommandsService.updateCommand.subscribe();
			window.open(url.origin, '_blank');
		}
	}
}
