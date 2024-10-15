/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { ConnectionService } from '@osee/messaging/shared/services';
import { connection } from '@osee/messaging/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/transactions/testing';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { of } from 'rxjs';
import { connectionMock } from './connection.response.mock';

export const connectionServiceMock: Partial<ConnectionService> = {
	getConnections(branchId: string) {
		return of([connectionMock]);
	},
	createConnection(connection: connection, tx: Required<transaction>) {
		return {
			tx: tx,
			nodes: [],
			connection: {
				typeId: ARTIFACTTYPEIDENUM.CONNECTION,
				name: connection.name.value,
				applicabilityId: connection.applicability.id,
				relations: [],
				key: '',
				attributes: [],
			},
		};
	},
	createNodeRelation(nodeId: string) {
		return of({
			typeName: 'Interface Connection Node',
			sideB: nodeId,
		});
	},
	createTransportTypeRelation(transportTypeId, connectionId?: string) {
		return of({
			typeName: 'Interface Connection Transport Type',
			sideA: connectionId,
			sideB: transportTypeId,
		});
	},
	performMutation(transaction: legacyTransaction) {
		return of(transactionResultMock);
	},
	deleteRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(transactionMock);
	},
};
