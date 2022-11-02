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
import { TestScheduler } from 'rxjs/testing';
import { tap } from 'rxjs/operators';
import { BranchListing } from '../../../../types/branches/BranchListing';

import { CurrentBranchTypeService } from './current-branch-type.service';
import { BranchService } from './http/branch.service';
import {
	branchListingMock1,
	branchServiceMock,
} from './http/branch.service.mock';
import { BranchTypeService } from './router/branch-type.service';
import { BranchType } from '../../types-interface/types/BranchTypes.enum';
import { branchType } from '../types/BranchTypes';

describe('CurrentBranchTypeService', () => {
	let service: CurrentBranchTypeService;
	let typeService: BranchTypeService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: BranchService, useValue: branchServiceMock },
			],
		});
		service = TestBed.inject(CurrentBranchTypeService);
		typeService = TestBed.inject(BranchTypeService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should call get branches with type working and with type baseline', () => {
		scheduler.run(({ expectObservable, cold }) => {
			const driverValues: { a: branchType; b: branchType } = {
				a: 'working',
				b: 'baseline',
			};
			const driverEmissions = '----a---b----aa-bb-ab--a';
			const emissions = 'b---a---a----aa-aa-aa--a';
			const expectedValues = { a: [branchListingMock1], b: [] };
			const driver = cold(driverEmissions, driverValues).pipe(
				tap((t) => (typeService.type = t))
			);
			expectObservable(driver).toBe(driverEmissions, driverValues);
			expectObservable(service.branches).toBe(emissions, expectedValues);
		});
	});
});
