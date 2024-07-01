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
import TimelinesComponent from './timelines.component';
import { DashboardHttpService } from '../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../services/dashboard-http.service.mock';
import { provideRouter } from '@angular/router';
import { CiDashboardControlsMockComponent } from '../../testing/ci-dashboard-controls.component.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TimelinesComponent', () => {
	let component: TimelinesComponent;
	let fixture: ComponentFixture<TimelinesComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CiDashboardControlsMockComponent,
				HttpClientTestingModule,
				TimelinesComponent,
			],
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
				provideRouter([]),
				provideNoopAnimations(),
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TimelinesComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
