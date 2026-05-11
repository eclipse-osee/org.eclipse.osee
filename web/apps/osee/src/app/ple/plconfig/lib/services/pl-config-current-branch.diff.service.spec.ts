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
	let branchServiceSpy: Partial<PlConfigBranchService>;
	let actionServiceSpy: Partial<ActionService>;
	let scheduler: TestScheduler;

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	beforeEach(() => {
		TestBed.resetTestingModule();
		branchServiceSpy = {
			getBranchApplicability: vi
				.fn()
				.mockName('PlConfigBranchService.getBranchApplicability')
				.mockReturnValue(of(testBranchApplicability)),
			modifyConfiguration: vi
				.fn()
				.mockName('PlConfigBranchService.modifyConfiguration')
				.mockReturnValue(of(MockXResultData)),
			addFeature: vi
				.fn()
				.mockName('PlConfigBranchService.addFeature')
				.mockReturnValue(of(MockXResultData)),
			modifyFeature: vi
				.fn()
				.mockName('PlConfigBranchService.modifyFeature')
				.mockReturnValue(of(MockXResultData)),
			deleteFeature: vi
				.fn()
				.mockName('PlConfigBranchService.deleteFeature')
				.mockReturnValue(of(MockXResultData)),
			getApplicabilityToken: vi
				.fn()
				.mockName('PlConfigBranchService.getApplicabilityToken')
				.mockReturnValue(of(testApplicabilityTag)),
		};
		actionServiceSpy = {
			getAction: vi
				.fn()
				.mockName('PlConfigActionService.getAction')
				.mockReturnValue(of(testBranchActions)),
			getWorkFlow: vi
				.fn()
				.mockName('PlConfigActionService.getWorkFlow')
				.mockReturnValue(of(testWorkFlow)),
		};
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
