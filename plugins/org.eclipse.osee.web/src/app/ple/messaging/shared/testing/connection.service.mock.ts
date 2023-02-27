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
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';
import { relation, transaction } from '@osee/shared/types';
import { iif, of } from 'rxjs';
import { connectionMock } from './connection.response.mock';

export const connectionServiceMock: Partial<ConnectionService> = {
	getConnections(branchId: string) {
		return of([connectionMock]);
	},
	createConnection(
		branchId: string,
		connection: connection,
		relations: relation[]
	) {
		return of(transactionMock);
	},
	createNodeRelation(nodeId: string, type: boolean) {
		return iif(
			() => type,
			of({
				typeName: 'Interface Connection Secondary Node',
				sideB: nodeId,
			}),
			of({
				typeName: 'Interface Connection Primary Node',
				sideB: nodeId,
			})
		);
	},
	createTransportTypeRelation(transportTypeId, connectionId?: string) {
		return of({
			typeName: 'Interface Connection Transport Type',
			sideA: connectionId,
			sideB: transportTypeId,
		});
	},
	changeConnection(branchId: string, connection: Partial<connection>) {
		return of(transactionMock);
	},
	performMutation(transaction: transaction) {
		return of(transactionResultMock);
	},
	deleteRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
};
