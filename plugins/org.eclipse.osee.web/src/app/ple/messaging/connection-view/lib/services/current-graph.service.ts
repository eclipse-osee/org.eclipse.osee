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
import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, from, iif, of } from 'rxjs';
import {
	concatMap,
	filter,
	map,
	reduce,
	repeatWhen,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';
import { applic } from '@osee/shared/types/applicability';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	changeInstance,
	changeTypeEnum,
} from '@osee/shared/types/change-report';
import { SideNavService } from '@osee/shared/services/layout';
import { RelationTypeId } from '@osee/shared/types/constants';
import {
	ConnectionService,
	NodeService,
	PreferencesUIService,
	SharedConnectionUIService,
} from '@osee/messaging/shared/services';
import type {
	connection,
	connectionWithChanges,
	node,
	nodeData,
	nodeDataWithChanges,
	OseeEdge,
	OseeNode,
	transportType,
} from '@osee/messaging/shared/types';
import { relation, transactionToken } from '@osee/shared/types';
import { ClusterNode } from '@swimlane/ngx-graph';

@Injectable({
	providedIn: 'root',
})
export class CurrentGraphService {
	private _graph = combineLatest([
		this.routeStateService.id,
		this.connectionUiService.viewId,
	]).pipe(
		switchMap(([val, viewId]) =>
			iif(
				() => val !== '' && val !== '-1' && val !== undefined,
				this.graphService.getNodes(val, viewId).pipe(
					map((split) => this.transform(split)),
					repeatWhen((_) => this.updated),
					share()
				),
				of({ nodes: [], edges: [], clusters: [] })
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _nodes = combineLatest([
		this.routeStateService.isInDiff,
		this._graph,
	]).pipe(
		switchMap(([diffState, graph]) =>
			iif(
				() => diffState,
				this.differences.pipe(
					filter((val) => val !== undefined),
					map((differences) =>
						this.parseIntoNodesAndEdges(
							differences as changeInstance[],
							graph
						)
					)
				),
				of(graph)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _nodeOptions = this.routeStateService.id.pipe(
		share(),
		filter((val) => val !== '' && val !== '-1'),
		switchMap((val) =>
			this.nodeService.getNodes(val).pipe(
				repeatWhen((_) => this.updated),
				share(),
				shareReplay(1)
			)
		),
		shareReplay(1)
	);
	private _branchPrefs = this.preferenceService.BranchPrefs;
	private _preferences = this.preferenceService.preferences;

	private _update = this.routeStateService.updated;
	private _differences = new BehaviorSubject<changeInstance[] | undefined>(
		undefined
	);
	constructor(
		private graphService: GraphService,
		private nodeService: NodeService,
		private connectionService: ConnectionService,
		private routeStateService: RouteStateService,
		private sideNavService: SideNavService,
		private preferenceService: PreferencesUIService,
		private connectionUiService: SharedConnectionUIService
	) {}

	get branchType() {
		return this.connectionUiService.branchId;
	}

	get branchId() {
		return this.connectionUiService.branchType;
	}

	get differences() {
		return this._differences;
	}
	set difference(value: changeInstance[]) {
		this._differences.next(value);
	}
	get nodes() {
		return this._nodes;
	}

	get updated() {
		return this._update;
	}

	set update(value: boolean) {
		this.routeStateService.update = value;
	}

	get nodeOptions() {
		return this._nodeOptions;
	}

	get preferences() {
		return this._preferences;
	}

	get BranchPrefs() {
		return this._branchPrefs;
	}
	get sideNavContent() {
		return this.sideNavService.rightSideNavContent;
	}

	set sideNav(value: {
		opened: boolean;
		field: string;
		currentValue: string | number | applic;
		previousValue?: string | number | applic;
		transaction?: transactionToken;
		user?: string;
		date?: string;
	}) {
		this.sideNavService.rightSideNav = value;
	}

	get InDiff() {
		return this.routeStateService.isInDiff;
	}

	getPaginatedNodes(pageNum: string | number, pageSize: number) {
		return this.routeStateService.id.pipe(
			take(1),
			switchMap((id) =>
				this.nodeService.getPaginatedNodes(id, pageNum, pageSize)
			)
		);
	}
	updateConnection(
		connection: Partial<connection>,
		oldTransportTypeId: string
	) {
		const { transportType, ...edited } = connection;
		return this.routeStateService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.connectionService.changeConnection(branchId, edited)
			),
			switchMap((connectionTransaction) =>
				transportType !== undefined
					? this.connectionService
							.createTransportTypeRelation(
								oldTransportTypeId,
								edited.id || ''
							)
							.pipe(
								switchMap((createRel) =>
									this.connectionService.deleteRelation(
										connectionTransaction.branch,
										createRel,
										connectionTransaction
									)
								),
								switchMap((tx) =>
									this.connectionService.performMutation(tx)
								),
								filter((val) => val.results.txId !== '-1'),
								switchMap((results) =>
									this.connectionService.createTransportTypeRelation(
										transportType?.id || '',
										edited.id || ''
									)
								),
								switchMap((createRel) =>
									this.connectionService.addRelation(
										connectionTransaction.branch,
										createRel
									)
								)
							)
					: of(connectionTransaction)
			),
			switchMap((transaction) =>
				this.connectionService.performMutation(transaction).pipe(
					tap(() => {
						this.update = true;
					})
				)
			)
		);
	}

	unrelateConnection(nodeId: string, id: string) {
		return combineLatest([this.routeStateService.id, this.nodes]).pipe(
			take(1),
			switchMap(([branchId, nodesArray]) =>
				from(nodesArray.edges).pipe(
					//turn into multi-emission
					filter(
						(edge) =>
							edge.source === nodeId || edge.target === nodeId
					), //only emit source/target edges
					switchMap((edge) =>
						this.nodeService.getNode(branchId, nodeId).pipe(
							//get node information
							take(1),
							switchMap((node) =>
								this.connectionService
									.createNodeRelation(node?.id || '', id)
									.pipe(
										//create primary relation if nodeId==source else nodeId==target create secondary relation
										switchMap((relation) =>
											this.connectionService
												.deleteRelation(
													branchId,
													relation
												)
												.pipe(
													//turn into transaction
													switchMap((transaction) =>
														this.connectionService
															.performMutation(
																transaction
															)
															.pipe(
																tap(() => {
																	this.update =
																		true;
																})
															)
													) //send to /orcs/tx
												)
										)
									)
							)
						)
					)
				)
			)
		);
	}

	updateNode(node: Partial<node | nodeData>) {
		return this.routeStateService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.nodeService.changeNode(branchId, node).pipe(
					take(1),
					switchMap((transaction) =>
						this.nodeService.performMutation(transaction).pipe(
							tap(() => {
								this.update = true;
							})
						)
					)
				)
			)
		);
	}
	deleteNode(nodeId: string) {
		return this.routeStateService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.nodeService.deleteArtifact(branchId, nodeId).pipe(
					take(1),
					switchMap((transaction) =>
						this.nodeService.performMutation(transaction).pipe(
							tap(() => {
								this.update = true;
							})
						)
					)
				)
			)
		);
	}

	deleteNodeAndUnrelate(
		nodeId: string,
		edges: OseeEdge<connection | connectionWithChanges>[]
	) {
		return this.deleteNode(nodeId);
	}

	createNewConnection(
		connection: connection,
		sourceId: string,
		targetId: string
	) {
		const { transportType, ...restOfConnection } = connection;
		const transportTypeRelations =
			this.connectionService.createTransportTypeRelation(
				transportType.id || ''
			);
		const nodeRelations = this.nodeOptions.pipe(
			concatMap((nodes) =>
				from(
					nodes.sort((a, b) =>
						(a?.id || '-1') < (b?.id || '-1')
							? -1
							: (a?.id || '-1') === (b?.id || '-1')
							? 0
							: 1
					)
				)
			),
			filter((val) => val.id === sourceId || val.id === targetId),
			take(2), //if this returns more than 2, something is very very wrong
			switchMap((node) =>
				this.connectionService.createNodeRelation(node.id || '')
			),
			reduce((acc, curr) => [...acc, curr], [] as relation[])
		);
		return combineLatest([
			this.routeStateService.id,
			transportTypeRelations,
			nodeRelations,
		]).pipe(
			take(1),
			switchMap(([id, transportRelation, nodesRel]) =>
				this.connectionService.createConnection(id, restOfConnection, [
					transportRelation,
					...nodesRel,
					{
						typeId: RelationTypeId.DEFAULT_HIERARCHICAL,
						sideA: '8255184',
					},
				])
			),
			switchMap((tx) =>
				this.connectionService.performMutation(tx).pipe(
					tap(() => {
						this.update = true;
					})
				)
			)
		);
	}

	createNewNode(node: node | nodeData) {
		return this.routeStateService.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.nodeService.createNode(branchId, node).pipe(
					take(1),
					switchMap((transaction) =>
						this.nodeService.performMutation(transaction).pipe(
							tap(() => {
								this.update = true;
							})
						)
					)
				)
			)
		);
	}
	/**
	 * Changes edges to have an id containing a+id
	 * @param apiResponse api response containing nodes and edges
	 * @returns transformation of api response
	 */
	private transform(apiResponse: {
		nodes: OseeNode<nodeData>[];
		edges: OseeEdge<connection>[];
		clusters: ClusterNode[];
	}) {
		let returnObj: {
			nodes: OseeNode<nodeData>[];
			edges: OseeEdge<connection>[];
			clusters: ClusterNode[];
		} = { nodes: [], edges: [], clusters: [] };
		apiResponse.nodes.forEach((node) => {
			returnObj.nodes.push({ ...node, id: node.id.toString() });
		});
		apiResponse.edges.forEach((edge) => {
			returnObj.edges.push({ ...edge, id: 'a' + edge?.id?.toString() });
		});
		returnObj.clusters = apiResponse.clusters;
		return returnObj;
	}

	private parseIntoNodesAndEdges(
		changes: changeInstance[],
		graph: {
			nodes: OseeNode<nodeData | nodeDataWithChanges>[];
			edges: OseeEdge<connection | connectionWithChanges>[];
			clusters: ClusterNode[];
		}
	) {
		let newNodes: changeInstance[] = [];
		let newNodesId: string[] = [];
		let newConnections: changeInstance[] = [];
		let newConnectionsId: string[] = [];
		changes.forEach((change) => {
			//this loop is solely just for building a list of deleted nodes/connections
			if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.CONNECTION &&
				!newConnectionsId.includes(change.artId) &&
				!newNodesId.includes(change.artId)
			) {
				//deleted connection
				newConnectionsId.push(change.artId);
			} else if (
				change.itemTypeId === ARTIFACTTYPEIDENUM.NODE &&
				!newConnectionsId.includes(change.artId) &&
				!newNodesId.includes(change.artId)
			) {
				//deleted node
				newNodesId.push(change.artId);
			}
		});
		changes.forEach((change) => {
			if (graph.nodes.find((val) => val.data.id === change.artId)) {
				let index = graph.nodes.indexOf(
					graph.nodes.find(
						(val) => val.data.id === change.artId
					) as OseeNode<nodeData | nodeDataWithChanges>
				);
				graph.nodes[index] = this.nodeChange(
					change,
					graph.nodes[index]
				);
			} else if (
				graph.edges.find((val) => val.data.id === change.artId)
			) {
				let index = graph.edges.indexOf(
					graph.edges.find(
						(val) => val.data.id === change.artId
					) as OseeEdge<connection | connectionWithChanges>
				);
				graph.edges[index] = this.edgeChange(
					change,
					graph.edges[index]
				);
			} else if (newConnectionsId.includes(change.artId)) {
				//deleted connection
				newConnections.push(change);
			} else if (newNodesId.includes(change.artId)) {
				//deleted node
				newNodes.push(change);
			}
		});
		newNodes.sort((a, b) => Number(a.artId) - Number(b.artId));
		newConnections.sort((a, b) => Number(a.artId) - Number(b.artId));
		let nodes = this.splitByArtId(newNodes);
		nodes.forEach((value) => {
			if (value.length > 0) {
				graph.nodes.push(this.fixNode(this.nodeDeletionChanges(value)));
			}
		});
		let connections = this.splitByArtId(newConnections);
		connections.forEach((value) => {
			if (value.length > 0) {
				let edge = this.connectionDeletionChanges(value);
				if (edge.id !== '') {
					graph.edges.push(edge);
				}
			}
		});
		//search change array for relation changes,append as source,target
		changes.forEach((change) => {
			//not doing this currently, need to update UI to remove relation on both ends before this would work.
			if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
				//
			}
		});
		return graph;
	}

	private splitByArtId(changes: changeInstance[]): changeInstance[][] {
		let returnValue: changeInstance[][] = [];
		let prev: Partial<changeInstance> | undefined = undefined;
		let tempArray: changeInstance[] = [];
		changes.forEach((value, index) => {
			if (prev !== undefined) {
				if (prev.artId === value.artId) {
					//condition where equal, add to array
					tempArray.push(value);
				} else {
					prev = Object.assign(prev, value);
					returnValue.push(tempArray);
					tempArray = [];
					tempArray.push(value);
					//condition where not equal, set prev to value, push old array onto returnValue, create new array
				}
			} else {
				tempArray = [];
				tempArray.push(value);
				prev = {};
				prev = Object.assign(prev, value);
				//create new array, push prev onto array, set prev
			}
		});
		if (tempArray.length !== 0) {
			returnValue.push(tempArray);
		}
		return returnValue;
	}

	private isNodeDataWithChanges(
		node: OseeNode<nodeData | nodeDataWithChanges>
	): node is OseeNode<nodeDataWithChanges> {
		return (
			(node as OseeNode<nodeDataWithChanges>).data.changes !== undefined
		);
	}

	private initializeNode(
		node: OseeNode<nodeData | nodeDataWithChanges>
	): OseeNode<nodeDataWithChanges> {
		let tempNode: OseeNode<nodeDataWithChanges>;
		if (!this.isNodeDataWithChanges(node)) {
			tempNode = node as OseeNode<nodeDataWithChanges>;
			tempNode.data.changes = {};
			return tempNode;
		} else {
			tempNode = node;
			return tempNode;
		}
	}
	private nodeChange(
		change: changeInstance,
		node: OseeNode<nodeData | nodeDataWithChanges>
	): OseeNode<nodeDataWithChanges> {
		return this.parseNodeChange(change, this.initializeNode(node));
	}

	private parseNodeChange(
		change: changeInstance,
		node: OseeNode<nodeDataWithChanges>
	) {
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			let changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				node.data.changes.name = changes;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				node.data.changes.description = changes;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
			) {
				node.data.changes.interfaceNodeAddress = changes;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEBGCOLOR
			) {
				node.data.changes.interfaceNodeBgColor = changes;
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				node.data.changes.applicability = {
					previousValue: change.baselineVersion.applicabilityToken,
					currentValue: change.currentVersion.applicabilityToken,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return node;
	}

	private initializeEdge(
		edge: OseeEdge<connection | connectionWithChanges>
	): OseeEdge<connectionWithChanges> {
		let tempEdge: OseeEdge<connectionWithChanges>;
		if (!this.isEdgeDataWithChanges(edge)) {
			tempEdge = edge as OseeEdge<connectionWithChanges>;
			tempEdge.data.changes = {};
			return tempEdge;
		} else {
			tempEdge = edge;
			return tempEdge;
		}
	}
	private isEdgeDataWithChanges(
		edge: OseeEdge<connection | connectionWithChanges>
	): edge is OseeEdge<connectionWithChanges> {
		return (
			(edge as OseeEdge<connectionWithChanges>).data.changes !== undefined
		);
	}
	private edgeChange(
		change: changeInstance,
		edge: OseeEdge<connection | connectionWithChanges>
	) {
		return this.parseEdgeChange(change, this.initializeEdge(edge));
	}
	private parseEdgeChange(
		change: changeInstance,
		edge: OseeEdge<connectionWithChanges>
	) {
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			let changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				edge.data.changes.name = changes;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				edge.data.changes.description = changes;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACETRANSPORTTYPE
			) {
				edge.data.changes.transportType = changes;
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (
				change.destinationVersion.modType === '-1' &&
				change.baselineVersion.modType === '-1' &&
				change.currentVersion.modType === '1'
			) {
				edge.data.added = true;
			}
			if (change.currentVersion.transactionToken.id !== '-1') {
				edge.data.changes.applicability = {
					previousValue: change.baselineVersion.applicabilityToken,
					currentValue: change.currentVersion.applicabilityToken,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return edge;
	}
	private nodeDeletionChanges(
		changes: changeInstance[]
	): OseeNode<nodeDataWithChanges> {
		let tempNode: OseeNode<nodeDataWithChanges> = {
			data: {
				deleted: true,
				id: '-1',
				name: '',
				interfaceNodeNumber: '',
				interfaceNodeGroupId: '',
				changes: {},
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
			},
			id: '-1',
		};
		changes.forEach((value) => {
			tempNode = this.nodeDeletionChange(value, tempNode);
		});
		return tempNode;
	}

	private parseNodeDeletionChange(
		change: changeInstance,
		node: OseeNode<nodeDataWithChanges>
	) {
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			let changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.destinationVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				node.data.changes.name = changes;
				node.label = change.currentVersion.value as string;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				node.data.description = change.currentVersion.value as string;
				node.data.changes.description = changes;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
			) {
				node.data.interfaceNodeAddress = change.currentVersion
					.value as string;
				node.data.changes.interfaceNodeAddress = changes;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEBGCOLOR
			) {
				node.data.interfaceNodeBgColor = change.currentVersion
					.value as string;
				node.data.changes.interfaceNodeBgColor = changes;
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			node.data.applicability = change.currentVersion
				.applicabilityToken as applic;
			if (change.currentVersion.transactionToken.id !== '-1') {
				node.data.changes.applicability = {
					previousValue: change.baselineVersion.applicabilityToken,
					currentValue: change.currentVersion.applicabilityToken,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return node;
	}
	private nodeDeletionChange(
		change: changeInstance,
		node: OseeNode<nodeDataWithChanges>
	): OseeNode<nodeDataWithChanges> {
		node.id = change.artId;
		if (node.data === undefined) {
			node.data = {
				id: '-1',
				name: '',
				interfaceNodeNumber: '',
				interfaceNodeGroupId: '',
				deleted: true,
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
				changes: {},
			};
		}
		if (node.data.changes === undefined) {
			node.data.changes = {};
		}
		return this.parseNodeDeletionChange(change, node);
	}

	private connectionDeletionChanges(
		changes: changeInstance[]
	): OseeEdge<connectionWithChanges> {
		let tempEdge: OseeEdge<connectionWithChanges> = {
			id: '',
			source: '',
			target: '',
			data: {
				added: false,
				deleted: true,
				dashed: false,
				changes: {},
				name: '',
				description: '',
				transportType: {} as transportType,
			},
		};
		changes.forEach((value) => {
			tempEdge = this.connectionDeletionChange(value, tempEdge);
		});
		return tempEdge;
	}
	private connectionDeletionChange(
		change: changeInstance,
		edge: OseeEdge<connectionWithChanges>
	): OseeEdge<connectionWithChanges> {
		edge.id = 'a' + change.artId;
		if (edge.data === undefined) {
			edge.data = {
				added: false,
				deleted: true,
				dashed: false,
				changes: {},
				name: '',
				description: '',
				transportType: {} as transportType,
			};
		}
		if (edge.data.changes === undefined) {
			edge.data.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			let changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				edge.data.changes.name = changes;
				edge.data.name = change.currentVersion.value as string;
				edge.label = change.currentVersion.value as string;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				edge.data.changes.description = changes;
				edge.data.description = change.currentVersion.value as string;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACETRANSPORTTYPE
			) {
				//edge.data.changes.transportType = changes;
				//edge.data.transportType = change.currentVersion.value as string;
				//OBE remove @lvaglien this attribute is no longer in use
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				edge.data.applicability = change.currentVersion
					.applicabilityToken as applic | undefined;
				edge.data.changes.applicability = {
					previousValue: change.baselineVersion.applicabilityToken,
					currentValue: change.currentVersion.applicabilityToken,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return edge;
	}
	private fixNode(node: OseeNode<nodeData | nodeDataWithChanges>) {
		if (node.data.interfaceNodeBgColor === undefined) {
			node.data.interfaceNodeBgColor = '';
		}
		if (node.label === undefined) {
			node.label = '';
		}
		return node;
	}

	get messageRoute() {
		return combineLatest([
			this.routeStateService.id,
			this.routeStateService.type,
			this.routeStateService.viewId,
			this.InDiff,
		]).pipe(
			switchMap(([id, type, viewId, diff]) =>
				of({
					beginning:
						'/ple/messaging/connections/' +
						type +
						'/' +
						id +
						'/' +
						viewId +
						'/',
					end: diff ? '/diff' : '',
				})
			)
		);
	}
}
