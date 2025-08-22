/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { CiDetailsListService } from './ci-details-list.service';
import {} from '@angular/common/http/testing';
import { TmoHttpService } from './tmo-http.service';
import { tmoHttpServiceMock } from './tmo-http.service.mock';

describe('CiDetailsListService', () => {
	let service: CiDetailsListService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TmoHttpService,
					useValue: tmoHttpServiceMock,
				},
				CiDetailsListService,
			],
		});
		service = TestBed.inject(CiDetailsListService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
