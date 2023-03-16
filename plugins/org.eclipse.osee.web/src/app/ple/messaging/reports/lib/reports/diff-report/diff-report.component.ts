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
import { ScrollToTopButtonComponent } from '@osee/shared/components';
import { AsyncPipe, DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { NodeDiffsComponent } from '../../diffs/node-diffs/node-diffs.component';
import { ConnectionDiffsComponent } from '../../diffs/connection-diffs/connection-diffs.component';
import { MessageDiffsComponent } from '../../diffs/message-diffs/message-diffs.component';
import { SubmessageDiffsComponent } from '../../diffs/submessage-diffs/submessage-diffs.component';
import { StructureDiffsComponent } from '../../diffs/structure-diffs/structure-diffs.component';
import {
	DiffReportService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type {
	branchSummary,
	diffReportSummaryItem,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-diff-report',
	templateUrl: './diff-report.component.html',
	styleUrls: ['./diff-report.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		NgFor,
		NgClass,
		DatePipe,
		MatTableModule,
		ScrollToTopButtonComponent,
		NodeDiffsComponent,
		ConnectionDiffsComponent,
		MessageDiffsComponent,
		SubmessageDiffsComponent,
		StructureDiffsComponent,
	],
})
export class DiffReportComponent {
	constructor(
		private diffReportService: DiffReportService,
		private headerService: HeaderService
	) {}

	date = new Date();

	branchSummaryKey = 'branchSummary';
	branchSummaryHeaders: (keyof branchSummary)[] = [
		'pcrNo',
		'description',
		'compareBranch',
		'reportDate',
	];

	reportSummaryKey = 'diffReportSummary';
	reportSummaryHeaders: (keyof diffReportSummaryItem)[] = [
		'changeType',
		'action',
		'name',
		'details',
	];

	branchInfo = this.diffReportService.branchInfo;
	parentBranchInfo = this.diffReportService.parentBranchInfo;
	branchSummary = this.diffReportService.branchSummary;
	diffReportSummary = this.diffReportService.diffReportSummary;
	differenceReport = this.diffReportService.diffReport;
	nodes = this.diffReportService.nodes;

	isDifference = this.differenceReport.pipe(
		map((report) => {
			return Object.keys(report.changeItems).length !== 0;
		})
	);

	getHeaderByName(
		value: keyof branchSummary | keyof diffReportSummaryItem,
		key: string
	) {
		return this.headerService.getHeaderByName(value, key);
	}

	scrollTo(id: string) {
		document.getElementById(id)?.scrollIntoView({
			behavior: 'smooth',
			block: 'start',
		});
	}
}

export default DiffReportComponent;
