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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { NamedIdListEditorComponent } from '@osee/shared/components';
import {
	MessagingControlsMockComponent,
	messageTypesServiceMock,
	ratesServiceMock,
} from '@osee/messaging/shared/testing';

import { CurrentMessagePeriodicityService } from '@osee/messaging/message-periodicity/services';
import { CurrentMessagePeriodicitiesServiceMock } from '@osee/messaging/message-periodicity/services/testing';
import { MessageTypesService } from '@osee/messaging/message-type/services';
import { RatesService } from '@osee/messaging/rate/services';
import { CurrentStructureCategoriesService } from '@osee/messaging/structure-category/services';
import { CurrentStructureCategoriesServiceMock } from '@osee/messaging/structure-category/services/testing';
import { UnitsService } from '@osee/messaging/units/services';
import { unitsServiceMock } from '@osee/messaging/units/services/testing';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { ListConfigurationComponent } from './list-configuration.component';
import { MockCurrentViewSelectorComponent } from '@osee/shared/components/testing';

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
					{
						provide: CurrentStructureCategoriesService,
						useValue: CurrentStructureCategoriesServiceMock,
					},
				],
				imports: [
					MockCurrentViewSelectorComponent,
					MessagingControlsMockComponent,
					NamedIdListEditorComponent,
					AsyncPipe,
					NgIf,
				],
			},
		})
			.configureTestingModule({
				imports: [ListConfigurationComponent],
				providers: [
					provideNoopAnimations(),
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
					{
						provide: CurrentStructureCategoriesService,
						useValue: CurrentStructureCategoriesServiceMock,
					},
					{
						provide: CurrentMessagePeriodicityService,
						useValue: CurrentMessagePeriodicitiesServiceMock,
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
