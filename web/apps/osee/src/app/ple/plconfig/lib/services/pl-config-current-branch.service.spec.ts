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
import {
	view,
	viewWithChanges,
} from '../types/pl-config-applicui-branch-mapping';
import {
	configGroup,
	configurationGroup,
} from '../types/pl-config-configurations';
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
			getCfgGroupDetail: of<configurationGroup>({
				id: '1',
				name: 'Hello',
				description: '',
				hasFeatureApplicabilities: false,
				productApplicabilities: [],
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

		it('should return the group list', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedValues: {
					a: configGroup[];
					b: configGroup[];
					c: configGroup[];
					d: configGroup[];
				} = {
					a: [
						testBranchApplicability.groups[2],
						testBranchApplicability.groups[1],
						testBranchApplicability.groups[0],
					],
					b: [testBranchApplicability.groups[2]],
					c: [
						testBranchApplicability.groups[2],
						testBranchApplicability.groups[1],
					],
					d: [],
				};
				expectObservable(service.groupList).toBe(
					'(dbca)',
					expectedValues
				);
			});
		});

		it('should find that abGroup is a cfgGroup', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedValues: { a: boolean; b: boolean } = {
					a: true,
					b: false,
				};
				expectObservable(service.isACfgGroup('abGroup')).toBe(
					'(bbbbba)',
					expectedValues
				);
			});
		});

		it('should find that Product D is not a cfgGroup', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedValues: { a: boolean; b: boolean } = {
					a: true,
					b: false,
				};
				expectObservable(service.isACfgGroup('Product D')).toBe(
					'(bbbbbb)',
					expectedValues
				);
			});
		});

		it('should return the group count', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.groupCount).toBe('(abcd)', {
					a: 6,
					b: 7,
					c: 8,
					d: 11,
				});
			});
		});

		it('should return the view count', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.viewCount).toBe('(a)', { a: 5 });
			});
		});

		it('should return the headers', () => {
			scheduler.run(({ expectObservable }) => {
				spyOn(Math, 'random').and.returnValue(0); //remove randomness
				const expectedValues = {
					a: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
					],
					b: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
					],
					c: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
					],
					d: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
					],
					e: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
					],
					f: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
						{ columnId: '', name: 'test' },
					],
					g: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
						{ columnId: '', name: 'test' },
						{ columnId: '', name: 'deleted group' },
					],
					h: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
						{ columnId: '', name: 'test' },
						{ columnId: '', name: 'deleted group' },
						{ columnId: '', name: 'abGroup' },
					],
					i: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
						{ columnId: '', name: 'test' },
						{ columnId: '', name: 'deleted group' },
						{ columnId: '', name: 'abGroup' },
						{ columnId: '', name: 'Product A' },
					],
					j: [
						{ columnId: '0', name: 'feature' },
						{ columnId: '', name: 'Product C' },
						{ columnId: '', name: 'Product D' },
						{ columnId: '', name: 'added view' },
						{ columnId: '', name: 'modified product app' },
						{ columnId: '', name: 'newconfig' },
						{ columnId: '', name: 'test' },
						{ columnId: '', name: 'deleted group' },
						{ columnId: '', name: 'abGroup' },
						{ columnId: '', name: 'Product A' },
						{ columnId: '', name: 'Product B' },
					],
				};
				expectObservable(service.headers).toBe(
					'(abcdefghij)',
					expectedValues
				);
			});
		});

		it('should return the secondary header length', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.secondaryHeaderLength).toBe('(aaaa)', {
					a: [1, 5, 1, 1, 3],
					b: [1, 5, 3],
				});
			});
		});

		it('should get the secondary headers', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedValues: {
					a: string[];
					b: string[];
					c: string[];
					d: string[];
				} = {
					a: ['   ', 'No Group '],
					b: ['    ', 'No Group  ', 'test '],
					c: ['     ', 'No Group   ', 'test  ', 'deleted group '],
					d: [
						'      ',
						'No Group    ',
						'test   ',
						'deleted group  ',
						'abGroup ',
					],
				};
				expectObservable(service.secondaryHeaders).toBe(
					'(abcd)',
					expectedValues
				);
			});
		});

		it('should get the top level headers', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.topLevelHeaders).toBe('(cbbb)', {
					a: [' ', 'Configurations', 'Groups'],
					b: [' ', 'Groups'],
					c: [' ', 'Configurations'],
				});
			});
		});

		it('should find a view by name', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.findViewByName('Product A')).toBe(
					'(bbbbbaa|)',
					{
						a: {
							id: '200045',
							name: 'Product A',
							description: '',
							hasFeatureApplicabilities: true,
						},
						b: undefined,
					}
				);
			});
		});

		it('should find a view by id', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.findViewById('200045')).toBe(
					'(bbbbbaa|)',
					{
						a: {
							id: '200045',
							name: 'Product A',
							description: '',
							hasFeatureApplicabilities: true,
						},
					}
				);
			});
		});

		it('should find a group by name', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.findGroup('abGroup')).toBe(
					'(bbbbba)',
					{
						a: {
							id: '736857919',
							name: 'abGroup',
							description: '',
							configurations: ['200045', '200046'],
						},
						b: undefined,
					}
				);
			});
		});
		it('should return the grouping', () => {
			scheduler.run(({ expectObservable }) => {
				const expectedValues: {
					a: {
						group: configGroup;
						views: (view | viewWithChanges)[];
					}[];
					b: {
						group: configGroup;
						views: (view | viewWithChanges)[];
					}[];
				} = {
					a: [
						{
							group: {
								id: '-1',
								name: 'No Group',
								description: '',
								configurations: [],
							},
							views: [
								{
									id: '200047',
									name: 'Product C',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '200048',
									name: 'Product D',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '201325',
									name: 'added view',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '201334',
									name: 'modified product app',
									description: '',
									hasFeatureApplicabilities: true,
									productApplicabilities: ['hello world'],
								},
								{
									id: '201343',
									name: 'newconfig',
									description: '',
									hasFeatureApplicabilities: true,
									productApplicabilities: ['Unspecified'],
								},
							],
						},
						{
							group: {
								id: '201321',
								name: 'test',
								description: '',
								configurations: [],
							},
							views: [],
						},
						{
							group: {
								id: '201322',
								name: 'deleted group',
								description: '',
								configurations: [],
							},
							views: [],
						},
						{
							group: {
								id: '736857919',
								name: 'abGroup',
								description: '',
								configurations: ['200045', '200046'],
							},
							views: [
								{
									id: '200045',
									name: 'Product A',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '200046',
									name: 'Product B',
									description: '',
									hasFeatureApplicabilities: true,
								},
							],
						},
					],
					b: [
						{
							group: {
								id: '-1',
								name: 'No Group',
								description: '',
								configurations: [],
							},
							views: [
								{
									id: '200047',
									name: 'Product C',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '200048',
									name: 'Product D',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '201325',
									name: 'added view',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '201334',
									name: 'modified product app',
									description: '',
									hasFeatureApplicabilities: true,
									productApplicabilities: ['hello world'],
								},
								{
									id: '201343',
									name: 'newconfig',
									description: '',
									hasFeatureApplicabilities: true,
									productApplicabilities: ['Unspecified'],
								},
							],
						},
						{
							group: {
								id: '736857919',
								name: 'abGroup',
								description: '',
								configurations: ['200045', '200046'],
							},
							views: [
								{
									id: '200045',
									name: 'Product A',
									description: '',
									hasFeatureApplicabilities: true,
								},
								{
									id: '200046',
									name: 'Product B',
									description: '',
									hasFeatureApplicabilities: true,
								},
							],
						},
					],
				};
				expectObservable(service.grouping).toBe('(a)', expectedValues);
			});
		});
		it('should get cfg group detail', () => {
			scheduler.run(({ expectObservable }) => {
				expectObservable(service.getCfgGroupDetail('1')).toBe('a', {
					a: {
						id: '1',
						name: 'Hello',
						description: '',
						hasFeatureApplicabilities: false,
						productApplicabilities: [],
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
					service.modifyConfiguration('123', 'string', [
						{
							id: '1',
							name: 'name',
							description: '',
							configurations: ['2', '3'],
						},
					])
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
