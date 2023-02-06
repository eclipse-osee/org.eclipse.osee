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
import { changeReportMock } from 'src/app/ple-services/http/change-report.mock';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import {
	testApplicabilityTag,
	testBranchApplicability,
} from '../testing/mockBranchService';
import { testBranchListing } from '../../../../testing/branch-listing.response.mock';
import {
	testBranchActions,
	testWorkFlow,
} from '../../../../testing/configuration-management.response.mock';
import { MockXResultData } from '../../../../testing/XResultData.response.mock';
import { ActionService } from '../../../../ple-services/http/action.service';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { PlConfigUIStateService } from './pl-config-uistate.service';
import { PlConfigTypesService } from './pl-config-types.service';
import { plConfigTypesServiceMock } from '../testing/pl-config-types.service.mock';

describe('PlConfigCurrentBranchService diffs', () => {
	let service: PlConfigCurrentBranchService;
	let ui: PlConfigUIStateService;
	let baseUi: UiService;
	let branchServiceSpy: jasmine.SpyObj<PlConfigBranchService>;
	let actionServiceSpy: jasmine.SpyObj<ActionService>;
	let scheduler: TestScheduler;
	const diffBranchApplic = {
		associatedArtifactId: '200578',
		branch: {
			id: '3182843164128526558',
			name: 'TW195 aaa',
			viewId: '-1',
			idIntValue: -1918287650,
		},
		editable: true,
		features: [
			{
				id: '1939294030',
				name: 'ENGINE_5',
				added: true,
				values: ['A2543', 'B5543'],
				defaultValue: 'A2543',
				description: 'Used select type of engine',
				multiValued: false,
				valueType: 'String',
				type: null,
				productApplicabilities: ['OFP'],
				idIntValue: 1939294030,
				idString: '1939294030',
				configurations: [
					{
						id: '12345',
						name: 'group1',
						value: '',
						values: [],
					},
					{
						id: '200047',
						name: 'Product-C',
						value: 'A2453',
						values: ['A2453'],
					},
					{
						id: '201326',
						name: '',
						value: 'A2543',
						values: ['A2543'],
					},
				],
			},
			{
				id: '758071644',
				name: 'JHU_CONTROLLER',
				deleted: true,
				values: ['Included', 'Excluded'],
				defaultValue: 'Included',
				description: 'A small point of variation',
				multiValued: false,
				valueType: 'String',
				type: null,
				productApplicabilities: [],
				idIntValue: 758071644,
				idString: '758071644',
				configurations: [
					{
						id: '12345',
						name: 'group1',
						value: '',
						values: [],
					},
					{
						id: '201326',
						name: '',
						value: 'Included',
						values: ['Included'],
					},
				],
			},
			{
				id: '130553732',
				name: 'ROBOT_ARM_LIGHT',
				values: ['Included', 'Excluded'],
				defaultValue: 'Included',
				description: 'A significant capability',
				multiValued: false,
				valueType: 'String',
				type: null,
				productApplicabilities: ['OFP'],
				idIntValue: 130553732,
				idString: '130553732',
				configurations: [
					{
						id: '12345',
						name: 'group1',
						value: '',
						values: [],
					},
					{
						id: '201326',
						name: '',
						value: 'Included',
						values: ['Included'],
					},
				],
			},
			{
				id: '293076452',
				name: 'ROBOT_SPEAKER',
				values: ['SPKR_A', 'SPKR_B', 'SPKR_C'],
				defaultValue: 'SPKR_A',
				description: 'This feature is multi-select.',
				multiValued: true,
				valueType: 'String',
				type: null,
				productApplicabilities: [],
				idIntValue: 293076452,
				idString: '293076452',
				configurations: [
					{
						id: '12345',
						name: 'group1',
						value: '',
						values: [],
					},
					{
						id: '201326',
						name: '',
						value: 'SPKR_A',
						values: ['SPKR_A'],
					},
				],
			},
			{
				id: '201342',
				name: 'BROKENFEATURE',
				added: true,
				values: ['Included', 'Excluded'],
				defaultValue: 'Included',
				description: 'yiuyoiyoi',
				multiValued: false,
				valueType: 'String',
				type: null,
				productApplicabilities: ['Unspecified'],
				idIntValue: 201342,
				idString: '201342',
				changes: {
					name: {
						currentValue: 'BROKENFEATURE',
						previousValue: null,
						transactionToken: {
							id: '1132',
							branchId: '3361000790344842462',
						},
					},
					multiValued: {
						currentValue: false,
						previousValue: false,
						transactionToken: {
							id: '1132',
							branchId: '3361000790344842462',
						},
					},
					valueType: {
						currentValue: 'String',
						previousValue: null,
						transactionToken: {
							id: '1132',
							branchId: '3361000790344842462',
						},
					},
					defaultValue: {
						currentValue: 'Included',
						previousValue: null,
						transactionToken: {
							id: '1132',
							branchId: '3361000790344842462',
						},
					},
					values: [
						{
							currentValue: 'Included',
							previousValue: null,
							transactionToken: {
								id: '1132',
								branchId: '3361000790344842462',
							},
						},
						{
							currentValue: 'Excluded',
							previousValue: null,
							transactionToken: {
								id: '1132',
								branchId: '3361000790344842462',
							},
						},
					],
					description: {
						currentValue: 'yiuyoiyoi',
						previousValue: null,
						transactionToken: {
							id: '1132',
							branchId: '3361000790344842462',
						},
					},
					productApplicabilities: [
						{
							currentValue: 'Unspecified',
							previousValue: null,
							transactionToken: {
								id: '1132',
								branchId: '3361000790344842462',
							},
						},
					],
				},
				configurations: [
					{
						id: '12345',
						name: 'group1',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201326',
						name: '',
						value: 'Included',
						values: ['Included'],
					},
				],
			},
			{
				id: '201338',
				name: 'FEATURE083024823075320573205723094832094832095470480258743',
				deleted: true,
				values: ['Included', 'Excluded'],
				defaultValue: 'Included',
				description: 'testing observable',
				multiValued: false,
				valueType: 'String',
				type: null,
				productApplicabilities: ['Unspecified'],
				changes: {
					name: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1109',
							branchId: '3361000790344842462',
						},
					},
					multiValued: {
						currentValue: false,
						previousValue: false,
						transactionToken: {
							id: '1109',
							branchId: '3361000790344842462',
						},
					},
					valueType: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1109',
							branchId: '3361000790344842462',
						},
					},
					defaultValue: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1109',
							branchId: '3361000790344842462',
						},
					},
					values: [
						{
							currentValue: null,
							previousValue: null,
							transactionToken: {
								id: '1109',
								branchId: '3361000790344842462',
							},
						},
					],
					description: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1109',
							branchId: '3361000790344842462',
						},
					},
					productApplicabilities: [
						{
							currentValue: null,
							previousValue: null,
							transactionToken: {
								id: '1109',
								branchId: '3361000790344842462',
							},
						},
					],
				},
				configurations: [
					{
						id: '200045',
						name: 'Product A',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '200046',
						name: 'Product B',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '200047',
						name: 'Product C',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '200048',
						name: 'Product D',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201325',
						name: 'added view',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201334',
						name: 'modified product app',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201343',
						name: 'newconfig',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201326',
						name: '',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '736857919',
						name: 'abGroup',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201322',
						name: 'deleted group',
						value: 'Included',
						values: ['Included'],
					},
					{
						id: '201321',
						name: 'test',
						value: 'Included',
						values: ['Included'],
					},
				],
				setValueStr: jasmine.any(Function),
				setProductAppStr: jasmine.any(Function),
			},
		],
		groups: [
			{
				id: '736857919',
				name: 'abGroup',
				description: '',
				configurations: ['200045', '200046'],
				added: true,
				changes: {},
			},
			{
				id: '201322',
				name: 'testForEdit',
				description: '',
				configurations: [],
				deleted: true,
				changes: {
					name: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1050',
							branchId: '3361000790344842462',
						},
					},
				},
			},
			{
				id: '201321',
				name: 'test',
				description: '',
				configurations: [],
				added: true,
				changes: {
					name: {
						currentValue: 'test',
						previousValue: null,
						transactionToken: {
							id: '1047',
							branchId: '3361000790344842462',
						},
					},
				},
			},
		],
		parentBranch: {
			id: '8',
			name: 'SAW Product Line',
			viewId: '-1',
			idIntValue: 8,
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
				changes: {
					productApplicabilities: [],
				},
			},
			{
				id: '200047',
				name: 'Product C',
				description: '',
				hasFeatureApplicabilities: true,
				changes: {
					productApplicabilities: [],
				},
			},
			{
				id: '200048',
				name: 'Product D',
				description: '',
				hasFeatureApplicabilities: true,
				deleted: true,
				changes: {
					name: {
						currentValue: null,
						previousValue: 'Product D',
						transactionToken: {
							id: '1146',
							branchId: '3361000790344842462',
						},
					},
				},
			},
			{
				id: '201325',
				name: 'added view',
				description: '',
				added: true,
				hasFeatureApplicabilities: true,
				changes: {
					productApplicabilities: [],
				},
			},
			{
				id: '201334',
				name: 'modified product app',
				description: '',
				hasFeatureApplicabilities: true,
				productApplicabilities: ['hello world'],
				changes: {
					productApplicabilities: [
						{
							currentValue: 'hello world',
							previousValue: null,
							transactionToken: {
								id: '1095',
								branchId: '3361000790344842462',
							},
						},
					],
				},
			},
			{
				id: '201343',
				name: 'newconfig',
				description: '',
				hasFeatureApplicabilities: true,
				productApplicabilities: ['Unspecified'],
				added: true,
				changes: {
					name: {
						currentValue: 'newconfig',
						previousValue: null,
						transactionToken: {
							id: '1133',
							branchId: '3361000790344842462',
						},
					},
					productApplicabilities: [
						{
							currentValue: 'Unspecified',
							previousValue: null,
							transactionToken: {
								id: '1133',
								branchId: '3361000790344842462',
							},
						},
					],
				},
			},
			{
				id: '201326',
				name: 'test9',
				description: '',
				hasFeatureApplicabilities: false,
				productApplicabilities: ['Unspecified'],
				added: false,
				deleted: true,
				changes: {
					name: {
						currentValue: null,
						previousValue: null,
						transactionToken: {
							id: '1073',
							branchId: '3361000790344842462',
						},
					},
					productApplicabilities: [
						{
							currentValue: 'Unspecified',
							previousValue: null,
							transactionToken: {
								id: '1073',
								branchId: '3361000790344842462',
							},
						},
					],
				},
			},
		],
	};

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
			synchronizeGroup: of(MockXResultData),
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
		it('should filter differences', () => {
			scheduler.run(({ expectObservable, cold }) => {
				ui.diffMode = true;
				ui.difference = changeReportMock;
				expectObservable(service.branchApplicability).toBe(
					'(aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa)',
					{
						a: diffBranchApplic,
						b: testBranchApplicability,
					}
				);
			});
		});
	});
});
