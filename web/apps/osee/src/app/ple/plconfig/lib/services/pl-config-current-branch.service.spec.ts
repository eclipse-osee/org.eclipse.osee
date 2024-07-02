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
import { ActionService } from '@osee/configuration-management/services';
import { UiService } from '@osee/shared/services';
import {
	MockXResultData,
	testBranchActions,
	testBranchListing,
	testCommitResponse,
	testWorkFlow,
} from '@osee/shared/testing';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import {
	testApplicabilityTag,
	testBranchApplicability,
} from '../testing/mockBranchService';
import { plConfigTypesServiceMock } from '../testing/pl-config-types.service.mock';
import { configGroup } from '../types/pl-config-configurations';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { PlConfigTypesService } from './pl-config-types.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';

describe('PlConfigCurrentBranchService', () => {
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
			commitBranch: of(testCommitResponse),
			getApplicabilityToken: of(testApplicabilityTag),
			getCfgGroupDetail: of<configGroup>({
				id: '1',
				name: 'Hello',
				description: '',
				configurations: [],
			}),
			editConfiguration: of(MockXResultData),
			addConfiguration: of(MockXResultData),
			deleteConfiguration: of(MockXResultData),
			addConfigurationGroup: of(MockXResultData),
			deleteConfigurationGroup: of(MockXResultData),
			updateConfigurationGroup: of(MockXResultData),
		});
		actionServiceSpy = jasmine.createSpyObj('PlConfigActionService', {
			//functions required to test
			getAction: of(testBranchActions),
			getWorkFlow: of(testWorkFlow),
		});
	});
	describe('normal tests', () => {
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
			ui.diffMode = false;
			ui.difference = [];
			baseUi.diffMode = false;
		});

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		it('should get cfg group detail', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.getCfgGroupDetail('1')).toBe('a', {
					a: {
						id: '1',
						name: 'Hello',
						description: '',
						configurations: [],
					},
				});
			});
		});
		it('should edit configuration', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.editConfiguration('123', 'string')
				).toBe('a', { a: MockXResultData });
			});
		});
		it('should modify configuration', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.modifyConfiguration('123', 'string')
				).toBe('a', { a: MockXResultData });
			});
		});
		it('should edit configuration details', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.editConfigurationDetails({
						name: 'abcd',
						description: '',
						configurationGroup: ['123'],
						productApplicabilities: [],
						copyFrom: '',
					})
				).toBe('a', {
					a: MockXResultData,
				});
			});
		});

		it('should add configuration', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.addConfiguration({
						name: '',
						description: '',
						copyFrom: '1',
						configurationGroup: ['123'],
					})
				).toBe('a', {
					a: MockXResultData,
				});
			});
		});

		it('should delete configuration', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.deleteConfiguration('123')).toBe('a', {
					a: MockXResultData,
				});
			});
		});

		it('should add feature', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.addFeature({
						name: 'feature',
						description: '',
						valueType: '',
						defaultValue: '',
						values: [],
						multiValued: false,
						productApplicabilities: [],
						valueStr: '',
						productAppStr: '',
						setValueStr: function (): void {},
						setProductAppStr: function (): void {},
					})
				).toBe('a', { a: MockXResultData });
			});
		});
		it('should modify feature', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.modifyFeature({
						id: '1',
						idString: '',
						idIntValue: 1,
						type: undefined,
						name: '',
						description: '',
						valueType: '',
						valueStr: '',
						defaultValue: '',
						productAppStr: '',
						values: [],
						productApplicabilities: [],
						multiValued: false,
						setValueStr: function (): void {},
						setProductAppStr: function (): void {},
					})
				).toBe('a', { a: MockXResultData });
			});
		});
		it('should delete feature', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.deleteFeature('123')).toBe('a', {
					a: MockXResultData,
				});
			});
		});

		it('should add a configuration group', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.addConfigurationGroup({
						name: 'abcd',
						description: '',
						configurations: ['1', '2'],
					})
				).toBe('a', { a: MockXResultData });
			});
		});
		it('should delete a configuration group', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.deleteConfigurationGroup('1')).toBe(
					'a',
					{ a: MockXResultData }
				);
			});
		});
		it('should update configuration group', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(
					service.updateConfigurationGroup({
						name: 'abcd',
						description: '',
						configurations: ['1', '2', '3'],
					})
				).toBe('a', { a: MockXResultData });
			});
		});
	});
});
