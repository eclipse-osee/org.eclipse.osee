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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { TimelineChartComponent } from './timeline-chart/timeline-chart.component';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../services/dashboard.service';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
	selector: 'osee-timelines',
	template: `<osee-ci-dashboard-controls />

		@if (branchId() && branchType()) {
			<div class="tw-px-4 tw-pb-4">
				@if (timelines(); as _timelines) {
					@if (_timelines.length === 0) {
						No data available for the selected branch and CI Set
					}
					@if (_timelines.length > 0) {
						<div
							class="tw-mb-2 tw-flex tw-items-center tw-justify-between">
							<h3 class="tw-font-bold">
								Test Scripts Pass/Fail/Aborted
							</h3>
							<mat-slide-toggle
								[checked]="showAbort()"
								(change)="showAbort.set($event.checked)">
								Show Aborted
							</mat-slide-toggle>
						</div>
						<p>Last updated {{ _timelines[0].updatedAt }}</p>
						<div class="tw-flex tw-flex-wrap tw-gap-8">
							@for (timeline of _timelines; track timeline.team) {
								<osee-timeline-chart
									class="tw-h-52 tw-w-full tw-pb-12"
									[timeline]="timeline"
									[showAbort]="showAbort()">
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
		CiDashboardControlsComponent,
		TimelineChartComponent,
		MatSlideToggleModule,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class TimelinesComponent {
	private uiService = inject(CiDashboardUiService);
	private dashboardService = inject(DashboardService);

	timelines = toSignal(this.dashboardService.timelines);
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);

	showAbort = signal(true);
}
