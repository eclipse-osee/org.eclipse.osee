/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, inject } from '@angular/core';
import { DiffTableComponent } from './diff-table/diff-table.component';
import { SearchOptions } from './model/types';
import { SearchCriteriaComponent } from './search-criteria/search-criteria.component';
import { ReportService } from './services/report.service';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-diff-report',
	imports: [AsyncPipe, SearchCriteriaComponent, DiffTableComponent],
	templateUrl: './diff-report.component.html',
})
export class DiffReportComponent {
	private reportService = inject(ReportService);

	displayTable = this.reportService.displayTable;

	startGeneratingReport(searchOptions: SearchOptions) {
		this.reportService.DisplayTable = searchOptions.displaySearch;
	}
}

export default DiffReportComponent;
