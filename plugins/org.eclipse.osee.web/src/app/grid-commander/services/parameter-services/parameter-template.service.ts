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
import { BehaviorSubject, filter, map } from 'rxjs';
import { CommandGroupOptionsService } from '../data-services/command-group-options.service';

@Injectable({
	providedIn: 'root',
})
export class ParameterTemplateService {
	//create action stream to make user input reactive
	private _parameterStringInput = new BehaviorSubject<string>('');

	selectedCommand$ = this.commandGroupOptService.selectedCommandObject;
	parameter$ = this.commandGroupOptService.commandsParameter;
	parameterAttributes$ = this.parameter$.pipe(
		filter((param) => param.attributes !== undefined),
		map((param) => param.attributes)
	);

	userPrompt$ = this.parameterAttributes$.pipe(
		filter((attributes) => attributes.description !== undefined),
		map((attributes) => attributes.description ?? '')
	);

	patternValidator$ = this.parameterAttributes$.pipe(
		filter((attributes) => attributes['validator type'] !== undefined),
		map(
			(attributes) =>
				attributes['validator type']
					?.split(';')
					.filter((el) => el.includes('pattern'))[0]
					?.split(': ')[1] ?? ''
		)
	);

	inputPlaceholder$ = this.parameterAttributes$.pipe(
		filter((attributes) => attributes['default value'] !== undefined),
		map((attributes) => attributes['default value'] ?? '')
	);

	isValidatorUsed$ = this.parameterAttributes$.pipe(
		filter((attributes) => attributes['is validator used'] !== undefined),
		map((attributes) =>
			attributes['is validator used'] === true ? true : false
		)
	);

	constructor(private commandGroupOptService: CommandGroupOptionsService) {}

	//updates our subject on user input
	updateParameterStringInput(parameterString: string): void {
		this._parameterStringInput.next(parameterString);
	}

	public get parameterStringInput() {
		return this._parameterStringInput.asObservable();
	}
}
