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
import { BranchInfoService } from '../../http/branch-info.service';
import { BranchInfoServiceMock } from '../../http/branch-info.service.mock';
import { changeReportMock } from '../../http/change-report.mock';
import { DifferenceReportService } from '../../http/difference-report.service';
import { DifferenceReportServiceMock } from '../../http/difference-report.service.mock';
import { UiService } from '../ui.service';

import { DifferenceBranchInfoService } from './difference-branch-info.service';

describe('DifferenceService', () => {
	let service: DifferenceBranchInfoService;
	let scheduler: TestScheduler;
	let uiService: UiService;

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
		service = TestBed.inject(DifferenceBranchInfoService);
		uiService = TestBed.inject(UiService);
		uiService.idValue = '10';
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
		scheduler.run(({ expectObservable }) => {
			const expectedValues = { a: changeReportMock };
			const expectedMarble = '(a|)';
			expectObservable(service.differences('10')).toBe(
				expectedMarble,
				expectedValues
			);
		});
	});
});
