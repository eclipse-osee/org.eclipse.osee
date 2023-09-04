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
import { map } from 'rxjs/operators';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';

@Component({
	selector: 'osee-parameter-boolean',
	templateUrl: './parameter-boolean.component.html',
	styles: [],
	standalone: true,
})
export class ParameterBooleanComponent {
	parameter$ = this.parameterDataService.parameter$;
	paramString = '';
	userPrompt$ = this.parameter$.pipe(
		map((param) => param?.attributes.description)
	);

	constructor(private parameterDataService: ParameterDataService) {}
}
