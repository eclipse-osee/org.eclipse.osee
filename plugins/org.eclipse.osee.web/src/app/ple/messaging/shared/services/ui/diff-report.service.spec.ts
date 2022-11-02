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
import { ActionService } from 'src/app/ple-services/http/action.service';
import { actionServiceMock } from 'src/app/ple-services/http/action.service.mock';
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from 'src/app/ple-services/http/branch-info.service.mock';
import { connectionDiffsMock } from 'src/app/ple-services/http/difference-report-connections.mock';
import { elementDiffsMock } from 'src/app/ple-services/http/difference-report-elements.mock';
import { messageDiffsMock } from 'src/app/ple-services/http/difference-report-messages.mock';
import { nodeDiffsMock } from 'src/app/ple-services/http/difference-report-nodes.mock';
import { structureElementDiffsMock } from 'src/app/ple-services/http/difference-report-structures-elements.mock';
import { structureDiffsMock } from 'src/app/ple-services/http/difference-report-structures.mock';
import { submessageDiffsMock } from 'src/app/ple-services/http/difference-report-submessages.mock';
import { differenceReportMock } from 'src/app/ple-services/http/difference-report.mock';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';
import { DiffReportBranchService } from 'src/app/ple-services/ui/diff/diff-report-branch.service';
import { diffReportBranchServiceMock } from 'src/app/ple-services/ui/diff/diff-report-branch.service.mock';
import { testBranchInfo } from 'src/app/ple/plconfig/testing/mockBranchService';
import { testBranchActions } from 'src/app/ple/plconfig/testing/mockTypes';
import { branchSummary } from '../../types/DifferenceReport';

import { DiffReportService } from './diff-report.service';

describe('DiffReportService', () => {
	let service: DiffReportService;
	let scheduler: TestScheduler;
	let uiService: BranchUIService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: DiffReportBranchService,
					useValue: diffReportBranchServiceMock,
				},
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{ provide: ActionService, useValue: actionServiceMock },
			],
		});
		service = TestBed.inject(DiffReportService);
		uiService = TestBed.inject(BranchUIService);
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
