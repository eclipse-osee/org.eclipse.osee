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
import { AsyncPipe } from '@angular/common';
import { Component, OnDestroy, inject } from '@angular/core';
import { CommandPaletteComponent } from './command-palette/command-palette/command-palette.component';
import { CreateCommandFormComponent } from './create-form/create-command-form/create-command-form.component';
import { GcDatatableComponent } from './gc-datatable/gc-datatable.component';
import { SelectedCommandDataService } from './services/data-services/selected-command-data/selected-command-data.service';
import { DataTableService } from './services/datatable-services/datatable.service';

@Component({
	selector: 'osee-grid-commander',
	templateUrl: './grid-commander.component.html',
	styles: [],
	providers: [],
	imports: [
		CommandPaletteComponent,
		CreateCommandFormComponent,
		GcDatatableComponent,
		AsyncPipe,
	],
})
export class GridCommanderComponent implements OnDestroy {
	private dataTableService = inject(DataTableService);
	private selectedCommandDataService = inject(SelectedCommandDataService);

	tableData = this.dataTableService.displayedTableData;
	displayCreateArtifactForm =
		this.selectedCommandDataService.displayCreateNewCommandform;

	ngOnDestroy(): void {
		this.dataTableService.doneFx = '';
	}
}
export default GridCommanderComponent;
