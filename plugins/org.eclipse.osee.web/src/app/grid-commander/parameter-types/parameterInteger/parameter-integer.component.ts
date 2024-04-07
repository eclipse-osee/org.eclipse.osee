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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { map } from 'rxjs/operators';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';

@Component({
	selector: 'osee-parameter-integer',
	templateUrl: './parameter-integer.component.html',
	styles: [],
	standalone: true,
	imports: [NgIf, MatFormField, MatLabel, MatInput, FormsModule, AsyncPipe],
})
export class ParameterIntegerComponent {
	value = '';
	parameter$ = this.parameterDataService.parameter$;
	paramString = '';
	userPrompt$ = this.parameter$.pipe(
		map((param) => param?.attributes.description)
	);

	constructor(private parameterDataService: ParameterDataService) {}
}
