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
import { TestBed } from '@angular/core/testing';
import { applicabilityListServiceMock } from '../../testing/applicability-list.service.mock';
import { ApplicabilityListService } from '../http/applicability-list.service';

import { ApplicabilityListUIService } from './applicability-list-ui.service';

describe('ApplicabilityListUIService', () => {
	let service: ApplicabilityListUIService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
		});
		service = TestBed.inject(ApplicabilityListUIService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
