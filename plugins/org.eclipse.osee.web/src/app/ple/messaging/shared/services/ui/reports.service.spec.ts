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
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { mimReportsMock } from 'src/app/ple-services/http/mim-reports.mock';
import { apiURL } from 'src/environments/environment';

import { ReportsService } from './reports.service';

describe('ReportsService', () => {
	let service: ReportsService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(ReportsService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get the difference report route', () => {
		scheduler.run(() => {
			service.BranchId = '10';
			service.BranchType = 'working';
			let expectedObservable = {
				a: '/ple/messaging/reports/working/10/differences',
			};
			let expectedMarble = '(a)';
			scheduler
				.expectObservable(service.diffReportRoute)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get reports', () => {
		const reports = mimReportsMock;
		service.getReports().subscribe();
		const req = httpTestingController.expectOne(apiURL + '/mim/reports');
		expect(req.request.method).toEqual('GET');
		req.flush(reports);
		httpTestingController.verify();
	});

	it('should get a diff report path', () => {
		scheduler.run(({ expectObservable }) => {
			service.BranchId = '10';
			service.BranchType = 'abc';
			expectObservable(service.diffReportRoute).toBe('a', {
				a: '/ple/messaging/reports/abc/10/differences',
			});
		});
	});
});
