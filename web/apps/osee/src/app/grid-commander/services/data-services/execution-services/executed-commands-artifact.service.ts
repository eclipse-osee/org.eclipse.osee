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
import { executedCommand } from '../../../types/grid-commander-types/executedCommand';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import { of, switchMap, take } from 'rxjs';
import { userHistory } from '../../../types/grid-commander-types/userHistory';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { legacyRelation, legacyTransaction } from '@osee/transactions/types';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class ExecutedCommandsArtifactService {
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	//create an ExecutedCommand Artifact
	createExecutedCommandArtifact(
		branchId: string,
		executedCommand: Partial<executedCommand>,
		transaction?: legacyTransaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				executedCommand,
				ARTIFACTTYPEIDENUM.EXECUTEDCOMMAND,
				[],
				transaction,
				branchId,
				'Create Executed Command',
				key
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	//Create relation of ExecutedCommand Artifact to the User's Executed Command History
	createCommandToHistoryRelation(historyId: string, commandId: string) {
		const relation: legacyRelation = {
			typeId: RELATIONTYPEIDENUM.DEFAULT_HIERARCHICAL,
			sideA: historyId,
			sideB: commandId,
		};
		return of(relation);
	}

	//Add new Relation
	addToExecutedCommandHx(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(
			this.builder.addRelation(
				undefined,
				relation.typeId,
				Array.isArray(relation.sideA)
					? relation.sideA.join(',')
					: relation.sideA,
				Array.isArray(relation.sideB)
					? relation.sideB.join(',')
					: relation.sideB,
				relation.afterArtifact,
				undefined,
				transaction,
				branchId,
				'Create Executed Command to Executed Command History relation'
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	//Calls both create and establish the command history relation
	createCommandAndEstablishHistoryRelationAndAddToHistory(
		branchId: string,
		executedCommand: Partial<executedCommand>,
		userHistory: userHistory,
		_transaction?: legacyTransaction,
		_key?: string
	) {
		return this.createExecutedCommandArtifact(
			branchId,
			executedCommand
		).pipe(
			take(1),
			switchMap((transaction) =>
				this.createCommandToHistoryRelation(
					userHistory.commandHistoryId,
					transaction.results.ids[0]
				).pipe(
					take(1),
					switchMap((relation) =>
						this.addToExecutedCommandHx(branchId, relation)
					)
				)
			)
		);
	}

	modifyExistingCommandArtifact(
		branchId: string,
		commandObj: Partial<executedCommand>
	) {
		return of(
			this.builder.modifyArtifact(
				commandObj,
				undefined,
				branchId,
				'Update executed command attributes'
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	deleteExistingCommandArtifact(branchId: string, artifactId: string) {
		return of(
			this.builder.deleteArtifact(
				artifactId,
				undefined,
				branchId,
				'Delete Command Artifact from data table'
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}
}
