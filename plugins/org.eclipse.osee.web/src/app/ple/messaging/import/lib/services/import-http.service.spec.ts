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
import { apiURL } from '../../../../../../environments/environment';
import {
	importOptionsMock,
	importSummaryMock,
} from '../testing/import.response.mock';

import { ImportHttpService } from './import-http.service';

describe('ImportHttpService', () => {
	let service: ImportHttpService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(ImportHttpService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get import options', () => {
		service.getImportOptions().subscribe();
		const req = httpTestingController.expectOne(apiURL + '/mim/import');
		expect(req.request.method).toEqual('GET');
		req.flush(importOptionsMock);
		httpTestingController.verify();
	});

	it('should get import summary', () => {
		service
			.getImportSummary('/test/url', 'filename', new FormData())
			.subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/test/url?fileName=filename'
		);
		expect(req.request.method).toEqual('POST');
		req.flush(importSummaryMock);
		httpTestingController.verify();
	});
});
