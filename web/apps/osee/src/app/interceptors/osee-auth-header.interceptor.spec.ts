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
	HttpClient,
	HttpInterceptorFn,
	provideHttpClient,
	withInterceptors,
} from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { apiURL, OSEEAuthURL } from '@osee/environments';
import { OseeAuthInterceptor } from './osee-auth-header.interceptor';

describe('OSEEAuthHeaderInterceptor', () => {
	let httpTestingController: HttpTestingController;
	const interceptor: HttpInterceptorFn = (req, next) =>
		TestBed.runInInjectionContext(() => OseeAuthInterceptor(req, next));
	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptors([interceptor])),
				provideHttpClientTesting(),
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(interceptor).toBeTruthy();
	});

	it('should add a header', () => {
		const http = TestBed.inject(HttpClient);
		http.get(apiURL + '/abcde').subscribe();
		const request = httpTestingController.expectOne(apiURL + '/abcde');
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
