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
import { TeamsListComponent } from './teams-list.component';
import { DashboardHttpService } from '../../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../../services/dashboard-http.service.mock';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';

describe('TeamsListComponent', () => {
	let component: TeamsListComponent;
	let fixture: ComponentFixture<TeamsListComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TeamsListComponent],
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
		}).compileComponents();

		fixture = TestBed.createComponent(TeamsListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
