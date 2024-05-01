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
import { apiURL } from '@osee/environments';
import { helpPagesResponseMock } from '@osee/shared/testing';

import { HelpHttpService } from './help-http.service';

describe('HelpHttpService', () => {
	let service: HelpHttpService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(HelpHttpService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get all help pages for the given app', () => {
		service.getHelpPages('APP').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/help?app=APP'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(helpPagesResponseMock);
		httpTestingController.verify();
	});

	it('should get a help page', () => {
		service.getHelpPage('10').subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/help/10');
		expect(req.request.method).toEqual('GET');
		req.flush(helpPagesResponseMock[0]);
		httpTestingController.verify();
	});
});
