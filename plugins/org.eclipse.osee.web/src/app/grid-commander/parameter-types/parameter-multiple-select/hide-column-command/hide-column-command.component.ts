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
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SelectedCommandDataService } from '../../../services/data-services/selected-command-data/selected-command-data.service';
import { DataTableService } from '../../../services/datatable-services/datatable.service';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NgIf, NgFor, AsyncPipe, TitleCasePipe } from '@angular/common';

@Component({
	selector: 'osee-hide-column-command',
	templateUrl: './hide-column-command.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
		MatFormFieldModule,
		MatSelectModule,
		FormsModule,
		MatChipsModule,
		NgFor,
		MatIconModule,
		MatOptionModule,
		AsyncPipe,
		TitleCasePipe,
	],
})
export class HideColumnCommandComponent {
	columnOptions = this.dataTableService.columnOptions;
	hideColumnsControl = this.dataTableService.hiddenColumns;

	commandDescription$ =
		this.selectedCommandDataService.selectedCommandObject.pipe(
			switchMap((commandObj) =>
				of(commandObj).pipe(
					map((command) => command.attributes.description)
				)
			)
		);

	constructor(
		private dataTableService: DataTableService,
		private selectedCommandDataService: SelectedCommandDataService
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
