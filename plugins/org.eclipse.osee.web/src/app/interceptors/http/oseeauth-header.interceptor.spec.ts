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
import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { apiURL, OSEEAuthURL } from 'src/environments/environment';

import { OSEEAuthHeaderInterceptor } from './oseeauth-header.interceptor';

describe('OSEEAuthHeaderInterceptor', () => {
	let httpTestingController: HttpTestingController;
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [
				{
					provide: HTTP_INTERCEPTORS,
					useClass: OSEEAuthHeaderInterceptor,
					multi: true,
				},
				OSEEAuthHeaderInterceptor,
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		const interceptor: OSEEAuthHeaderInterceptor = TestBed.inject(
			OSEEAuthHeaderInterceptor
		);
		expect(interceptor).toBeTruthy();
	});

	it('should add a header', () => {
		const http = TestBed.inject(HttpClient);
		http.get(apiURL + 'abcde').subscribe();
		const request = httpTestingController.expectOne(
			(req) =>
				req.headers.has('osee.account.id') &&
				req.headers.get('osee.account.id') === '0'
		);
		request.flush({});
		expect(request.request.headers.has('osee.account.id'));
		expect(request.request.headers.get('osee.account.id')).toEqual('0');
	});
	describe('should not add a header', () => {
		it("shouldn't send an account header to the authentication api", () => {
			const http = TestBed.inject(HttpClient);
			http.get(OSEEAuthURL).subscribe();
			const request = httpTestingController.expectOne(
				(req) => !req.headers.has('osee.account.id')
			);
			request.flush({});
			expect(request.request.headers.has('osee.account.id')).toBeFalsy();
		});

		it("shouldn't send an account header to non-osee sources", () => {
			const http = TestBed.inject(HttpClient);
			http.get('example.com').subscribe();
			const request = httpTestingController.expectOne(
				(req) => !req.headers.has('osee.account.id')
			);
			request.flush({});
			expect(request.request.headers.has('osee.account.id')).toBeFalsy();
		});
	});
	afterEach(() => {
		httpTestingController.verify();
	});
});
