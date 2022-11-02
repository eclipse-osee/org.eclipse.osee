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
} from '../testing/MockResponses/ElementSearch';
import { TestScheduler } from 'rxjs/testing';

import { CurrentElementSearchService } from './current-element-search.service';
import { PlatformTypesService } from './http/platform-types.service';
import { BranchIdService } from './router/branch-id.service';
import { SearchService } from './router/search.service';
import { PlatformType } from '../../shared/types/platformType';

describe('CurrentElementSearchService', () => {
	let service: CurrentElementSearchService;
	let searchService: SearchService;
	let branchIdService: BranchIdService;
	let idServiceSpy: jasmine.SpyObj<BranchIdService>;
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
		branchIdService = TestBed.inject(BranchIdService);
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
			branchIdService.id = '8';
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
