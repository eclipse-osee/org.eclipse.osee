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
import { TestBed } from '@angular/core/testing';
import { CiSetsHttpService } from './ci-sets-http.service';
import { signal } from '@angular/core';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('CiSetsHttpService', () => {
	let service: CiSetsHttpService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(CiSetsHttpService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	it('should request only the selected set by id and skip when none is selected', () => {
		const http = TestBed.inject(HttpTestingController);
		const branchId = signal('10');
		const setId = signal('-1');
		const resource = TestBed.runInInjectionContext(() =>
			service.getCiSetResource(branchId, setId)
		);
		TestBed.tick();
		http.expectNone(() => true);
		setId.set('55');
		TestBed.tick();
		http.expectOne((req) => req.url.endsWith('/script/tmo/10/set/55'));
		expect(resource.isLoading()).toBe(true);
	});
});
