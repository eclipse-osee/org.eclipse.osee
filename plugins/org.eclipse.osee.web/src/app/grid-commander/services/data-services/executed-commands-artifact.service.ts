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
import { executedCommand } from '../../types/grid-commander-types/executedCommand';
import { ARTIFACTTYPEIDENUM } from 'src/app/types/constants/ArtifactTypeId.enum';
import { of, switchMap, take } from 'rxjs';
import { userHistory } from '../../types/grid-commander-types/userHistory';
import { RelationTypeId } from 'src/app/types/constants/RelationTypeId.enum';
import {
	TransactionService,
	TransactionBuilderService,
	transaction,
	relation,
} from '@osee/shared/transactions';

@Injectable({
	providedIn: 'root',
})
export class ExecutedCommandsArtifactService {
	constructor(
		private transactionService: TransactionService,
		private builder: TransactionBuilderService
	) {}

	//create an ExecutedCommand Artifact
	createExecutedCommandArtifact(
		branchId: string,
		executedCommand: Partial<executedCommand>,
		transaction?: transaction,
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
		let relation: relation = {
			typeId: RelationTypeId.DEFAULT_HIERARCHICAL,
			sideA: historyId,
			sideB: commandId,
		};
		return of(relation);
	}

	//Add new Relation
	addToExecutedCommandHx(
		branchId: string,
		relation: relation,
		transaction?: transaction
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
		transaction?: transaction,
		key?: string
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

	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}
}
