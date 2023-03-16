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

import { StructureNamesService } from './structure-names.service';

describe('StructureNamesService', () => {
	let service: StructureNamesService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(StructureNamesService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get user prefs', () => {
		service.getStructureNames('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/all/StructureNames'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
