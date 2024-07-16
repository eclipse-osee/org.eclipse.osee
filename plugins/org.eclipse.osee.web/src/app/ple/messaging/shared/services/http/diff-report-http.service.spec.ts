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
import { DiffReportHttpService } from './diff-report-http.service';

describe('DiffReportHttpService', () => {
	let service: DiffReportHttpService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		httpTestingController = TestBed.inject(HttpTestingController);
		service = TestBed.inject(DiffReportHttpService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
