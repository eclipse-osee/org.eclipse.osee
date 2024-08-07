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

import { CurrentGraphService } from './current-graph.service';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';
import {
	ApplicabilityListService,
	BranchInfoService,
} from '@osee/shared/services';

import type {
	connection,
	node,
	nodeData,
	OseeEdge,
	OseeNode,
} from '@osee/messaging/shared/types';
import { UserDataAccountService } from '@osee/auth';
import { transaction } from '@osee/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';
import {
	MimPreferencesServiceMock,
	MimPreferencesMock,
	connectionServiceMock,
	sharedConnectionServiceMock,
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import {
	applicabilityListServiceMock,
	BranchInfoServiceMock,
	changeReportMock,
} from '@osee/shared/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	ConnectionService,
	MimPreferencesService,
	NodeService,
	SharedConnectionService,
} from '@osee/messaging/shared/services';
import { ClusterNode } from '@swimlane/ngx-graph';

describe('CurrentGraphService', () => {
	let service: CurrentGraphService;
	let scheduler: TestScheduler;
	let graphService: Partial<GraphService> = {
		getNodes(id: string, viewId: string) {
			return of({
				nodes: [
					{
						id: '1',
						name: '1',
						data: {
							id: '1',
							name: '1',
							interfaceNodeNumber: '1',
							interfaceNodeGroupId: 'group1',
							interfaceNodeAddress: '',
							interfaceNodeBackgroundColor: '',
						},
					},
					{
						id: '2',
						name: '2',
						data: {
							id: '2',
							name: '2',
							interfaceNodeNumber: '2',
							interfaceNodeGroupId: 'group2',
							interfaceNodeAddress: '',
							interfaceNodeBackgroundColor: '',
						},
					},
					{
						id: '201279',
						label: '',
						data: {
							id: '201279',
							name: '',
							interfaceNodeNumber: '3',
							interfaceNodeGroupId: 'group3',
							interfaceNodeAddress: '',
							interfaceNodeBackgroundColor: '',
							applicability: { id: '1', name: 'Base' },
						},
					},
					{
						id: '201379',
						label: '',
						data: {
							id: '201379',
							name: '',
							interfaceNodeNumber: '4',
							interfaceNodeGroupId: 'group4',
							interfaceNodeAddress: '',
							interfaceNodeBackgroundColor: '',
							applicability: { id: '1', name: 'Base' },
						},
					},
				],
				edges: [
					{
						id: '1234',
						source: '1',
						target: '2',
						data: {
							name: 'abcd',
							description: '',
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
						},
					},
					{
						id: '201376',
						source: '201279',
						target: '1',
						data: {
							id: '201376',
							name: 'foundEdge',
							description: '',
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
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
			});
		},
	};
	let nodeService: Partial<NodeService> = {
		getNodes(branchId: string) {
			return of([
				{ id: '1', name: '1' },
				{ id: '2', name: '2' },
			]);
		},
		getNode(branchId: string, nodeId: string) {
			return of({ id: '1', name: '1' });
		},
		createNode(branchId: string, body: Partial<node>) {
			return of(transactionMock);
		},
		changeNode(branchId: string, node: Partial<node>) {
			return of(transactionMock);
		},
		performMutation(transaction: transaction) {
			return of(transactionResultMock);
		},
		deleteArtifact(branchId: string, artId: string) {
			return of(transactionMock);
		},
	};
	let routeState: RouteStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: GraphService, useValue: graphService },
				{ provide: NodeService, useValue: nodeService },
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
				.expectObservable(service.updateConnection({}, ''))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when unrelating connection(source)', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(aa|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.unrelateConnection('1', '1'))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when unrelating connection(target)', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.unrelateConnection('2', '2'))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when updating a node', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.updateNode({}))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when deleting a node and unrelating', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(
					service.deleteNodeAndUnrelate('10', [
						{
							id: '20',
							source: '15',
							target: '10',
							data: {
								name: 'abcd',
								description: '',
								transportType: ethernetTransportType,
								nodes: nodesMock,
							},
						},
						{
							id: '20',
							source: '10',
							target: '15',
							data: {
								name: 'abcd',
								description: '',
								transportType: ethernetTransportType,
								nodes: nodesMock,
							},
						},
					])
				)
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
				.expectObservable(
					service.createNewConnection(
						{
							name: 'connection',
							description: '',
							transportType: ethernetTransportType,
							nodes: [],
						},
						['1', '2']
					)
				)
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
				.expectObservable(
					service.createNewConnection(
						{
							name: 'connection',
							description: '',
							transportType: ethernetTransportType,
							nodes: [],
						},
						['2', '1']
					)
				)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should return a transactionResultMock when creating a node', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.createNewNode({ name: 'node' }))
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should fetch empty array of nodes and edges', () => {
		scheduler.run(() => {
			const expectedfilterValues = {
				a: {
					nodes: [
						{
							id: '1',
							name: '1',
							data: {
								id: '1',
								name: '1',
								interfaceNodeNumber: '1',
								interfaceNodeGroupId: 'group1',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
							},
						},
						{
							id: '2',
							name: '2',
							data: {
								id: '2',
								name: '2',
								interfaceNodeNumber: '2',
								interfaceNodeGroupId: 'group2',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
							},
						},
						{
							id: '201279',
							label: '',
							data: {
								id: '201279',
								name: '',
								interfaceNodeNumber: '3',
								interfaceNodeGroupId: 'group3',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
								applicability: { id: '1', name: 'Base' },
							},
						},
						{
							id: '201379',
							label: '',
							data: {
								id: '201379',
								name: '',
								interfaceNodeNumber: '4',
								interfaceNodeGroupId: 'group4',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
								applicability: { id: '1', name: 'Base' },
							},
						},
					],
					edges: [
						{
							id: 'a1234',
							source: '1',
							target: '2',
							data: {
								name: 'abcd',
								description: '',
								transportType: {
									name: 'ETHERNET',
									byteAlignValidation: false,
									byteAlignValidationSize: 0,
									messageGeneration: false,
									messageGenerationPosition: '',
									messageGenerationType: '',
								},
							},
						},
						{
							id: 'a201376',
							source: '201279',
							target: '1',
							data: {
								id: '201376',
								name: 'foundEdge',
								description: '',
								transportType: {
									name: 'ETHERNET',
									byteAlignValidation: false,
									byteAlignValidationSize: 0,
									messageGeneration: false,
									messageGenerationPosition: '',
									messageGenerationType: '',
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
				},
			};
			const expectedMarble = 'a';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.nodes)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should fetch array of nodes', () => {
		scheduler.run(() => {
			const expectedfilterValues = {
				a: [
					{ id: '1', name: '1' },
					{ id: '2', name: '2' },
				],
			};
			const expectedMarble = 'a';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.nodeOptions)
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
		scheduler.run(({ expectObservable, cold }) => {
			routeState.branchId = '10';
			const values = { a: [], b: changeReportMock, c: undefined };
			service.difference = changeReportMock;
			expectObservable(service.differences).toBe('b', values);
		});
	});
	it('should get differences in graph', () => {
		scheduler.run(({ expectObservable }) => {
			service.difference = changeReportMock;
			routeState.DiffMode = true;
			routeState.branchId = '10';
			const expectedfilterValues = {
				a: {
					nodes: [
						{
							id: '1',
							name: '1',
							data: {
								id: '1',
								name: '1',
								interfaceNodeNumber: '1',
								interfaceNodeGroupId: 'group1',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
							},
						},
						{
							id: '2',
							name: '2',
							data: {
								id: '2',
								name: '2',
								interfaceNodeNumber: '2',
								interfaceNodeGroupId: 'group2',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
							},
						},
						{
							id: '201279',
							label: '',
							data: {
								interfaceNodeNumber: '3',
								interfaceNodeGroupId: 'group3',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
								applicability: {
									id: '1',
									name: 'Base',
								},
								id: '201279',
								name: '',
								changes: {
									description: {
										previousValue: '',
										currentValue: 'changed',
										transactionToken: {
											id: '1014',
											branchId: '1014568291390890988',
										},
									},
								},
							},
						},
						{
							id: '201379',
							label: '',
							data: {
								id: '201379',
								name: '',
								interfaceNodeNumber: '4',
								interfaceNodeGroupId: 'group4',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
								applicability: { id: '1', name: 'Base' },
								changes: {
									applicability: Object({
										previousValue: null,
										currentValue: Object({
											id: '1',
											name: 'Base',
										}),
										transactionToken: Object({
											id: '1239',
											branchId: '2780650236653788489',
										}),
									}),
									name: Object({
										previousValue: null,
										currentValue:
											'testNodeForGettingConnectionEndpoint',
										transactionToken: Object({
											id: '1239',
											branchId: '2780650236653788489',
										}),
									}),
									interfaceNodeAddress: Object({
										previousValue: null,
										currentValue: '',
										transactionToken: Object({
											id: '1239',
											branchId: '2780650236653788489',
										}),
									}),
									interfaceNodeBackgroundColor: Object({
										previousValue: null,
										currentValue: '',
										transactionToken: Object({
											id: '1239',
											branchId: '2780650236653788489',
										}),
									}),
								},
							},
						},
						{
							data: {
								deleted: true,
								id: '-1',
								name: '',
								changes: {
									name: {
										previousValue: null,
										currentValue: null,
										transactionToken: {
											id: '1239',
											branchId: '2780650236653788489',
										},
									},
									description: {
										previousValue: null,
										currentValue: null,
										transactionToken: {
											id: '1239',
											branchId: '2780650236653788489',
										},
									},
									interfaceNodeAddress: {
										previousValue: null,
										currentValue: null,
										transactionToken: {
											id: '1239',
											branchId: '2780650236653788489',
										},
									},
									interfaceNodeBackgroundColor: {
										previousValue: null,
										currentValue: null,
										transactionToken: {
											id: '1239',
											branchId: '2780650236653788489',
										},
									},
									applicability: {
										previousValue: null,
										currentValue: { id: '1', name: 'Base' },
										transactionToken: {
											id: '1239',
											branchId: '2780650236653788489',
										},
									},
								},
								interfaceNodeNumber: '',
								interfaceNodeGroupId: '',
								interfaceNodeAddress: '',
								interfaceNodeBackgroundColor: '',
								description: '',
								interfaceNodeBuildCodeGen: false,
								interfaceNodeCodeGen: false,
								interfaceNodeCodeGenName: '',
								nameAbbrev: '',
								interfaceNodeToolUse: false,
								interfaceNodeType: '',
								notes: '',
								applicability: {
									id: '1',
									name: 'Base',
								},
							},
							id: '201375',
							label: 'testNodeForGettingConnectionEndpoint',
						},
					],
					edges: [
						{
							id: 'a1234',
							source: '1',
							target: '2',
							data: {
								name: 'abcd',
								description: '',
								transportType: {
									name: 'ETHERNET',
									byteAlignValidation: false,
									byteAlignValidationSize: 0,
									messageGeneration: false,
									messageGenerationPosition: '',
									messageGenerationType: '',
								},
							},
						},
						{
							id: 'a201376',
							source: '201279',
							target: '1',
							data: {
								id: '201376',
								name: 'foundEdge',
								description: '',
								transportType: {
									name: 'ETHERNET',
									byteAlignValidation: false,
									byteAlignValidationSize: 0,
									messageGeneration: false,
									messageGenerationPosition: '',
									messageGenerationType: '',
								},
								changes: {
									applicability: {
										previousValue: null,
										currentValue: { id: '1', name: 'Base' },
										transactionToken: {
											id: '1234',
											branchId: '2780650236653788489',
										},
									},
									transportType: {
										previousValue: null,
										currentValue: 'ETHERNET',
										transactionToken: {
											id: '1234',
											branchId: '2780650236653788489',
										},
									},
									name: {
										previousValue: null,
										currentValue: 'T8_TC',
										transactionToken: {
											id: '1234',
											branchId: '2780650236653788489',
										},
									},
								},
								added: true,
							},
						},
						{
							id: 'a201282',
							source: '',
							target: '',
							data: {
								description: 'a description',
								deleted: true,
								dashed: false,
								changes: {
									description: {
										previousValue: null,
										currentValue: 'a description',
										transactionToken: {
											id: '1246',
											branchId: '2780650236653788489',
										},
									},
								},
								name: '',
								transportType: {},
								nodes: [],
								added: false,
							},
						},
						{
							id: 'a201377',
							source: '',
							target: '',
							data: {
								deleted: true,
								description: '',
								dashed: false,
								changes: {
									applicability: {
										previousValue: null,
										currentValue: { id: '1', name: 'Base' },
										transactionToken: {
											id: '1235',
											branchId: '2780650236653788489',
										},
									},
									name: {
										previousValue: null,
										currentValue: 'T7_TC',
										transactionToken: {
											id: '1235',
											branchId: '2780650236653788489',
										},
									},
								},
								name: 'T7_TC',
								transportType: {},
								nodes: [],
								applicability: { id: '1', name: 'Base' },
								added: false,
							},
							label: 'T7_TC',
						},
					],
					clusters: [
						{
							id: '1',
							label: 'cluster1',
							childNodeIds: ['1,2'],
						},
					],
				},
			};
			const expectedMarble = 'a';
			expectObservable(service.nodes).toBe(
				expectedMarble,
				expectedfilterValues
			);
		});
	});
});
