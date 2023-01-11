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
import { of } from 'rxjs';
import {
	elementSearch1,
	elementSearch2,
} from '../testing/element-search.response.mock';
import { TestScheduler } from 'rxjs/testing';

import { CurrentElementSearchService } from './current-element-search.service';
import { PlatformTypesService } from './platform-types.service';
import { RouterStateService } from './router-state.service';
import { SearchService } from './search.service';

describe('CurrentElementSearchService', () => {
	let service: CurrentElementSearchService;
	let searchService: SearchService;
	let routerStateService: RouterStateService;
	let searchServiceSpy: jasmine.SpyObj<SearchService>;
	let platformTypeSpy: jasmine.SpyObj<PlatformTypesService>;
	let scheduler: TestScheduler;

	beforeEach(() => {
		jasmine.setDefaultSpyStrategy((and) =>
			and.returnValues([of('hello'), of('world')])
		);
		searchServiceSpy = jasmine.createSpyObj('SearchService', {}, [
			'searchTerm',
		]);
		jasmine.setDefaultSpyStrategy((and) =>
			and.returnValues(
				of(elementSearch1),
				of(elementSearch2),
				of([]),
				of([]),
				of([]),
				of([]),
				of([]),
				of([])
			)
		);
		platformTypeSpy = jasmine.createSpyObj('PlatformTypesService', [
			'getFilteredElements',
		]);
		jasmine.setDefaultSpyStrategy();
		TestBed.configureTestingModule({
			providers: [
				{ provide: PlatformTypesService, useValue: platformTypeSpy },
			],
		});
		service = TestBed.inject(CurrentElementSearchService);
		searchService = TestBed.inject(SearchService);
		routerStateService = TestBed.inject(RouterStateService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should create list of elements', () => {
		expect(platformTypeSpy.getFilteredElements).toBeDefined();
		scheduler.run(() => {
			routerStateService.id = '8';
			searchService.search = 'hello';
			const values = {
				a: elementSearch1,
				b: elementSearch2,
			};
			const marble = '500ms a';
			scheduler.expectObservable(service.elements).toBe(marble, values);
		});
	});
});
