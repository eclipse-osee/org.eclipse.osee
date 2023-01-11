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
import { HttpClient } from '@angular/common/http';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '../../../../../../environments/environment';
import { PlatformType } from '../../../shared/types/platformType';

import { PlatformTypesService } from './platform-types.service';

describe('PlatformTypesService', () => {
	let service: PlatformTypesService;
	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(PlatformTypesService);
		httpClient = TestBed.inject(HttpClient);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should query for platform types', () => {
		let testData: PlatformType[] = [];
		service.getFilteredElements('filter', '8').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 8 + '/elements/types/filter/' + 'filter'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});
});
