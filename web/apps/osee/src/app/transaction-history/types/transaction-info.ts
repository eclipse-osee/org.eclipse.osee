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
import { transactionToken } from '@osee/transactions/types';
export type transactionInfo = {
	txId: transactionToken;
	branchUuid: number;
	txType: {
		id: string;
		name: string;
		baseline: boolean;
		idString: string;
		idIntValue: number;
	};
	comment: string;
	author: {
		id: string;
		name: string;
		userId: string;
		active: boolean;
		email: string;
		loginIds: unknown[];
		roles: unknown[];
	};
	timeStamp: string;
	commitArt: string;
};
