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
import { map } from 'rxjs';
import { CommandGroupOptionsService } from '../../services/data-services/command-group-options.service';
import { DataTableService } from '../../services/datatable-services/datatable.service';

@Component({
	selector: 'osee-parameter-single-select',
	templateUrl: './parameter-single-select.component.html',
	styleUrls: ['./parameter-single-select.component.sass'],
})
export class ParameterSingleSelectComponent {
	//TODO: Determine how to dynamically render options in template based on command -- paramater attribute?
	colOptions = this.dataTableService.displayedCols;

	parameter$ = this.commandGroupOptService.commandsParameter;
	paramString = '';
	userPrompt$ = this.parameter$.pipe(
		map((param) => param?.attributes.description)
	);

	constructor(
		private dataTableService: DataTableService,
		private commandGroupOptService: CommandGroupOptionsService
	) {}
}
