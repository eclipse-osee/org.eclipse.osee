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

import { TestScheduler } from 'rxjs/testing';
import {
	BranchInfoService,
	DifferenceReportService,
} from '@osee/shared/services';

import { DiffUIService } from './diff-ui.service';
import {
	BranchInfoServiceMock,
	DifferenceReportServiceMock,
	changeReportMock,
} from '@osee/shared/testing';

describe('DiffUIService', () => {
	let service: DiffUIService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: DifferenceReportService,
					useValue: DifferenceReportServiceMock,
				},
			],
		});
		service = TestBed.inject(DiffUIService);
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
	it('should not return a diff', () => {
		scheduler.run(({ expectObservable, cold }) => {
			service.DiffMode = false;
			const expectedValues = { a: changeReportMock, b: undefined };
			expectObservable(service.diff).toBe('(b|)', expectedValues);
		});
	});

	it('should return the branch id', () => {
		service.branchId = '10';
		expect(service.id).toBe('10');
	});

	it('should return the branch type', () => {
		service.branchType = 'working';
		expect(service.type).toBe('working');
	});
});
