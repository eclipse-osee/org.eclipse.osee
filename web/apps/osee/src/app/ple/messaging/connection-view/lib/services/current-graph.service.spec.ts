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

import {
	ApplicabilityListService,
	BranchInfoService,
} from '@osee/shared/services';
import { CurrentGraphService } from './current-graph.service';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';

import { applicabilitySentinel } from '@osee/applicability/types';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	ConnectionService,
	MimPreferencesService,
	NodeService,
	SharedConnectionService,
} from '@osee/messaging/shared/services';
import {
	connectionMock,
	connectionServiceMock,
	MimPreferencesMock,
	MimPreferencesServiceMock,
	nodeServiceMock,
	nodesMock,
	sharedConnectionServiceMock,
} from '@osee/messaging/shared/testing';
import type {
	connection,
	nodeData,
	OseeEdge,
	OseeNode,
} from '@osee/messaging/shared/types';
import {
	applicabilityListServiceMock,
	BranchInfoServiceMock,
	changeReportMock,
} from '@osee/shared/testing';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import { ClusterNode } from '@swimlane/ngx-graph';
describe('CurrentGraphService', () => {
	let service: CurrentGraphService;
	let scheduler: TestScheduler;
	const graph = {
		nodes: [
			{
				id: '1',
				name: '1',
				data: {
					id: '1',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: '1',
					},
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '1',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: 'group1',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: 'NODE_2',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: 'node2',
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: 'abnormal',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: 'This is also a note',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			},
			{
				id: '2',
				name: '2',
				data: {
					id: '2',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: '2',
					},
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '2',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: 'group2',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: 'NODE_2',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: 'node2',
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: 'abnormal',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: 'This is also a note',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			},
			{
				id: '201279',
				label: '',
				data: {
					id: '201279',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '3',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: 'group3',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},

					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: 'NODE_2',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: 'node2',
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: 'abnormal',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: 'This is also a note',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			},
			{
				id: '201379',
				label: '',
				data: {
					id: '201379',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '4',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: 'group4',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: 'NODE_2',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: 'node2',
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: 'abnormal',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: 'This is also a note',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			},
		],
		edges: [
			{
				id: '1234',
				source: '1',
				target: '2',
				data: {
					id: '1234',
					gammaId: '-1',
					applicability: applicabilitySentinel,
					nodes: [],
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: 'abcd',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					transportType: {
						id: '-1',
						gammaId: '-1',
						name: {
							id: '-1',
							typeId: '1152921504606847088',
							gammaId: '-1',
							value: 'ETHERNET',
						},
						byteAlignValidation: {
							id: '-1',
							typeId: '1682639796635579163',
							gammaId: '-1',
							value: false,
						},
						byteAlignValidationSize: {
							id: '-1',
							typeId: '6745328086388470469',
							gammaId: '-1',
							value: 0,
						},
						messageGeneration: {
							id: '-1',
							typeId: '6696101226215576386',
							gammaId: '-1',
							value: false,
						},
						messageGenerationPosition: {
							id: '-1',
							typeId: '7004358807289801815',
							gammaId: '-1',
							value: '',
						},
						messageGenerationType: {
							id: '-1',
							typeId: '7121809480940961886',
							gammaId: '-1',
							value: '',
						},
						minimumPublisherMultiplicity: {
							id: '-1',
							typeId: '7904304476851517',
							gammaId: '-1',
							value: 0,
						},
						maximumPublisherMultiplicity: {
							id: '-1',
							typeId: '8536169210675063038',
							gammaId: '-1',
							value: 0,
						},
						minimumSubscriberMultiplicity: {
							id: '-1',
							typeId: '6433031401579983113',
							gammaId: '-1',
							value: 0,
						},
						maximumSubscriberMultiplicity: {
							id: '-1',
							typeId: '7284240818299786725',
							gammaId: '-1',
							value: 0,
						},
						availableMessageHeaders: {
							id: '-1',
							typeId: '2811393503797133191',
							gammaId: '-1',
							value: [],
						},
						availableSubmessageHeaders: {
							id: '-1',
							typeId: '3432614776670156459',
							gammaId: '-1',
							value: [],
						},
						availableStructureHeaders: {
							id: '-1',
							typeId: '3020789555488549747',
							gammaId: '-1',
							value: [],
						},
						availableElementHeaders: {
							id: '-1',
							typeId: '3757258106573748121',
							gammaId: '-1',
							value: [],
						},
						interfaceLevelsToUse: {
							id: '-1',
							typeId: '1668394842614655222',
							gammaId: '-1',
							value: [],
						},
						dashedPresentation: {
							id: '-1',
							typeId: '3564212740439618526',
							gammaId: '-1',
							value: false,
						},
						spareAutoNumbering: {
							id: '-1',
							typeId: '6696101226215576390',
							gammaId: '-1',
							value: false,
						},
						applicability: applicabilitySentinel,
						directConnection: false,
					},
				},
			},
			{
				id: '201376',
				source: '201279',
				target: '1',
				data: {
					id: '201376',
					gammaId: '-1',
					applicability: applicabilitySentinel,
					nodes: [],
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: 'foundEdge',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					transportType: {
						id: '-1',
						gammaId: '-1',
						name: {
							id: '-1',
							typeId: '1152921504606847088',
							gammaId: '-1',
							value: 'ETHERNET',
						},
						byteAlignValidation: {
							id: '-1',
							typeId: '1682639796635579163',
							gammaId: '-1',
							value: false,
						},
						byteAlignValidationSize: {
							id: '-1',
							typeId: '6745328086388470469',
							gammaId: '-1',
							value: 0,
						},
						messageGeneration: {
							id: '-1',
							typeId: '6696101226215576386',
							gammaId: '-1',
							value: false,
						},
						messageGenerationPosition: {
							id: '-1',
							typeId: '7004358807289801815',
							gammaId: '-1',
							value: '',
						},
						messageGenerationType: {
							id: '-1',
							typeId: '7121809480940961886',
							gammaId: '-1',
							value: '',
						},
						minimumPublisherMultiplicity: {
							id: '-1',
							typeId: '7904304476851517',
							gammaId: '-1',
							value: 0,
						},
						maximumPublisherMultiplicity: {
							id: '-1',
							typeId: '8536169210675063038',
							gammaId: '-1',
							value: 0,
						},
						minimumSubscriberMultiplicity: {
							id: '-1',
							typeId: '6433031401579983113',
							gammaId: '-1',
							value: 0,
						},
						maximumSubscriberMultiplicity: {
							id: '-1',
							typeId: '7284240818299786725',
							gammaId: '-1',
							value: 0,
						},
						availableMessageHeaders: {
							id: '-1',
							typeId: '2811393503797133191',
							gammaId: '-1',
							value: [],
						},
						availableSubmessageHeaders: {
							id: '-1',
							typeId: '3432614776670156459',
							gammaId: '-1',
							value: [],
						},
						availableStructureHeaders: {
							id: '-1',
							typeId: '3020789555488549747',
							gammaId: '-1',
							value: [],
						},
						availableElementHeaders: {
							id: '-1',
							typeId: '3757258106573748121',
							gammaId: '-1',
							value: [],
						},
						interfaceLevelsToUse: {
							id: '-1',
							typeId: '1668394842614655222',
							gammaId: '-1',
							value: [],
						},
						dashedPresentation: {
							id: '-1',
							typeId: '3564212740439618526',
							gammaId: '-1',
							value: false,
						},
						spareAutoNumbering: {
							id: '-1',
							typeId: '6696101226215576390',
							gammaId: '-1',
							value: false,
						},
						applicability: applicabilitySentinel,
						directConnection: false,
					},
				},
			},
		],
		clusters: [
			{
				id: '1',
				label: 'cluster1',
				childNodeIds: ['1,2'],
			},
		],
	} as {
		nodes: OseeNode<nodeData>[];
		edges: OseeEdge<connection>[];
		clusters: ClusterNode[];
	};
	const graphService: Partial<GraphService> = {
		getNodes(_id: string, _viewId: string) {
			//TODO: pray this just works as is...
			return of(graph);
		},
	};
	let routeState: RouteStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: GraphService, useValue: graphService },
				{ provide: NodeService, useValue: nodeServiceMock },
				{ provide: ConnectionService, useValue: connectionServiceMock },
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: SharedConnectionService,
					useValue: sharedConnectionServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentGraphService);
		routeState = TestBed.inject(RouteStateService);
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

	it('should return a transactionResultMock when updating connection', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(
					service.updateConnection(connectionMock, connectionMock)
				)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when unrelating connection(source)', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.unrelateConnection(['1'], '1'))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when unrelating connection(target)', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.unrelateConnection(['2'], '2'))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when updating a node', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(
					service.updateNode(nodesMock[0], nodesMock[1])
				)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when deleting a node and unrelating', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.deleteNodeAndUnrelate('10'))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when creating a connection', () => {
		routeState.branchId = '10';
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.createNewConnection(connectionMock))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when creating a connection(and flip the target and source)', () => {
		routeState.branchId = '10';
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.createNewConnection(connectionMock))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when creating a node', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.createNewNode(nodesMock[0]))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should fetch empty array of nodes and edges', () => {
		scheduler.run(() => {
			const modifiedGraph = structuredClone(graph);
			modifiedGraph.edges[0].id = 'a1234';
			modifiedGraph.edges[1].id = 'a201376';
			const expectedfilterValues = {
				a: modifiedGraph,
			};
			const expectedMarble = 'a';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.nodes)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should fetch preferences', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: MimPreferencesMock };
			const expectedMarble = 'a';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.preferences)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should set and get differences', () => {
		scheduler.run(({ expectObservable }) => {
			routeState.branchId = '10';
			const values = { a: [], b: changeReportMock, c: undefined };
			service.difference = changeReportMock;
			expectObservable(service.differences).toBe('b', values);
		});
	});
	//TODO: we need to rethink diffing, testing this will be OBE in the near-mid term future.
	xit('should get differences in graph', () => {
		const modifiedGraph = structuredClone(graph);
		modifiedGraph.edges[0].id = 'a1234';
		modifiedGraph.edges[1].id = 'a201376';
		modifiedGraph.nodes[2].data.changes = {
			description: {
				previousValue: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '20484',
					value: '',
				},
				currentValue: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '20687',
					value: 'changed',
				},
				transactionToken: {
					id: '1014',
					branchId: '1014568291390890988',
				},
			},
		};
		modifiedGraph.nodes.push({
			data: {
				deleted: true,
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				changes: {
					name: {
						previousValue: {
							id: '-1',
							typeId: '1152921504606847088',
							gammaId: '-1',
							value: '',
						},
						currentValue: {
							id: '-1',
							typeId: '1152921504606847088',
							gammaId: '-1',
							value: '',
						},
						transactionToken: {
							id: '1239',
							branchId: '2780650236653788489',
						},
					},
					description: {
						previousValue: {
							id: '-1',
							typeId: '1152921504606847090',
							gammaId: '-1',
							value: '',
						},
						currentValue: {
							id: '-1',
							typeId: '1152921504606847090',
							gammaId: '-1',
							value: '',
						},
						transactionToken: {
							id: '1239',
							branchId: '2780650236653788489',
						},
					},
					interfaceNodeAddress: {
						previousValue: {
							id: '-1',
							typeId: '5726596359647826656',
							gammaId: '-1',
							value: '',
						},
						currentValue: {
							id: '-1',
							typeId: '5726596359647826656',
							gammaId: '-1',
							value: '',
						},
						transactionToken: {
							id: '1239',
							branchId: '2780650236653788489',
						},
					},
					interfaceNodeBackgroundColor: {
						previousValue: {
							id: '-1',
							typeId: '5221290120300474048',
							gammaId: '-1',
							value: '',
						},
						currentValue: {
							id: '-1',
							typeId: '5221290120300474048',
							gammaId: '-1',
							value: '',
						},
						transactionToken: {
							id: '1239',
							branchId: '2780650236653788489',
						},
					},
					applicability: {
						previousValue: applicabilitySentinel,
						currentValue: { id: '1', name: 'Base' },
						transactionToken: {
							id: '1239',
							branchId: '2780650236653788489',
						},
					},
				},
				interfaceNodeNumber: {
					id: '-1',
					typeId: '5726596359647826657',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeGroupId: {
					id: '-1',
					typeId: '5726596359647826658',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeAddress: {
					id: '-1',
					typeId: '5726596359647826656',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeBackgroundColor: {
					id: '-1',
					typeId: '5221290120300474048',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeBuildCodeGen: {
					id: '-1',
					typeId: '5806420174793066197',
					gammaId: '-1',
					value: false,
				},
				interfaceNodeCodeGen: {
					id: '-1',
					typeId: '4980834335211418740',
					gammaId: '-1',
					value: false,
				},
				interfaceNodeCodeGenName: {
					id: '-1',
					typeId: '5390401355909179776',
					gammaId: '-1',
					value: '',
				},
				nameAbbrev: {
					id: '-1',
					typeId: '8355308043647703563',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeToolUse: {
					id: '-1',
					typeId: '5863226088234748106',
					gammaId: '-1',
					value: false,
				},
				interfaceNodeType: {
					id: '-1',
					typeId: '6981431177168910500',
					gammaId: '-1',
					value: '',
				},
				notes: {
					id: '-1',
					typeId: '1152921504606847085',
					gammaId: '-1',
					value: '',
				},
				applicability: {
					id: '1',
					name: 'Base',
				},
			},
			id: '201375',
			label: 'testNodeForGettingConnectionEndpoint',
		});
		scheduler.run(({ expectObservable }) => {
			service.difference = changeReportMock;
			routeState.DiffMode = true;
			routeState.branchId = '10';
			const expectedfilterValues = {
				a: modifiedGraph,
			};
			const expectedMarble = 'a';
			expectObservable(service.nodes).toBe(
				expectedMarble,
				expectedfilterValues
			);
		});
	});
});
