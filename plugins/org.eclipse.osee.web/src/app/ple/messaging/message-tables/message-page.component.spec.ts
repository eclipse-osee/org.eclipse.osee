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
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import type { message } from '@osee/messaging/shared/types';
import { MockMessageTableComponent } from '@osee/messaging/message-tables/testing';
import { applicabilitySentinel } from '@osee/shared/types/applicability';

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
			interfaceMessageExclude: false,
			interfaceMessageIoMode: '',
			interfaceMessageModeCode: '',
			interfaceMessageRateVer: '',
			interfaceMessagePriority: '',
			interfaceMessageProtocol: '',
			interfaceMessageRptWordCount: '',
			interfaceMessageRptCmdWord: '',
			interfaceMessageRunBeforeProc: false,
			interfaceMessageVer: '',
			publisherNodes: [
				{
					id: '100',
					name: 'Node1',
				},
			],
			subscriberNodes: [
				{
					id: '101',
					name: 'Node2',
				},
			],
			applicability: applicabilitySentinel,
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
							clearRows() {},
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
