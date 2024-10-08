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
import { TestBed } from '@angular/core/testing';

import { CurrentTransportTypeService } from './current-transport-type.service';
import { transportTypeServiceMock } from '@osee/messaging/shared/testing';
import { TransportTypeService } from '@osee/messaging/shared/services';

describe('CurrentTransportTypeService', () => {
	let service: CurrentTransportTypeService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransportTypeService,
					useValue: transportTypeServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentTransportTypeService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
