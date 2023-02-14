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
import { iif, of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { CurrentGraphService } from './current-graph.service';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';
import { BranchInfoService } from '../../../../../ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from '../../../../../ple-services/http/branch-info.service.mock';
import { DifferenceReportService } from '../../../../../ple-services/http/difference-report.service';
import { DifferenceReportServiceMock } from '../../../../../ple-services/http/difference-report.service.mock';
import { changeReportMock } from '../../../../../ple-services/http/change-report.mock';
import {
	ApplicabilityListService,
	ConnectionService,
	MimPreferencesService,
	NodeService,
} from '@osee/messaging/shared';
import type { connection, node } from '@osee/messaging/shared';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';
import { relation, transaction } from '@osee/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';
import {
	applicabilityListServiceMock,
	MimPreferencesServiceMock,
	MimPreferencesMock,
	connectionServiceMock,
} from '@osee/messaging/shared/testing';

describe('CurrentGraphService', () => {
	let service: CurrentGraphService;
	let scheduler: TestScheduler;
	let graphService: Partial<GraphService> = {
		getNodes(id: string) {
			return of({
				nodes: [
					{
						id: '1',
						name: '1',
						data: {
							id: '1',
							name: '1',
							interfaceNodeAddress: '',
							interfaceNodeBgColor: '',
						},
					},
					{
						id: '2',
						name: '2',
						data: {
							id: '2',
							name: '2',
							interfaceNodeAddress: '',
							interfaceNodeBgColor: '',
						},
					},
					{
						id: '201279',
						label: '',
						data: {
							id: '201279',
							name: '',
							interfaceNodeAddress: '',
							interfaceNodeBgColor: '',
							applicability: { id: '1', name: 'Base' },
						},
					},
					{
						id: '201379',
						label: '',
						data: {
							id: '201379',
							name: '',
							interfaceNodeAddress: '',
							interfaceNodeBgColor: '',
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
					provide: DifferenceReportService,
					useValue: DifferenceReportServiceMock,
				},
				CurrentGraphService,
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
							id: '20',
							source: '10',
							target: '15',
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
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
						},
						'1',
						'2'
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
							transportType: {
								name: 'ETHERNET',
								byteAlignValidation: false,
								byteAlignValidationSize: 0,
								messageGeneration: false,
								messageGenerationPosition: '',
								messageGenerationType: '',
							},
						},
						'2',
						'1'
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
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
							},
						},
						{
							id: '2',
							name: '2',
							data: {
								id: '2',
								name: '2',
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
							},
						},
						{
							id: '201279',
							label: '',
							data: {
								id: '201279',
								name: '',
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
								applicability: { id: '1', name: 'Base' },
							},
						},
						{
							id: '201379',
							label: '',
							data: {
								id: '201379',
								name: '',
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
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

	it('should fetch applicabilities', () => {
		scheduler.run(() => {
			const expectedFilterValues = {
				a: [
					{ id: '1', name: 'Base' },
					{ id: '2', name: 'Second' },
				],
			};
			const expectedMarble = 'a';
			routeState.branchId = '10';
			scheduler
				.expectObservable(service.applic)
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
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
							},
						},
						{
							id: '2',
							name: '2',
							data: {
								id: '2',
								name: '2',
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
							},
						},
						{
							id: '201279',
							label: '',
							data: {
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
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
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
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
									interfaceNodeBgColor: Object({
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
									interfaceNodeBgColor: {
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
								interfaceNodeAddress: '',
								interfaceNodeBgColor: '',
								description: '',
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
								added: false,
							},
						},
						{
							id: 'a201377',
							source: '201375',
							target: '201312',
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
								applicability: { id: '1', name: 'Base' },
								added: false,
							},
							label: 'T7_TC',
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

	it('should get diff service diff', () => {
		scheduler.run(({ expectObservable }) => {
			routeState.branchId = '10';
			expectObservable(service.diff).toBe('(b|)', {
				a: changeReportMock,
				b: undefined,
			});
		});
	});
});
