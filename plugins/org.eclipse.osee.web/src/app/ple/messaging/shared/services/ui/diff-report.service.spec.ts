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
	differenceReportMock,
	nodeDiffsMock,
	messageDiffsMock,
	submessageDiffsMock,
	structureDiffsMock,
	elementDiffsMock,
	structureElementDiffsMock,
	connectionDiffsMock,
	CurrentDifferenceReportServiceMock,
	DifferenceReportServiceMock,
} from '@osee/messaging/shared/testing';
import {
	CurrentDiffReportService,
	DifferenceReportService,
} from '@osee/messaging/shared/services';
import {
	BranchInfoServiceMock,
	actionServiceMock,
	testBranchInfo,
	testBranchActions,
} from '@osee/shared/testing';

describe('DiffReportService', () => {
	let service: DiffReportService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: DifferenceReportService,
					useValue: DifferenceReportServiceMock,
				},
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: CurrentDiffReportService,
					useValue: CurrentDifferenceReportServiceMock,
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
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.branchInfo)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the parent branch info', () => {
		scheduler.run(() => {
			let expectedObservable = { a: testBranchInfo };
			let expectedMarble = '(a|)';
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
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.branchSummary)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get difference report', () => {
		scheduler.run(() => {
			let expectedObservable = { a: differenceReportMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.diffReport)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the node differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: nodeDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.nodes)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the connection differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: connectionDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.connections)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the message differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: messageDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.messages)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the submessage differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: submessageDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.submessages)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the structure differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: structureDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.structures)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the element differences', () => {
		scheduler.run(() => {
			let expectedObservable = { a: elementDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.elements)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should get the structure differences including elements', () => {
		scheduler.run(() => {
			let expectedObservable = { a: structureElementDiffsMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.structuresWithElements)
				.toBe(expectedMarble, expectedObservable);
		});
	});
});
