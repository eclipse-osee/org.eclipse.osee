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
import { combineLatest, switchMap, iif, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ExecutedCommandsService } from '../../services/data-services/executed-commands.service';
import { ParameterStringActionService } from '../../services/parameter-services/parameter-string-action.service';
import { ParameterTemplateService } from '../../services/parameter-services/parameter-template.service';

@Component({
	selector: 'osee-parameter-string',
	templateUrl: './parameter-string.component.html',
	styleUrls: ['./parameter-string.component.sass'],
})
export class ParameterStringComponent implements OnDestroy {
	parameter$ = this.paramTemplateService.parameter$;
	userPrompt$ = this.paramTemplateService.userPrompt$;
	inputPlaceholder$ = this.paramTemplateService.inputPlaceholder$;
	isValidatorUsed$ = this.paramTemplateService.isValidatorUsed$;
	patternValidator$ = this.paramTemplateService.patternValidator$;

	displayInParamStringComponent$ = combineLatest([
		this.parameter$,
		this.userPrompt$,
		this.inputPlaceholder$,
		this.isValidatorUsed$,
		this.patternValidator$,
	]).pipe(
		map(
			([
				parameter,
				prompt,
				placeholder,
				validatorUsed,
				patternValidator,
			]) => ({
				parameter,
				prompt,
				placeholder,
				validatorUsed,
				patternValidator,
			})
		)
	);

	parameterValueFromHistory = combineLatest([
		this.parameterStringActionService.selectedExecutedCommandObject,
		this.parameterStringActionService.fromHistory,
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
		private parameterStringActionService: ParameterStringActionService,
		private paramTemplateService: ParameterTemplateService,
		private executedCommandService: ExecutedCommandsService
	) {}

	ngOnDestroy(): void {
		this.executedCommandService.doneFx = '';
	}

	//Other checks if the command is from the history?
	_onInput(e: { input: string }) {
		let inputVal = e.input;
		if (inputVal[0] === undefined || inputVal === '') {
			this.parameterStringActionService.fromHistory.next(false);
			this.parameterStringActionService.selectedCommandId = '';
			return;
		}
	}

	_onSubmit(e: { input: string }) {
		const url = new URL(e.input.trim(), `https://${e.input.trim()}`);
		this.paramTemplateService.updateParameterStringInput(url.host);
		this.executedCommandService.updateCommand.subscribe();
		window.open(url.origin, '_blank');
	}
}
