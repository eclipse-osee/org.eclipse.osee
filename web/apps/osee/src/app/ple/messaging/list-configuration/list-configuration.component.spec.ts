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
import { AsyncPipe, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { NamedIdListEditorComponent } from '@osee/messaging/shared/main-content';
import {
	UnitsService,
	RatesService,
	MessageTypesService,
} from '@osee/messaging/shared/services';
import {
	unitsServiceMock,
	ratesServiceMock,
	messageTypesServiceMock,
	MessagingControlsMockComponent,
	ViewSelectorMockComponent,
} from '@osee/messaging/shared/testing';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';

import { ListConfigurationComponent } from './list-configuration.component';

describe('ListConfigurationComponent', () => {
	let component: ListConfigurationComponent;
	let fixture: ComponentFixture<ListConfigurationComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ListConfigurationComponent, {
			set: {
				providers: [
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: UnitsService,
						useValue: unitsServiceMock,
					},
					{
						provide: RatesService,
						useValue: ratesServiceMock,
					},
					{
						provide: MessageTypesService,
						useValue: messageTypesServiceMock,
					},
				],
				imports: [
					ViewSelectorMockComponent,
					MessagingControlsMockComponent,
					NamedIdListEditorComponent,
					AsyncPipe,
					NgIf,
				],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule, ListConfigurationComponent],
				providers: [
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
					{
						provide: UnitsService,
						useValue: unitsServiceMock,
					},
					{
						provide: RatesService,
						useValue: ratesServiceMock,
					},
					{
						provide: MessageTypesService,
						useValue: messageTypesServiceMock,
					},
					provideRouter([]),
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ListConfigurationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
