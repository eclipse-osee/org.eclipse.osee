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
import { inject, Injectable } from '@angular/core';
import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import {
	PreferencesUIService,
	SharedConnectionUIService,
} from '@osee/messaging/shared/services';
import type {
	connection,
	nodeData,
	OseeEdge,
	OseeNode,
	transportType,
} from '@osee/messaging/shared/types';
import { SideNavService } from '@osee/shared/services/layout';
import {
	changeInstance,
	changeTypeEnum,
} from '@osee/shared/types/change-report';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	addRelations,
	createArtifact,
	deleteRelations,
	modifyArtifact,
} from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	legacyRelation,
	transaction,
	transactionToken,
} from '@osee/transactions/types';
import { ClusterNode } from '@swimlane/ngx-graph';
import { BehaviorSubject, combineLatest, iif, of } from 'rxjs';
import {
	filter,
	map,
	repeatWhen,
	share,
	shareReplay,
	switchMap,
} from 'rxjs/operators';
import { GraphService } from './graph.service';
import { RouteStateService } from './route-state-service.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentGraphService {
	private graphService = inject(GraphService);
	private routeStateService = inject(RouteStateService);
	private sideNavService = inject(SideNavService);
	private preferenceService = inject(PreferencesUIService);
	private connectionUiService = inject(SharedConnectionUIService);

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
	private _branchPrefs = this.preferenceService.BranchPrefs;
	private _preferences = this.preferenceService.preferences;

	private _update = this.routeStateService.updated;
	private _differences = new BehaviorSubject<changeInstance[] | undefined>(
		undefined
	);

	private _currentTx = inject(CurrentTransactionService);

	get branchType() {
		return this.connectionUiService.branchType;
	}

	get branchId() {
		return this.connectionUiService.branchId;
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

	private _getConnectionAttributes(connection: connection) {
		const {
			id,
			gammaId,
			dashed,
			added,
			changes,
			deleted,
			applicability,
			transportType,
			nodes,
			...remainingAttributes
		} = connection;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return attributes;
	}

	updateConnection(connection: connection, previousConnection: connection) {
		const {
			id,
			gammaId,
			dashed,
			added,
			changes,
			deleted,
			applicability,
			transportType,
			nodes,
			...remainingAttributes
		} = connection;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		const previousAttributes =
			this._getConnectionAttributes(previousConnection);
		const addAttributes = attributes.filter((v) => v.id === '-1');
		const modifyAttributes = attributes
			.filter((v) => v.id !== '-1')
			.filter(
				(v) =>
					previousAttributes.filter(
						(x) =>
							x.id === v.id &&
							x.typeId === v.typeId &&
							x.gammaId === v.gammaId &&
							x.value !== v.value
					).length > 0
			);
		const deleteAttributes = previousAttributes.filter(
			(v) => !attributes.map((x) => x.id).includes(v.id)
		);
		const relationstoAdd =
			previousConnection.transportType.id !== transportType.id
				? [
						{
							typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONTRANSPORTTYPE,
							aArtId: previousConnection.id,
							bArtId: transportType.id,
						},
					]
				: [];
		const relationsToRemove =
			previousConnection.transportType.id !== transportType.id
				? [
						{
							typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONTRANSPORTTYPE,
							aArtId: previousConnection.id,
							bArtId: previousConnection.transportType.id,
						},
					]
				: [];
		let tx = this._currentTx.createTransaction(
			`Updating ${previousConnection.name.value}`
		);
		tx = modifyArtifact(tx, id, applicability, {
			set: modifyAttributes,
			add: addAttributes,
			delete: deleteAttributes,
		});
		tx = addRelations(tx, relationstoAdd);
		tx = deleteRelations(tx, relationsToRemove);
		return of(tx).pipe(this._currentTx.performMutation());
	}

	unrelateConnection(nodeIds: `${number}`[], connectionId: `${number}`) {
		const nodeRelations = nodeIds.map((id) => {
			return {
				typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONNODE,
				aArtId: connectionId,
				bArtId: id,
			};
		});
		return this._currentTx.deleteRelationsAndMutate(
			`Removing connection ${connectionId} from ${nodeIds.join(',')}`,
			nodeRelations
		);
	}

	private _getNodeAttributes(node: nodeData) {
		const {
			id,
			gammaId,
			added,
			changes,
			deleted,
			applicability,
			...remainingAttributes
		} = node;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return attributes;
	}
	updateNode(node: nodeData, previousNode: nodeData) {
		const {
			id,
			gammaId,
			added,
			changes,
			deleted,
			applicability,
			...remainingAttributes
		} = node;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		const previousAttributes = this._getNodeAttributes(previousNode);
		const addAttributes = attributes.filter((v) => v.id === '-1');
		const modifyAttributes = attributes
			.filter((v) => v.id !== '-1')
			.filter(
				(v) =>
					previousAttributes.filter(
						(x) =>
							x.id === v.id &&
							x.typeId === v.typeId &&
							x.gammaId === v.gammaId &&
							x.value !== v.value
					).length > 0
			);
		const deleteAttributes = previousAttributes.filter(
			(v) => !attributes.map((x) => x.id).includes(v.id)
		);
		return this._currentTx.modifyArtifactAndMutate(
			`Modifying node ${previousNode.name.value}`,
			previousNode.id,
			applicability,
			{
				set: modifyAttributes,
				add: addAttributes,
				delete: deleteAttributes,
			}
		);
	}
	deleteNode(nodeId: `${number}`) {
		return this._currentTx.deleteArtifactAndMutate(
			`Deleting node ${nodeId}`,
			nodeId
		);
	}

	deleteNodeAndUnrelate(nodeId: `${number}`) {
		return this.deleteNode(nodeId);
	}

	createNewConnection(connection: connection) {
		const {
			id,
			gammaId,
			dashed,
			added,
			changes,
			deleted,
			applicability,
			transportType,
			nodes,
			...remainingAttributes
		} = connection;
		const transportTypeRelation = {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONTRANSPORTTYPE,
			sideB: transportType.id,
		};
		const nodeRelations = nodes
			.filter((x) => x.id !== '-1' && x.id !== '0')
			.map((x) => {
				return {
					typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONNODE,
					sideB: x.id,
				};
			});
		const hierarchicalRelation = {
			typeId: RELATIONTYPEIDENUM.DEFAULT_HIERARCHICAL,
			sideA: '8255184',
		};
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		let tx = this._currentTx.createTransaction(
			`Creating connection ${connection.name.value}`
		);
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.CONNECTION,
			applicability,
			[...nodeRelations, transportTypeRelation, hierarchicalRelation],
			undefined,
			...attributes
		);
		tx = results.tx;

		//create nodes and then perform mutation
		nodes
			.filter((v) => v.id === '-1')
			.forEach((v) => {
				tx = this._createNodeWithRelation(
					v,
					results._newArtifact.key,
					tx
				);
			});
		return of(tx).pipe(this._currentTx.performMutation());
	}

	createNewNode(node: nodeData) {
		const {
			id,
			gammaId,
			applicability,
			deleted,
			added,
			changes,
			...remainingAttributes
		} = node;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return this._currentTx.createArtifactAndMutate(
			`Creating node ${node.name.value}`,
			ARTIFACTTYPEIDENUM.NODE,
			applicability,
			[],
			...attributes
		);
	}

	private _createNodeWithRelation(
		node: nodeData,
		connectionId: string,
		tx: Required<transaction>
	) {
		const {
			id,
			gammaId,
			applicability,
			deleted,
			added,
			changes,
			...remainingAttributes
		} = node;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		const connectionRel = {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONNODE,
			sideA: connectionId,
		};
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.NODE,
			applicability,
			[connectionRel],
			undefined,
			...attributes
		);
		return results.tx;
	}
	createNodeWithRelation(node: nodeData, connectionId: string) {
		let tx = this._currentTx.createTransaction(
			`Creating node ${node.name.value} with relation to ${connectionId}`
		);
		tx = this._createNodeWithRelation(node, connectionId, tx);

		return of(tx).pipe(this._currentTx.performMutation());
	}

	createNodeConnectionRelation(connectionId: string, nodeId?: string) {
		const relation: legacyRelation = {
			typeName: 'Interface Connection Node',
			sideA: connectionId,
			sideB: nodeId,
		};
		return of(relation);
	}

	relateNode(connectionId: string, nodeId: string) {
		const relation = {
			typeId: RELATIONTYPEIDENUM.INTERFACECONNECTIONNODE,
			aArtId: connectionId,
			bArtId: nodeId,
		};
		return this._currentTx.addRelationAndMutate(
			`Adding connection-node relation between ${connectionId} and ${nodeId}`,
			relation
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
		const returnObj: {
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
			nodes: OseeNode<nodeData>[];
			edges: OseeEdge<connection>[];
			clusters: ClusterNode[];
		}
	) {
		const newNodes: changeInstance[] = [];
		const newNodesId: string[] = [];
		const newConnections: changeInstance[] = [];
		const newConnectionsId: string[] = [];
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
				const index = graph.nodes.indexOf(
					graph.nodes.find(
						(val) => val.data.id === change.artId
					) as OseeNode<nodeData>
				);
				graph.nodes[index] = this.nodeChange(
					change,
					graph.nodes[index]
				);
			} else if (
				graph.edges.find((val) => val.data.id === change.artId)
			) {
				const index = graph.edges.indexOf(
					graph.edges.find(
						(val) => val.data.id === change.artId
					) as OseeEdge<connection>
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
		const nodes = this.splitByArtId(newNodes);
		nodes.forEach((value) => {
			if (value.length > 0) {
				graph.nodes.push(this.fixNode(this.nodeDeletionChanges(value)));
			}
		});
		const connections = this.splitByArtId(newConnections);
		connections.forEach((value) => {
			if (value.length > 0) {
				const edge = this.connectionDeletionChanges(value);
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
		const returnValue: changeInstance[][] = [];
		let prev: Partial<changeInstance> | undefined = undefined;
		let tempArray: changeInstance[] = [];
		changes.forEach((value, _index) => {
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

	private initializeNode(node: OseeNode<nodeData>): OseeNode<nodeData> {
		let tempNode: OseeNode<nodeData>;
		if (node.data.changes === undefined) {
			tempNode = node as OseeNode<nodeData>;
			tempNode.data.changes = {};
			return tempNode;
		} else {
			tempNode = node;
			return tempNode;
		}
	}
	private nodeChange(
		change: changeInstance,
		node: OseeNode<nodeData>
	): OseeNode<nodeData> {
		return this.parseNodeChange(change, this.initializeNode(node));
	}

	private parseNodeChange(change: changeInstance, node: OseeNode<nodeData>) {
		if (node.data.changes === undefined) {
			node.data.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				node.data.changes.name = {
					previousValue: {
						id: node.data.name.id,
						typeId: node.data.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.name.id,
						typeId: node.data.name.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				node.data.changes.description = {
					previousValue: {
						id: node.data.description.id,
						typeId: node.data.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.description.id,
						typeId: node.data.description.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
			) {
				node.data.changes.interfaceNodeAddress = {
					previousValue: {
						id: node.data.interfaceNodeAddress.id,
						typeId: node.data.interfaceNodeAddress.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.interfaceNodeAddress.id,
						typeId: node.data.interfaceNodeAddress.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACENODEBACKGROUNDCOLOR
			) {
				node.data.changes.interfaceNodeBackgroundColor = {
					previousValue: {
						id: node.data.interfaceNodeBackgroundColor.id,
						typeId: node.data.interfaceNodeBackgroundColor.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.interfaceNodeBackgroundColor.id,
						typeId: node.data.interfaceNodeBackgroundColor.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				node.data.changes.applicability = {
					previousValue: change.baselineVersion
						.applicabilityToken as applic,
					currentValue: change.currentVersion
						.applicabilityToken as applic,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return node;
	}

	private initializeEdge(edge: OseeEdge<connection>): OseeEdge<connection> {
		let tempEdge: OseeEdge<connection>;
		if (!this.isEdgeDataWithChanges(edge)) {
			tempEdge = edge as OseeEdge<connection>;
			tempEdge.data.changes = {};
			return tempEdge;
		} else {
			tempEdge = edge;
			return tempEdge;
		}
	}
	private isEdgeDataWithChanges(
		edge: OseeEdge<connection | connection>
	): edge is OseeEdge<connection> {
		return (edge as OseeEdge<connection>).data.changes !== undefined;
	}
	private edgeChange(change: changeInstance, edge: OseeEdge<connection>) {
		return this.parseEdgeChange(change, this.initializeEdge(edge));
	}
	private parseEdgeChange(
		change: changeInstance,
		edge: OseeEdge<connection>
	) {
		if (edge.data.changes === undefined) {
			edge.data.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				edge.data.changes.name = {
					previousValue: {
						id: edge.data.name.id,
						typeId: edge.data.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: edge.data.name.id,
						typeId: edge.data.name.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				edge.data.changes.description = {
					previousValue: {
						id: edge.data.description.id,
						typeId: edge.data.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: edge.data.description.id,
						typeId: edge.data.description.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACETRANSPORTTYPE
			) {
				// edge.data.changes.transportType = changes as difference<string>; //TODO fix this edge case doesn't exist anymore, types also need to be fixed here so change.itemTypeId infers the correct difference<T> type
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
				//TODO fix the api to return a sentinel applicability for diffs?
				edge.data.changes.applicability = {
					previousValue: change.baselineVersion
						.applicabilityToken as applic,
					currentValue: change.currentVersion
						.applicabilityToken as applic,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return edge;
	}
	private nodeDeletionChanges(changes: changeInstance[]): OseeNode<nodeData> {
		let tempNode: OseeNode<nodeData> = {
			data: {
				deleted: true,
				changes: {},
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				applicability: applicabilitySentinel,
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
				interfaceNodeBackgroundColor: {
					id: '-1',
					typeId: '5221290120300474048',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeAddress: {
					id: '-1',
					typeId: '5726596359647826656',
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
		node: OseeNode<nodeData>
	) {
		if (node.data.changes === undefined) {
			node.data.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.destinationVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				node.data.changes.name = {
					previousValue: {
						id: node.data.name.id,
						typeId: node.data.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.name.id,
						typeId: node.data.name.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
				node.data.name.value = change.currentVersion.value as string;
				node.label = change.currentVersion.value as string;
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				node.data.changes.description = {
					previousValue: {
						id: node.data.description.id,
						typeId: node.data.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.description.id,
						typeId: node.data.description.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
				node.data.description.value = change.currentVersion
					.value as string;
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACENODEADDRESS
			) {
				node.data.interfaceNodeAddress.value = change.currentVersion
					.value as string;
				node.data.changes.interfaceNodeAddress = {
					previousValue: {
						id: node.data.interfaceNodeAddress.id,
						typeId: node.data.interfaceNodeAddress.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.interfaceNodeAddress.id,
						typeId: node.data.interfaceNodeAddress.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId ===
				ATTRIBUTETYPEIDENUM.INTERFACENODEBACKGROUNDCOLOR
			) {
				node.data.interfaceNodeBackgroundColor.value = change
					.currentVersion.value as string;
				node.data.changes.interfaceNodeBackgroundColor = {
					previousValue: {
						id: node.data.interfaceNodeBackgroundColor.id,
						typeId: node.data.interfaceNodeBackgroundColor.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: node.data.interfaceNodeBackgroundColor.id,
						typeId: node.data.interfaceNodeBackgroundColor.typeId,
						gammaId: change.destinationVersion
							.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			node.data.applicability = change.currentVersion
				.applicabilityToken as applic;
			if (change.currentVersion.transactionToken.id !== '-1') {
				node.data.changes.applicability = {
					previousValue: change.baselineVersion
						.applicabilityToken as applic,
					currentValue: change.currentVersion
						.applicabilityToken as applic,
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
		node: OseeNode<nodeData>
	): OseeNode<nodeData> {
		node.id = change.artId;
		if (node.data === undefined) {
			node.data = {
				id: '-1',
				gammaId: '-1',
				deleted: true,
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				applicability: applicabilitySentinel,
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
				interfaceNodeBackgroundColor: {
					id: '-1',
					typeId: '5221290120300474048',
					gammaId: '-1',
					value: '',
				},
				interfaceNodeAddress: {
					id: '-1',
					typeId: '5726596359647826656',
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
	): OseeEdge<connection> {
		let tempEdge: OseeEdge<connection> = {
			id: '',
			source: '',
			target: '',
			data: {
				id: '-1',
				gammaId: '-1',
				added: false,
				deleted: true,
				dashed: false,
				applicability: applicabilitySentinel,
				changes: {},
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				transportType: {} as transportType,
				nodes: [],
			},
		};
		changes.forEach((value) => {
			tempEdge = this.connectionDeletionChange(value, tempEdge);
		});
		return tempEdge;
	}
	private connectionDeletionChange(
		change: changeInstance,
		edge: OseeEdge<connection>
	): OseeEdge<connection> {
		edge.id = 'a' + change.artId;
		if (edge.data === undefined) {
			edge.data = {
				id: '-1',
				gammaId: '-1',
				added: false,
				deleted: true,
				dashed: false,
				applicability: applicabilitySentinel,
				changes: {},
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				transportType: {} as transportType,
				nodes: [],
			};
		}
		if (edge.data.changes === undefined) {
			edge.data.changes = {};
		}
		if (change.changeType.name === changeTypeEnum.ATTRIBUTE_CHANGE) {
			const changes = {
				previousValue: change.baselineVersion.value,
				currentValue: change.currentVersion.value,
				transactionToken: change.currentVersion.transactionToken,
			};
			if (change.itemTypeId === ATTRIBUTETYPEIDENUM.NAME) {
				edge.data.changes.name = {
					previousValue: {
						id: edge.data.name.id,
						typeId: edge.data.name.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: edge.data.name.id,
						typeId: edge.data.name.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (change.itemTypeId === ATTRIBUTETYPEIDENUM.DESCRIPTION) {
				edge.data.changes.description = {
					previousValue: {
						id: edge.data.description.id,
						typeId: edge.data.description.typeId,
						gammaId: change.baselineVersion.gammaId as `${number}`,
						value: changes.previousValue as string,
					},
					currentValue: {
						id: edge.data.description.id,
						typeId: edge.data.description.typeId,
						gammaId: change.currentVersion.gammaId as `${number}`,
						value: changes.currentValue as string,
					},
					transactionToken: changes.transactionToken,
				};
			} else if (
				change.itemTypeId === ATTRIBUTETYPEIDENUM.INTERFACETRANSPORTTYPE
			) {
				//edge.data.changes.transportType = changes;
				//edge.data.transportType = change.currentVersion.value as string;
				//OBE remove @lvaglien this attribute is no longer in use
			}
		} else if (change.changeType.name === changeTypeEnum.ARTIFACT_CHANGE) {
			if (change.currentVersion.transactionToken.id !== '-1') {
				//TODO fix this to allow null? weird that the api could return null here...potentially fix the differences api to instead return a -1 sentinel
				edge.data.applicability = change.currentVersion
					.applicabilityToken as applic;
				edge.data.changes.applicability = {
					previousValue: change.baselineVersion
						.applicabilityToken as applic,
					currentValue: change.currentVersion
						.applicabilityToken as applic,
					transactionToken: change.currentVersion.transactionToken,
				};
			}
		} else if (change.changeType.name === changeTypeEnum.RELATION_CHANGE) {
			//do nothing currently
		}
		return edge;
	}
	private fixNode(node: OseeNode<nodeData>) {
		if (node.data.interfaceNodeBackgroundColor === undefined) {
			node.data.interfaceNodeBackgroundColor = {
				id: '-1',
				typeId: '5221290120300474048',
				gammaId: '-1',
				value: '',
			};
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
			this.InDiff,
		]).pipe(
			switchMap(([id, type, diff]) =>
				of({
					beginning:
						'/ple/messaging/connections/' + type + '/' + id + '/',
					end: diff ? '/diff' : '',
				})
			)
		);
	}
}
