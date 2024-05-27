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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MessageTypesService } from '@osee/messaging/message-type/services';
import { messageTypesServiceMock } from '@osee/messaging/shared/testing';

import { MessageTypeDropdownComponent } from './message-type-dropdown.component';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('MessageTypeDropdownComponent', () => {
	let component: MessageTypeDropdownComponent;
	let fixture: ComponentFixture<MessageTypeDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessageTypeDropdownComponent, {
			set: {
				providers: [
					{
						provide: MessageTypesService,
						useValue: messageTypesServiceMock,
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
				imports: [NoopAnimationsModule, MessageTypeDropdownComponent],
				providers: [
					{
						provide: MessageTypesService,
						useValue: messageTypesServiceMock,
					},
					{
						provide: TransactionService,
						useValue: transactionServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(MessageTypeDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
