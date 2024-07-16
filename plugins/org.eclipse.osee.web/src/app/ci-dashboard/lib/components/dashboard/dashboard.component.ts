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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDivider } from '@angular/material/divider';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../services/dashboard.service';
import { ScriptsPassFailChartComponent } from '../charts/scripts-pass-fail-chart/scripts-pass-fail-chart.component';
import { ScriptsRanChartComponent } from '../charts/scripts-ran-chart/scripts-ran-chart.component';
import { TestPointsPassFailChartComponent } from '../charts/test-points-pass-fail-chart/test-points-pass-fail-chart.component';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';

@Component({
	selector: 'osee-dashboard',
	standalone: true,
	imports: [
		AsyncPipe,
		NgIf,
		NgFor,
		ScriptsPassFailChartComponent,
		ScriptsRanChartComponent,
		TestPointsPassFailChartComponent,
		CiDashboardControlsComponent,
		MatDivider,
	],
	templateUrl: './dashboard.component.html',
})
export default class DashboardComponent {
	constructor(
		private dashboardService: DashboardService,
		private uiService: CiDashboardUiService
	) {}

	teamStats = this.dashboardService.teamStats;
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}
