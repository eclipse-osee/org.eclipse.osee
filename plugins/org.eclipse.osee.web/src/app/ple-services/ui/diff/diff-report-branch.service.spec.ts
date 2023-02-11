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
import { tap } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { changeReportMock } from '../../http/change-report.mock';
import { BranchUIService } from '../branch/branch-ui.service';

import { DiffReportBranchService } from './diff-report-branch.service';
import { DifferenceBranchInfoService } from './difference-branch-info.service';
import { DifferenceBranchInfoServiceMock } from './difference-branch-info.service.mock';

describe('DiffReportBranchService', () => {
	let service: DiffReportBranchService;
	let scheduler: TestScheduler;
	let branchService: BranchUIService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: DifferenceBranchInfoService,
					useValue: DifferenceBranchInfoServiceMock,
				},
			],
		});
		service = TestBed.inject(DiffReportBranchService);
		branchService = TestBed.inject(BranchUIService);
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

	it('should get differences', () => {
		scheduler.run(({ expectObservable, cold }) => {
			const makeemissions = cold('-a|', { a: '10' }).pipe(
				tap((t) => (branchService.idValue = t))
			);
			const expectedValues = { a: changeReportMock };
			const expectedMarble = '(a|)';
			expectObservable(service.differences).toBe(
				expectedMarble,
				expectedValues
			);
		});
	});
});
