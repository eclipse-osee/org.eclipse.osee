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
import { AsyncPipe, DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MessagingDiffsComponent } from './messaging-diffs/messaging-diffs.component';
import { StructureDiffsComponent } from './structure-diffs/structure-diffs.component';
import { ObjectValuesPipe } from '@osee/shared/utils';
import {
	DiffReportService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type {
	branchSummary,
	diffReportSummaryItem,
} from '@osee/messaging/shared/types';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-messaging-diff-report',
	templateUrl: './diff-report.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [
		AsyncPipe,
		DatePipe,
		ObjectValuesPipe,
		MatTableModule,
		ScrollToTopButtonComponent,
		MessagingDiffsComponent,
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

	differenceReport =
		this.diffReportService.diffReport.pipe(takeUntilDestroyed());

	isDifference = this.differenceReport.pipe(
		map((report) => {
			return (
				Object.keys(report.nodes).length > 0 ||
				Object.keys(report.connections).length > 0 ||
				Object.keys(report.messages).length > 0 ||
				Object.keys(report.subMessages).length > 0 ||
				Object.keys(report.structures).length > 0
			);
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
