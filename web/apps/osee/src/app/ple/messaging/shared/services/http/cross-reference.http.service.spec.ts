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
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { crossReferencesMock } from '@osee/messaging/shared/testing';
import { apiURL } from '@osee/environments';
import { CrossReferenceHttpService } from './cross-reference.http.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('CrossReferenceHttpService', () => {
	let service: CrossReferenceHttpService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(CrossReferenceHttpService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get all cross references', () => {
		service.getAll('10', '123', '', '').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/10/crossReference?connectionId=123'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(crossReferencesMock);
		httpTestingController.verify();
	});

	it('should get a cross references', () => {
		service.get('10', '456').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/10/crossReference/456'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(crossReferencesMock[0]);
		httpTestingController.verify();
	});
});
