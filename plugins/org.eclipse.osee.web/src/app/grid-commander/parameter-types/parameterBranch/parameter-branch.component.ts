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
import { ActionDropDownComponent } from '../../../shared/components/action-state-button/action-drop-down/action-drop-down.component';
import { BranchPickerComponent } from '../../../shared/components/branch-picker/branch-picker/branch-picker.component';

@Component({
	selector: 'osee-parameter-branch',
	templateUrl: './parameter-branch.component.html',
	styleUrls: ['./parameter-branch.component.sass'],
	standalone: true,
	imports: [BranchPickerComponent, ActionDropDownComponent],
})
export class ParameterBranchComponent {
	parameter$ = this.parameterDataService.parameter$;
	paramString = '';
	userPrompt$ = this.parameter$.pipe(
		map((param) => param?.attributes.description)
	);

	constructor(private parameterDataService: ParameterDataService) {}
}
