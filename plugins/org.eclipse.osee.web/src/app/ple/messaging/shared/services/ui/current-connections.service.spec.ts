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
import { CurrentConnectionsService } from './current-connections.service';
import { ConnectionService } from 'src/app/ple/messaging/shared/services/public-api';
import { connectionServiceMock } from '@osee/messaging/shared/testing';

describe('CurrentConnectionsService', () => {
	let service: CurrentConnectionsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: ConnectionService,
					useValue: connectionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentConnectionsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
