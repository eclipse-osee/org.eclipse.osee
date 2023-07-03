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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RatesService } from '@osee/messaging/shared/services';
import { ratesServiceMock } from '@osee/messaging/shared/testing';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';

import { RateDropdownComponent } from './rate-dropdown.component';

describe('RateDropdownComponent', () => {
	let component: RateDropdownComponent;
	let fixture: ComponentFixture<RateDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(RateDropdownComponent, {
			set: {
				providers: [
					{
						provide: RatesService,
						useValue: ratesServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
				],
				viewProviders: [],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule, RateDropdownComponent],
				providers: [
					{
						provide: RatesService,
						useValue: ratesServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(RateDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
