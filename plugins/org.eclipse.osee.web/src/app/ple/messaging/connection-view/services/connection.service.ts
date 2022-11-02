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
import { relation, transaction } from '../../../../transactions/transaction';
import { TransactionBuilderService } from '../../../../transactions/transaction-builder.service';
import { apiURL } from 'src/environments/environment';
import { OSEEWriteApiResponse } from '../../shared/types/ApiWriteResponse';
import { connection, _newConnection } from '../../shared/types/connection';
import { ARTIFACTTYPEID } from '../../../../types/constants/ArtifactTypeId.enum';
import { TransactionService } from '../../../../transactions/transaction.service';

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

	/**
	 *
	 * @param nodeId Id of node to create a connection-node relationship
	 * @param type 0=primary 1=secondary
	 */
	createNodeRelation(nodeId: string, type: boolean, connectionId?: string) {
		if (type) {
			let relation: relation = {
				typeName: 'Interface Connection Secondary Node',
				sideB: nodeId,
				sideA: connectionId,
			};
			return of(relation);
		}
		let relation: relation = {
			typeName: 'Interface Connection Primary Node',
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
		relations: relation[]
	) {
		return of(
			this.builder.createArtifact(
				connection,
				ARTIFACTTYPEID.CONNECTION,
				relations,
				undefined,
				branchId,
				'Create Connection and Relate to Node(s): ' +
					relations[1].sideB +
					' , ' +
					relations[2].sideB +
					' and Relate to Transport Type: ' +
					relations[0].sideB
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
				ARTIFACTTYPEID.CONNECTION,
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
