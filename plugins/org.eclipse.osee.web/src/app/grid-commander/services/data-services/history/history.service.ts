/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { executedCommandHistory } from '../../../types/grid-commander-types/userHistory';
import { of } from 'rxjs';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class HistoryService {
	constructor(
		private transactionService: TransactionService,
		private builder: TransactionBuilderService
	) {}

	createUserHistoryRelation(userId: string, historyId?: string) {
		let relation: relation = {
			typeName: 'User to History',
			sideA: userId,
			sideB: historyId,
		};
		return of(relation);
	}

	createUserToHistoryRelationship(
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
				'Create UserToHistory relation'
			)
		);
	}

	createExecutedCommandHistoryArtifact(
		branchId: string,
		ecHistory: Partial<executedCommandHistory>,
		transaction?: transaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				ecHistory,
				ARTIFACTTYPEIDENUM.EXECUTEDCOMMANDHISTORY,
				[],
				transaction,
				branchId,
				"Create user's command history",
				key
			)
		);
	}

	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}
}
