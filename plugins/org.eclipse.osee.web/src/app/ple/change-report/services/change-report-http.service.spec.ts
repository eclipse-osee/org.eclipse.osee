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
import { apiURL } from '@osee/environments';
import { changeReportMock } from '../mocks/changeReportMock';

import { ChangeReportHttpService } from './change-report-http.service';

describe('ChangeReportHttpService', () => {
	let service: ChangeReportHttpService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(ChangeReportHttpService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get a change report using two branch ids', () => {
		service.getBranchChangeReport('10', '12').subscribe();
		const req = httpTestingController.expectOne(
			`${apiURL}/orcs/branches/10/changes/12`
		);
		expect(req.request.method).toEqual('GET');
		req.flush(changeReportMock);
		httpTestingController.verify();
	});

	it('should get a change report using transaction ids', () => {
		service.getTxChangeReport('10', '1', '2').subscribe();
		const req = httpTestingController.expectOne(
			`${apiURL}/orcs/branches/10/changes/1/2`
		);
		expect(req.request.method).toEqual('GET');
		req.flush(changeReportMock);
		httpTestingController.verify();
	});
});
