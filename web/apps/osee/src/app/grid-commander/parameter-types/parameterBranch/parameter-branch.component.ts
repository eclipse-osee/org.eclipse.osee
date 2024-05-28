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
import { BranchPickerComponent } from '@osee/shared/components';
import { map } from 'rxjs/operators';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';

@Component({
	selector: 'osee-parameter-branch',
	templateUrl: './parameter-branch.component.html',
	styles: [],
	standalone: true,
	imports: [BranchPickerComponent, CurrentActionDropDownComponent],
})
export class ParameterBranchComponent {
	parameter$ = this.parameterDataService.parameter$;
	paramString = '';
	userPrompt$ = this.parameter$.pipe(
		map((param) => param?.attributes.description)
	);

	constructor(private parameterDataService: ParameterDataService) {}
}
