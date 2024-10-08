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
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import type { PlatformType } from '@osee/messaging/shared/types';
import { apiURL } from '@osee/environments';

import { PlatformTypesService } from './platform-types.service';

describe('PlatformTypesService', () => {
	let service: PlatformTypesService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(PlatformTypesService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should query for platform types', () => {
		const testData: PlatformType[] = [];
		service.getFilteredElements('filter', '8').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 8 + '/elements/types/filter/' + 'filter'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});
});
