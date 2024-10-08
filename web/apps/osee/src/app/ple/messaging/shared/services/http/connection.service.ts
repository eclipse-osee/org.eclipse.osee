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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { of } from 'rxjs';
import { apiURL } from '@osee/environments';
import type { connection, _newConnection } from '../../types/connection';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
	createArtifact,
} from '@osee/transactions/types';
import { HttpParamsType } from '@osee/shared/types';
import { TransactionService } from '@osee/transactions/services';
import { createArtifact as _createArtifact } from '@osee/transactions/functions';
import { nodeData } from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ConnectionService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	getConnections(branchId: string) {
		return this.http.get<connection[]>(
			apiURL + '/mim/branch/' + branchId + '/connections'
		);
	}

	getFiltered(
		branchId: string,
		filter?: string,
		viewId?: string,
		pageNum?: string | number,
		pageSize?: number,
		orderByName?: boolean
	) {
		let params: HttpParamsType = {};
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (orderByName) {
			params = {
				...params,
				orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
			};
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<connection[]>(
			apiURL + '/mim/branch/' + branchId + '/connections',
			{
				params: params,
			}
		);
	}

	getCount(branchId: string, filter?: string, viewId?: string) {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/connections/count',
			{
				params: params,
			}
		);
	}

	/**
	 *
	 * @param nodeId Id of node to create a connection-node relationship
	 * @param type 0=primary 1=secondary
	 */
	createNodeRelation(nodeId: string, connectionId?: string) {
		const relation: legacyRelation = {
			typeName: 'Interface Connection Node',
			sideB: nodeId,
			sideA: connectionId,
		};
		return of(relation);
	}

	createTransportTypeRelation(
		transportTypeId: string,
		connectionId?: string
	) {
		const rel: legacyRelation = {
			typeName: 'Interface Connection Transport Type',
			sideA: connectionId,
			sideB: transportTypeId,
		};
		return of(rel);
	}

	createConnection(connection: connection, tx: Required<transaction>) {
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
		const results = _createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.CONNECTION,
			applicability,
			[...nodeRelations, transportTypeRelation, hierarchicalRelation],
			undefined,
			...attributes
		);
		tx = results.tx;

		const createdNodes: createArtifact[] = [];
		//create nodes and then perform mutation
		nodes
			.filter((v) => v.id === '-1')
			.forEach((v) => {
				const nodeResults = this._createNodeWithRelation(
					v,
					results._newArtifact.key,
					tx
				);
				createdNodes.push(nodeResults.node);
				tx = nodeResults.tx;
			});
		return {
			connection: results._newArtifact,
			nodes: createdNodes,
			tx: tx,
		};
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
		const results = _createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.NODE,
			applicability,
			[connectionRel],
			undefined,
			...attributes
		);
		return { node: results._newArtifact, tx: results.tx };
	}

	/**
	 * NOTE: this still creates the transport type and hierarchical relations
	 */
	createConnectionNoRelations(
		connection: connection,
		tx: Required<transaction>,
		key: string
	) {
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
		const results = _createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.CONNECTION,
			applicability,
			[transportTypeRelation, hierarchicalRelation],
			key,
			...attributes
		);
		return results.tx;
	}

	deleteRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(
			this.builder.deleteRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				undefined,
				transaction,
				branchId,
				'Unrelating Connection'
			)
		);
	}

	addRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(
			this.builder.addRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				relation.afterArtifact,
				undefined,
				transaction,
				branchId,
				'Relating Connection'
			)
		);
	}

	performMutation(transaction: legacyTransaction) {
		return this.transactionService.performMutation(transaction);
	}
}
