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

import { UnreferencedUiService } from './unreferenced-ui.service';
import { UnreferencedService } from '../http/unreferenced.service';
import { unreferencedServiceMock } from '@osee/messaging/shared/testing';

describe('UnreferencedUiService', () => {
	let service: UnreferencedUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: UnreferencedService,
					useValue: unreferencedServiceMock,
				},
			],
		});
		service = TestBed.inject(UnreferencedUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
