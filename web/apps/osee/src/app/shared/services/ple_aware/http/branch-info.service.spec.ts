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
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';
import { testBranchInfo } from '@osee/shared/testing';

import { BranchInfoService } from './branch-info.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('BranchInfoService', () => {
	let service: BranchInfoService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(BranchInfoService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get branch info', () => {
		const testInfo = testBranchInfo;
		service.getBranch('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/branches/' + 10
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testInfo);
		httpTestingController.verify();
	});
});
