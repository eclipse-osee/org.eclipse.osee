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
import { apiURL } from 'src/environments/environment';
import { changeReportMock } from './change-report.mock';
import { differenceReportMock } from './difference-report.mock';

import { DifferenceReportService } from './difference-report.service';

describe('DifferenceReportService', () => {
	let service: DifferenceReportService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(DifferenceReportService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get transaction info', () => {
		const testInfo = changeReportMock;
		service.getDifferences('20', '10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/branches/' + 10 + '/diff/' + 20
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testInfo);
		httpTestingController.verify();
	});

	it('should get difference report', () => {
		const testReport = differenceReportMock;
		service.getDifferenceReport('20', '10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/diff/' + 20
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testReport);
		httpTestingController.verify();
	});
});
