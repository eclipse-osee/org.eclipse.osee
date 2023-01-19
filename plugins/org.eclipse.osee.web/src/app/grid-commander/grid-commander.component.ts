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
import { DataTableService } from './services/datatable-services/datatable.service';

@Component({
	selector: 'osee-grid-commander',
	templateUrl: './grid-commander.component.html',
	styleUrls: ['./grid-commander.component.sass'],
	providers: [],
})
export class GridCommanderComponent implements OnDestroy {
	tableData = this.dataTableService.displayedTableData;
	constructor(private dataTableService: DataTableService) {}

	ngOnDestroy(): void {
		this.dataTableService.doneFx = '';
	}
}
