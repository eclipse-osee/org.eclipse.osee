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
import { TestBed } from '@angular/core/testing';
import { CiSetsService } from './ci-sets.service';
import { CiSetsHttpService } from 'src/app/ci-dashboard/lib/services/ci-sets-http.service';
import { ciSetsHttpServiceMock } from 'src/app/ci-dashboard/lib/services/ci-sets-http.service.mock';

describe('CiSetsService', () => {
	let service: CiSetsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: CiSetsHttpService, useValue: ciSetsHttpServiceMock },
			],
		});
		service = TestBed.inject(CiSetsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
