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

import { BranchListService } from './branch-list.service';
import { BranchCategoryService } from './branch-category.service';
import { UiService } from '@osee/shared/services';
import { branch } from '@osee/shared/types';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('BranchListService', () => {
	let service: BranchListService;
	let routeService: UiService;
	let categoryService: BranchCategoryService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(BranchListService);
		routeService = TestBed.inject(UiService);
		categoryService = TestBed.inject(BranchCategoryService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	describe('Core Functionality', () => {
		describe('branches observable', () => {
			it('should call for baseline branches when set to product line', () => {
				const testData: branch[] = [];
				routeService.typeValue = 'baseline';
				categoryService.category = '3';
				service.branches.subscribe();
				const req = httpTestingController.expectOne(
					apiURL +
						'/ats/ple/branches' +
						'?workType=None&type=2&category=3'
				);
				expect(req.request.method).toEqual('GET');
				req.flush(testData);
				httpTestingController.verify();
			});

			it('should call for working branches when set to working', () => {
				const testData: branch[] = [];
				routeService.typeValue = 'working';
				categoryService.category = '3';
				service.branches.subscribe();
				const req = httpTestingController.expectOne(
					apiURL +
						'/ats/ple/branches' +
						'?workType=None&type=0&category=3'
				);
				expect(req.request.method).toEqual('GET');
				req.flush(testData);
				httpTestingController.verify();
			});
		});
	});
});
