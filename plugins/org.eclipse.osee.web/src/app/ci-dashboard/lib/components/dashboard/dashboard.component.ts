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
import { Component, computed } from '@angular/core';
import { ScriptsPassFailChartComponent } from './scripts-pass-fail-chart/scripts-pass-fail-chart.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { NgFor, NgIf } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { MatDividerModule } from '@angular/material/divider';
import { TestPointsPassFailChartComponent } from './test-points-pass-fail-chart/test-points-pass-fail-chart.component';
import { ScriptsRanChartComponent } from './scripts-ran-chart/scripts-ran-chart.component';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';

@Component({
	selector: 'osee-dashboard',
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		ScriptsPassFailChartComponent,
		ScriptsRanChartComponent,
		TestPointsPassFailChartComponent,
		CiDashboardControlsComponent,
		MatDividerModule,
	],
	templateUrl: './dashboard.component.html',
})
export default class DashboardComponent {
	constructor(
		private dashboardService: DashboardService,
		private uiService: CiDashboardUiService
	) {}

	teamStats = toSignal(this.dashboardService.teamStats);
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}
