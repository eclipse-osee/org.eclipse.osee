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
import { TestBed } from '@angular/core/testing';
import { QueryServiceMock } from '../../testing/query.service.mock';
import { QueryService } from '../http/query.service';

import { CurrentQueryService } from './current-query.service';

describe('CurrentQueryService', () => {
	let service: CurrentQueryService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [{ provide: QueryService, useValue: QueryServiceMock }],
		});
		service = TestBed.inject(CurrentQueryService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
