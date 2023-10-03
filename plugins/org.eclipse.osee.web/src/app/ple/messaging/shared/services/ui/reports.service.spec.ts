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
import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { mimReportsMock } from '@osee/messaging/shared/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';
import { NodeTraceReportMock } from '../../testing/node-trace-report-mock';

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
			service.BranchType = 'working';
			expectObservable(service.diffReportRoute).toBe('a', {
				a: '/ple/messaging/reports/working/10/differences',
			});
		});
	});

	it('should get a trace report path', () => {
		scheduler.run(({ expectObservable }) => {
			service.BranchId = '10';
			service.BranchType = 'working';
			expectObservable(service.nodeTraceReportRoute).toBe('a', {
				a: '/ple/messaging/reports/working/10/traceReport',
			});
		});
	});

	it('should get a requirement trace report', fakeAsync(() => {
		const traceReport = NodeTraceReportMock;
		service.BranchId = '10';
		service.BranchType = 'working';
		service.nodeTraceReportRequirements.subscribe();
		tick(100);
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/reports/10/allRequirementsToInterface?count=200&pageNum=1'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(traceReport);
		httpTestingController.verify();
	}));

	it('should get an interface artifact trace report', fakeAsync(() => {
		const traceReport = NodeTraceReportMock;
		service.BranchId = '10';
		service.BranchType = 'working';
		service.nodeTraceReportInterfaceArtifacts.subscribe();
		tick(100);
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/reports/10/allInterfaceToRequirements?count=200&pageNum=1'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(traceReport);
		httpTestingController.verify();
	}));
});
