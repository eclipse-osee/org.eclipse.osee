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

import { SearchService } from './search.service';

describe('SearchService', () => {
	let service: SearchService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(SearchService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should set search terms', () => {
		service.search = 'terms';
		expect(service.search).toEqual('terms');
	});
});
