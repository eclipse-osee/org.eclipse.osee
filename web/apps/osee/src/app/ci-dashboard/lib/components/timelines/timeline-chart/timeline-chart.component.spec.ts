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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TimelineChartComponent } from './timeline-chart.component';
import { DashboardHttpService } from '../../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../../services/dashboard-http.service.mock';
import { timelineStatsMock } from '../../../testing/dashboard.response.mock';

describe('TimelineChartComponent', () => {
	let component: TimelineChartComponent;
	let fixture: ComponentFixture<TimelineChartComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TimelineChartComponent],
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TimelineChartComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('timeline', timelineStatsMock[0]);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
