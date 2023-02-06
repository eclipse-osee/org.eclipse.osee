/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';

import { MessagePageComponent } from './message-page.component';
import { MockMessageTableComponent } from './lib/testing/message-table.component.mock';
import { CurrentMessagesService, message } from '@osee/messaging/shared';

describe('MessageInterfaceComponent', () => {
	let component: MessagePageComponent;
	let fixture: ComponentFixture<MessagePageComponent>;
	let expectedData: message[] = [
		{
			id: '-1',
			name: 'name',
			description: 'description',
			interfaceMessageRate: '50Hz',
			interfaceMessageNumber: '0',
			interfaceMessagePeriodicity: '1Hz',
			interfaceMessageWriteAccess: true,
			interfaceMessageType: 'Connection',
			subMessages: [
				{
					id: '0',
					name: 'sub message name',
					description: '',
					interfaceSubMessageNumber: '0',
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			],
			initiatingNode: {
				id: '1',
				name: 'Node 1',
			},
		},
	];

	beforeEach(async () => {
		await TestBed.overrideComponent(MessagePageComponent, {
			set: {
				imports: [MockMessageTableComponent, RouterTestingModule],
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: {
							filter: '',
							string: '',
							messages: of(expectedData),
							BranchId: new BehaviorSubject('10'),
						},
					},
				],
			},
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessagePageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
