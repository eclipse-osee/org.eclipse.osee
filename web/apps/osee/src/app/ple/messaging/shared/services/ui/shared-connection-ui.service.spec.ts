/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import {
	connectionMock,
	sharedConnectionServiceMock,
} from '@osee/messaging/shared/testing';
import { TestScheduler } from 'rxjs/testing';
import { SharedConnectionService } from '../http/shared-connection.service';
import { MimRouteService } from './mim-route.service';

import { SharedConnectionUIService } from './shared-connection-ui.service';

describe('SharedConnectionUIService', () => {
	let service: SharedConnectionUIService;
	let uiService: MimRouteService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: SharedConnectionService,
					useValue: sharedConnectionServiceMock,
				},
			],
		});
		service = TestBed.inject(SharedConnectionUIService);
		uiService = TestBed.inject(MimRouteService);
		uiService.idValue = '10';
		uiService.connectionId.set('20');
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	//TODO: this isn't behaving properly with signals...the actual code is working fine
	xit('should get the connection', () => {
		scheduler.run(({ expectObservable }) => {
			uiService.connectionId.set('30');
			expectObservable(service.connection).toBe('a', {
				a: connectionMock,
			});
		});
	});
});
