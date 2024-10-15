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
import { UiService } from '@osee/shared/services';
import {
	testApplicabilityTag,
	testBranchApplicability,
} from '../testing/mockBranchService';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';
import { PlConfigTypesService } from './pl-config-types.service';
import { plConfigTypesServiceMock } from '../testing/pl-config-types.service.mock';
import {
	testBranchListing,
	MockXResultData,
	testBranchActions,
	testWorkFlow,
	changeReportMock,
} from '@osee/shared/testing';
import { ActionService } from '@osee/configuration-management/services';

describe('PlConfigCurrentBranchService diffs', () => {
	let service: PlConfigCurrentBranchService;
	let ui: PlConfigUIStateService;
	let baseUi: UiService;
	let branchServiceSpy: jasmine.SpyObj<PlConfigBranchService>;
	let actionServiceSpy: jasmine.SpyObj<ActionService>;
	let scheduler: TestScheduler;

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	beforeEach(() => {
		TestBed.resetTestingModule();
		branchServiceSpy = jasmine.createSpyObj('PlConfigBranchService', {
			//functions required to test
			getBranchApplicability: of(testBranchApplicability),
			getBranchState: of(testBranchListing),
			modifyConfiguration: of(MockXResultData),
			addFeature: of(MockXResultData),
			modifyFeature: of(MockXResultData),
			deleteFeature: of(MockXResultData),
			commitBranch: of(MockXResultData),
			getApplicabilityToken: of(testApplicabilityTag),
		});
		actionServiceSpy = jasmine.createSpyObj('PlConfigActionService', {
			//functions required to test
			getAction: of(testBranchActions),
			getWorkFlow: of(testWorkFlow),
		});
	});
	describe('diff tests', () => {
		beforeEach(() => {
			TestBed.configureTestingModule({
				providers: [
					{
						provide: PlConfigBranchService,
						useValue: branchServiceSpy,
					},
					{ provide: ActionService, useValue: actionServiceSpy },
					{
						provide: PlConfigTypesService,
						useValue: plConfigTypesServiceMock,
					},
					PlConfigCurrentBranchService,
				],
			});
			service = TestBed.inject(PlConfigCurrentBranchService);
			ui = TestBed.inject(PlConfigUIStateService);
			baseUi = TestBed.inject(UiService);
		});
		beforeEach(() => {
			ui.branchIdNum = '10';
			ui.updateReqConfig = true;
			baseUi.diffMode = false;
		});
		afterEach(() => {
			ui.diffMode = false;
			ui.difference = [];
		});

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		it('should set difference', () => {
			service.difference = [changeReportMock[0]];
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.differences).toBe('a', {
					a: [changeReportMock[0]],
				});
			});
		});
	});
});
