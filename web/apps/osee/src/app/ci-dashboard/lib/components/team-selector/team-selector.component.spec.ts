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
import { TeamSelectorComponent } from './team-selector.component';
import { DashboardHttpService } from '../../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../../services/dashboard-http.service.mock';

import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { defReferenceMock } from '../../testing/tmo.response.mock';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('TeamSelectorComponent', () => {
	let component: TeamSelectorComponent;
	let fixture: ComponentFixture<TeamSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TeamSelectorComponent],
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				provideNoopAnimations(),
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TeamSelectorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('script', defReferenceMock[0]);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
