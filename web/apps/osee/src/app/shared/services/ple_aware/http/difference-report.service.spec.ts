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
import { changeReportMock } from '@osee/shared/testing';

import { DifferenceReportService } from './difference-report.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('DifferenceReportService', () => {
	let service: DifferenceReportService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
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
});
