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
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';

import { EnumsService } from './enums.service';

describe('EnumsService', () => {
	let service: EnumsService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(EnumsService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});
	afterEach(() => {
		httpTestingController.verify();
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('Message Interface', () => {
		it('should fetch message periodicities', () => {
			let testData = ['p1', 'p2', 'p3'];
			service.periodicities.subscribe();
			const req = httpTestingController.expectOne(
				apiURL + '/mim/enums/' + 'MessagePeriodicities'
			);
			expect(req.request.method).toEqual('GET');
			req.flush(testData);
		});
	});

	describe('Message Element Interface', () => {
		it('should fetch structure categories', () => {
			let testData = ['s1', 's2', 's3'];
			service.categories.subscribe();
			const req = httpTestingController.expectOne(
				apiURL + '/mim/enums/' + 'StructureCategories'
			);
			expect(req.request.method).toEqual('GET');
			req.flush(testData);
		});
	});

	//basic explanation of http test
	describe('Connection View', () => {
		//describe block just groups tests
		//it() describes a spec/test
	});
});
