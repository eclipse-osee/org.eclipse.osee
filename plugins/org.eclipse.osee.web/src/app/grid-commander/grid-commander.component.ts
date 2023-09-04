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
import { Component, OnDestroy } from '@angular/core';
import { CommandGroupOptionsService } from './services/data-services/commands/command-group-options.service';
import { SelectedCommandDataService } from './services/data-services/selected-command-data/selected-command-data.service';
import { DataTableService } from './services/datatable-services/datatable.service';
import { GcDatatableComponent } from './gc-datatable/gc-datatable.component';
import { CreateCommandFormComponent } from './create-form/create-command-form/create-command-form.component';
import { NgIf, AsyncPipe } from '@angular/common';
import { CommandPaletteComponent } from './command-palette/command-palette/command-palette.component';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
	selector: 'osee-grid-commander',
	templateUrl: './grid-commander.component.html',
	styles: [],
	providers: [],
	standalone: true,
	imports: [
		CommandPaletteComponent,
		NgIf,
		CreateCommandFormComponent,
		GcDatatableComponent,
		AsyncPipe,
		MatDialogModule,
	],
})
export class GridCommanderComponent implements OnDestroy {
	tableData = this.dataTableService.displayedTableData;
	displayCreateArtifactForm =
		this.selectedCommandDataService.displayCreateNewCommandform;

	constructor(
		private dataTableService: DataTableService,
		private selectedCommandDataService: SelectedCommandDataService
	) {}

	ngOnDestroy(): void {
		this.dataTableService.doneFx = '';
	}
}
export default GridCommanderComponent;
