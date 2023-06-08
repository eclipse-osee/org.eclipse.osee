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
import { TestBed } from '@angular/core/testing';
import { TransportTypeUiService } from '@osee/messaging/shared/services';
import { transportTypeUIServiceMock } from '@osee/messaging/shared/testing';
import { MessageNodesCountDirective } from './message-nodes-count.directive';

describe('MessageNodesCountDirective', () => {
	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{
					provide: TransportTypeUiService,
					useValue: transportTypeUIServiceMock,
				},
			],
		}).compileComponents();
	});
	it('should create an instance', () => {
		const directive = new MessageNodesCountDirective(
			TestBed.inject(TransportTypeUiService)
		);
		expect(directive).toBeTruthy();
	});
});
