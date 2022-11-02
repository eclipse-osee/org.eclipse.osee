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
import { testDataUser } from 'src/app/ple/plconfig/testing/mockTypes';
import { apiURL } from 'src/environments/environment';

import { MimPreferencesService } from './mim-preferences.service';

describe('MimPreferencesService', () => {
	let service: MimPreferencesService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(MimPreferencesService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get user prefs', () => {
		service.getUserPrefs('10', testDataUser).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/mim/user/' + 10);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get branch prefs', () => {
		service.getBranchPrefs(testDataUser).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/user/branches'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
