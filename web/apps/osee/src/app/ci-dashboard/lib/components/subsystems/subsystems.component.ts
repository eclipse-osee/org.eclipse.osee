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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../services/dashboard.service';
import { ScriptsPassFailChartComponent } from '../charts/scripts-pass-fail-chart/scripts-pass-fail-chart.component';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';

@Component({
	selector: 'osee-subsystems',
	standalone: true,
	imports: [
		AsyncPipe,
		CiDashboardControlsComponent,
		ScriptsPassFailChartComponent,
	],
	templateUrl: './subsystems.component.html',
})
export default class SubsystemsComponent {
	private uiService = inject(CiDashboardUiService);
	private dashboardService = inject(DashboardService);

	subsystemStats = this.dashboardService.subsystemStats;
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}
