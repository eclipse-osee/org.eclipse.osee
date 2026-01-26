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
	computed,
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
				@if (timelinesView(); as _timelines) {
					@if (_timelines.length === 0) {
						No data available for the selected branch{{
							compareMode() ? '' : ' and CI Set'
						}}
					}
					@if (_timelines.length > 0) {
						<div
							class="tw-mb-2 tw-flex tw-items-center tw-justify-between">
							<h3 class="tw-font-bold">
								{{
									testPointsMode()
										? 'Test Points Pass/Fail'
										: 'Test Scripts Pass/Fail/Aborted'
								}}
							</h3>
							<div class="tw-flex tw-items-center tw-gap-4">
								<mat-slide-toggle
									[checked]="compareMode()"
									(change)="compareMode.set($event.checked)">
									Compare Across Sets
								</mat-slide-toggle>
								<mat-slide-toggle
									[checked]="testPointsMode()"
									(change)="
										testPointsMode.set($event.checked)
									">
									Show Test Points
								</mat-slide-toggle>
								<mat-slide-toggle
									[checked]="showAbort()"
									(change)="showAbort.set($event.checked)">
									Show Aborted
								</mat-slide-toggle>
							</div>
						</div>
						<p>Last updated {{ _timelines[0].updatedAt }}</p>
						<div class="tw-flex tw-flex-wrap tw-gap-8">
							@for (timeline of _timelines; track timeline.team) {
								<osee-timeline-chart
									class="tw-h-52 tw-w-full tw-pb-12"
									[timeline]="timeline"
									[showAbort]="showAbort()"
									[testPointsMode]="testPointsMode()">
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
	timelineCompare = toSignal(this.dashboardService.timelineCompare);
	branchId = toSignal(this.uiService.branchId);
	branchType = toSignal(this.uiService.branchType);

	showAbort = signal(true);
	compareMode = signal(false);
	testPointsMode = signal(false);

	timelinesView = computed(() => {
		const inCompare = this.compareMode();
		const compare = this.timelineCompare();
		const normal = this.timelines();
		return inCompare ? (compare ?? []) : (normal ?? []);
	});
}
