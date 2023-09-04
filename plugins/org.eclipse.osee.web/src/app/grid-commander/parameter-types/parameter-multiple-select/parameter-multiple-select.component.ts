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
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { SelectedCommandDataService } from '../../services/data-services/selected-command-data/selected-command-data.service';
import { HideColumnCommandComponent } from './hide-column-command/hide-column-command.component';
import { NgIf, AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-parameter-multiple-select',
	templateUrl: './parameter-multiple-select.component.html',
	styles: [],
	standalone: true,
	imports: [NgIf, HideColumnCommandComponent, AsyncPipe],
})
export class ParameterMultipleSelectComponent {
	command$ = this.selectedCommandDataService.selectedCommandObject;
	parameter$ = this.parameterDataService.parameter$;

	constructor(
		private selectedCommandDataService: SelectedCommandDataService,
		private parameterDataService: ParameterDataService
	) {}
}
