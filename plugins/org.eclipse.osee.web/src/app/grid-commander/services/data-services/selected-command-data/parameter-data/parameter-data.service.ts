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
	BehaviorSubject,
	combineLatest,
	filter,
	iif,
	map,
	of,
	switchMap,
} from 'rxjs';
import { checkIfUndefinedOrNull } from '@osee/shared/utils';
import { Parameter } from '../../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { SelectedCommandDataService } from '../selected-command-data.service';

@Injectable({
	providedIn: 'root',
})
export class ParameterDataService {
	private _parameter$ =
		this.selectedCommandDataService.selectedCommandObject.pipe(
			map((commandObj) => commandObj.parameter),
			filter((parameter): parameter is Parameter =>
				checkIfUndefinedOrNull(parameter)
			)
		);

	private _parameterStringInput = new BehaviorSubject<string>('');

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

	public get parameterDefaultValue$() {
		return this.parameterAttributes$.pipe(
			switchMap((attributes) =>
				iif(
					() => attributes['default value'] !== undefined,
					of(attributes).pipe(
						map(
							(attributes) =>
								attributes['default value']?.split(', ')
						)
					),
					of(null)
				)
			)
		);
	}
	public get isParameterTypeDefined() {
		return this.selectedCommandDataService.selectedCommandObject
			.pipe(map((commandObj) => commandObj.parameter))
			.pipe(
				switchMap((param) =>
					iif(
						() =>
							param?.typeAsString !== null &&
							param?.typeAsString !== '' &&
							param?.id !== '-1',
						of(true),
						of(false)
					)
				)
			);
	}

	constructor(
		private selectedCommandDataService: SelectedCommandDataService
	) {}

	public get parameter$() {
		return this._parameter$;
	}
	public set parameter$(value) {
		this._parameter$ = value;
	}

	updateParameterStringInput(parameterVal: string): void {
		this._parameterStringInput.next(parameterVal);
	}

	public get parameterStringInput() {
		return this._parameterStringInput.asObservable();
	}
}
