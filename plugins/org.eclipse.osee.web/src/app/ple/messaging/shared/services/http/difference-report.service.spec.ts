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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { differenceReportMock } from '@osee/messaging/shared/testing';
import { apiURL } from 'src/environments/environment';

import { DifferenceReportService } from './difference-report.service';

describe('DifferenceReportService', () => {
	let service: DifferenceReportService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		httpTestingController = TestBed.inject(HttpTestingController);
		service = TestBed.inject(DifferenceReportService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
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
