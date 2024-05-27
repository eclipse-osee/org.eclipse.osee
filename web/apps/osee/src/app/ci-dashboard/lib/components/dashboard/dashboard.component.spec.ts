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
import DashboardComponent from './dashboard.component';
import { DashboardHttpService } from '../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../services/dashboard-http.service.mock';
import { NgIf } from '@angular/common';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('DashboardComponent', () => {
	let component: DashboardComponent;
	let fixture: ComponentFixture<DashboardComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(DashboardComponent, {
			set: {
				imports: [NgIf, CiDashboardControlsMockComponent],
			},
		}).configureTestingModule({
			imports: [DashboardComponent],
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(DashboardComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
