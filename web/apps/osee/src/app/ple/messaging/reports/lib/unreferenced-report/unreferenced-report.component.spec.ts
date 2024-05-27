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
import { UnreferencedReportComponent } from './unreferenced-report.component';
import {
	MimPreferencesService,
	UnreferencedService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	MimPreferencesServiceMock,
	unreferencedServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('UnreferencedReportComponent', () => {
	let component: UnreferencedReportComponent;
	let fixture: ComponentFixture<UnreferencedReportComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(UnreferencedReportComponent, {
			set: {
				providers: [
					{
						provide: UnreferencedService,
						useValue: unreferencedServiceMock,
					},
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
			},
		})
			.configureTestingModule({
				imports: [UnreferencedReportComponent],
				providers: [
					provideRouter([]),
					provideNoopAnimations(),
					{
						provide: UnreferencedService,
						useValue: unreferencedServiceMock,
					},
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(UnreferencedReportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
