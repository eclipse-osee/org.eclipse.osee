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
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';

import { SharedConnectionService } from './shared-connection.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('SharedConnectionService', () => {
	let service: SharedConnectionService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(SharedConnectionService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should fetch a connection', () => {
		service.getConnection('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/connections/' + 20
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
	});
});
