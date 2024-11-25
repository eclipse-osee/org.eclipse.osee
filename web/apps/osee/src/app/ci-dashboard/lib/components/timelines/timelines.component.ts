/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { TimelineChartComponent } from './timeline-chart/timeline-chart.component';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../services/dashboard.service';

@Component({
	selector: 'osee-timelines',
	template: `<osee-ci-dashboard-controls />

		@if (branchId() && branchType()) {
			<div class="tw-px-4 tw-pb-4">
				@if (timelineStats | async; as timelines) {
					@if (timelines.length === 0) {
						No data available for the selected branch and CI Set
					}
					@if (timelines.length > 0) {
						<h2 class="tw-font-bold">Test Scripts Pass/Fail</h2>
						<div class="tw-flex tw-flex-wrap tw-gap-8">
							@for (timeline of timelines; track timeline) {
								<osee-timeline-chart
									class="tw-h-52 tw-w-full tw-pb-12"
									[timelineData]="timeline">
								</osee-timeline-chart>
							}
						</div>
					}
				} @else {
					Loading...
				}
			</div>
		}`,
	imports: [
		AsyncPipe,
		NgIf,
		NgFor,
		CiDashboardControlsComponent,
		TimelineChartComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TimelinesComponent {
	uiService = inject(CiDashboardUiService);
	dashboardService = inject(DashboardService);

	timelineStats = this.dashboardService.timelineStats;
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);
}
