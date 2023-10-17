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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import SubsystemsComponent from './subsystems.component';
import { NgIf } from '@angular/common';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { DashboardHttpService } from '../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../services/dashboard-http.service.mock';

describe('SubsystemsComponent', () => {
	let component: SubsystemsComponent;
	let fixture: ComponentFixture<SubsystemsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(SubsystemsComponent, {
			set: {
				imports: [NgIf, CiDashboardControlsMockComponent],
			},
		}).configureTestingModule({
			imports: [SubsystemsComponent],
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(SubsystemsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
