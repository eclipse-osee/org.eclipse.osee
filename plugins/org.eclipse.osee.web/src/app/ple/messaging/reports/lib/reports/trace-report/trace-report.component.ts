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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportsService } from '@osee/messaging/shared/services';
import { ActivatedRoute } from '@angular/router';
import { UiService } from '@osee/shared/services';
import { TraceReportTableComponent } from '../../tables/trace-report-table/trace-report-table.component';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
	selector: 'osee-trace-report',
	standalone: true,
	imports: [
		CommonModule,
		MatButtonModule,
		MatButtonToggleModule,
		MatExpansionModule,
		TraceReportTableComponent,
	],
	templateUrl: './trace-report.component.html',
})
export class NodeTraceReportRequirementsComponent {
	constructor(
		private route: ActivatedRoute,
		private ui: UiService,
		private reportsService: ReportsService
	) {
		this.route.paramMap.subscribe((params) => {
			this.ui.idValue = params.get('branchId') || '';
			this.ui.typeValue = params.get('branchType') || '';
		});
	}

	requirementsReport = this.reportsService.nodeTraceReportRequirements;

	missingRequirementsReport =
		this.reportsService.nodeTraceReportNoMatchingArtifacts;
	artifactsReport = this.reportsService.nodeTraceReportInterfaceArtifacts;

	missingInterfaceArtifactsReport =
		this.reportsService.nodeTraceReportNoMatchingInterfaceArtifacts;
}

export default NodeTraceReportRequirementsComponent;
