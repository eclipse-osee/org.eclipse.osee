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
import { AsyncPipe, TitleCasePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatChipListbox,
	MatChipOption,
	MatChipRemove,
} from '@angular/material/chips';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import {
	MatSelect,
	MatSelectChange,
	MatSelectTrigger,
} from '@angular/material/select';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SelectedCommandDataService } from '../../../services/data-services/selected-command-data/selected-command-data.service';
import { DataTableService } from '../../../services/datatable-services/datatable.service';

@Component({
	selector: 'osee-hide-column-command',
	templateUrl: './hide-column-command.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		TitleCasePipe,
		MatFormField,
		MatLabel,
		MatSelect,
		MatSelectTrigger,
		MatChipListbox,
		MatChipOption,
		MatIcon,
		MatChipRemove,
		MatOption,
	],
})
export class HideColumnCommandComponent {
	private dataTableService = inject(DataTableService);
	private selectedCommandDataService = inject(SelectedCommandDataService);

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

	onSelectColToHide(event: MatSelectChange) {
		if (event.value.length === 0) {
			this.dataTableService.updateHiddenColumns([]);
		} else {
			this.dataTableService.updateHiddenColumns(event.value);
		}
	}

	unhideCol(col: string) {
		this.dataTableService.updateHiddenColumns(
			this.hideColumnsControl.value.filter((val) => val !== col)
		);
	}
}
