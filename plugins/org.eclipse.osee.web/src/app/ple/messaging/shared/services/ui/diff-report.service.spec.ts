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
import { TestScheduler } from 'rxjs/testing';
import {
	ActionService,
	BranchInfoService,
	UiService,
} from '@osee/shared/services';
import type { branchSummary } from '../../types/DifferenceReport';

import { DiffReportService } from './diff-report.service';
import {
	diffReportHttpServiceMock,
	mimChangeSummaryMock,
} from '@osee/messaging/shared/testing';
import {
	BranchInfoServiceMock,
	actionServiceMock,
	testBranchInfo,
	testBranchActions,
} from '@osee/shared/testing';
import { DiffReportHttpService } from '../http/diff-report-http.service';

describe('DiffReportService', () => {
	let service: DiffReportService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: DiffReportHttpService,
					useValue: diffReportHttpServiceMock,
				},
			],
		});
		service = TestBed.inject(DiffReportService);
		uiService = TestBed.inject(UiService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				uiService.idValue = '10';
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get the branch info', () => {
		scheduler.run(() => {
			let expectedObservable = { a: testBranchInfo };
			let expectedMarble = 'a';
			scheduler
				.expectObservable(service.branchInfo)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the parent branch info', () => {
		scheduler.run(() => {
			let expectedObservable = { a: testBranchInfo };
			let expectedMarble = 'a';
			scheduler
				.expectObservable(service.parentBranchInfo)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the branch summary', () => {
		scheduler.run(() => {
			let summary: branchSummary[] = [
				{
					pcrNo: testBranchActions[0].AtsId,
					description: testBranchActions[0].Name,
					compareBranch: testBranchInfo.name,
				},
			];
			let expectedObservable = { a: summary };
			let expectedMarble = 'a';
			scheduler
				.expectObservable(service.branchSummary)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get difference report', () => {
		scheduler.run(() => {
			let expectedObservable = { a: mimChangeSummaryMock };
			let expectedMarble = 'a';
			scheduler
				.expectObservable(service.diffReport)
				.toBe(expectedMarble, expectedObservable);
		});
	});
});
