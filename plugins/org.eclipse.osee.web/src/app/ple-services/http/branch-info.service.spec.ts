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
import { testBranchInfo } from 'src/app/testing/branch-info.response.mock';
import { apiURL } from 'src/environments/environment';

import { BranchInfoService } from './branch-info.service';

describe('BranchInfoService', () => {
	let service: BranchInfoService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
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
