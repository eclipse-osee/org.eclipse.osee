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
import { Component, inject } from '@angular/core';
import { ParameterDataService } from '../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { ParameterMultipleSelectComponent } from './parameter-multiple-select/parameter-multiple-select.component';
import { ParameterStringComponent } from './parameterString/parameter-string.component';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-parameter-types',
	templateUrl: './parameter-types.component.html',
	styles: [],
	imports: [
		ParameterStringComponent,
		ParameterMultipleSelectComponent,
		AsyncPipe,
	],
})
export class ParameterTypesComponent {
	private parameterDataService = inject(ParameterDataService);

	parameter$ = this.parameterDataService.parameter$;
}
