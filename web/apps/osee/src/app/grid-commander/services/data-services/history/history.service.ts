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
import { Injectable, inject } from '@angular/core';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { executedCommandHistory } from '../../../types/grid-commander-types/userHistory';
import { of } from 'rxjs';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { legacyRelation, legacyTransaction } from '@osee/transactions/types';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class HistoryService {
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	createUserHistoryRelation(userId: string, historyId?: string) {
		const relation: legacyRelation = {
			typeName: 'User to History',
			sideA: userId,
			sideB: historyId,
		};
		return of(relation);
	}

	createUserToHistoryRelationship(
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
				'Create UserToHistory relation'
			)
		);
	}

	createExecutedCommandHistoryArtifact(
		branchId: string,
		ecHistory: Partial<executedCommandHistory>,
		transaction?: legacyTransaction,
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

	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}
}
