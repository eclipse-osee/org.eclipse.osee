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
import { MatSelectChange } from '@angular/material/select';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CommandGroupOptionsService } from '../../../services/data-services/command-group-options.service';
import { DataTableService } from '../../../services/datatable-services/datatable.service';

@Component({
	selector: 'osee-hide-column-command',
	templateUrl: './hide-column-command.component.html',
	styleUrls: ['./hide-column-command.component.sass'],
})
export class HideColumnCommandComponent {
	columnOptions = this.dataTableService.columnOptions;
	hideColumnsControl = this.dataTableService.hiddenColumns;

	commandDescription$ =
		this.commandGroupOptService.selectedCommandObject.pipe(
			switchMap((commandObj) =>
				of(commandObj).pipe(
					map((command) => command.attributes.description)
				)
			)
		);

	constructor(
		private dataTableService: DataTableService,
		private commandGroupOptService: CommandGroupOptionsService
	) {}

	onSelectColToHide(event: MatSelectChange) {
		event.value.length === 0
			? this.dataTableService.updateHiddenColumns([])
			: this.dataTableService.updateHiddenColumns(event.value);
	}

	unhideCol(col: string) {
		this.dataTableService.updateHiddenColumns(
			this.hideColumnsControl.value.filter((val) => val !== col)
		);
	}
}
