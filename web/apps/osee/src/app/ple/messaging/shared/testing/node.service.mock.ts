/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { NodeService } from '@osee/messaging/shared/services';
import { nodeData } from '@osee/messaging/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/transactions/testing';
import { legacyTransaction, transaction } from '@osee/transactions/types';
import { of } from 'rxjs';
import { nodesMock } from './nodes-response.mock';

export const nodeServiceMock: Partial<NodeService> = {
	getNodes(branchId: string) {
		return of(nodesMock);
	},
	getNode(branchId: string, nodeId: string) {
		return of(nodesMock[0]);
	},
	addNewNodeToTransaction(
		node: nodeData,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	performMutation(transaction: legacyTransaction) {
		return of(transactionResultMock);
	},
	deleteArtifact(branchId: string, artId: string) {
		return of(transactionMock);
	},
};
