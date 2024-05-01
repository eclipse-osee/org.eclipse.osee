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
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { apiURL } from '@osee/environments';
import type { connection, _newConnection } from '../../types/connection';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { HttpParamsType, relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ConnectionService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}

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
		let relation: relation = {
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
		const rel: relation = {
			typeName: 'Interface Connection Transport Type',
			sideA: connectionId,
			sideB: transportTypeId,
		};
		return of(rel);
	}

	createConnection(
		branchId: string,
		connection: _newConnection,
		relations: relation[],
		transaction?: transaction
	) {
		return of(
			this.builder.createArtifact(
				connection,
				ARTIFACTTYPEIDENUM.CONNECTION,
				relations,
				transaction,
				branchId,
				'Create Connection and Relate to Nodes and Transport Type'
			)
		);
	}

	createConnectionNoRelations(
		branchId: string,
		connection: _newConnection,
		transaction?: transaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				connection,
				ARTIFACTTYPEIDENUM.CONNECTION,
				[],
				transaction,
				branchId,
				'Create Connection',
				key
			)
		);
	}

	changeConnection(branchId: string, connection: Partial<connection>) {
		return of(
			this.builder.modifyArtifact(
				connection,
				undefined,
				branchId,
				'Change connection attributes'
			)
		);
	}

	deleteRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
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
		relation: relation,
		transaction?: transaction
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

	performMutation(transaction: transaction) {
		return this.transactionService.performMutation(transaction);
	}
}
