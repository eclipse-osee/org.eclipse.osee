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
import { Injectable, inject } from '@angular/core';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import {
	legacyRelation,
	legacyCreateArtifact,
	legacyTransaction,
	legacyArtifact,
} from '@osee/transactions/types';
import { of, switchMap, take, tap } from 'rxjs';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { GCRELATIONTYPEID } from '../../types/grid-commander-constants/gcRelationTypeId.enum';
import { CreateParameterService } from '../create-command-form-services/create-parameter.service';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class CreateCommandandAndRelationsService {
	private createParameterService = inject(CreateParameterService);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	createCommandArtifact(
		branchId: string,
		command: Partial<legacyCreateArtifact & legacyArtifact>,
		transaction?: legacyTransaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				command,
				ARTIFACTTYPEIDENUM.COMMAND,
				[],
				transaction,
				branchId,
				'Create Command Artifact',
				key
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	createCommandToContextRelation(contextId: string, commandId: string) {
		const relation: legacyRelation = {
			typeId: GCRELATIONTYPEID.CONTEXT_TO_COMMAND,
			sideA: contextId,
			sideB: commandId,
		};
		return of(relation);
	}

	establishCommandToContextRelation(
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
				'Create Command to Context relationship'
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	createCommandAndEstablishContextRelation(
		branchId: string,
		command: Partial<legacyCreateArtifact & legacyArtifact>,
		context: string[],
		_transaction?: legacyTransaction,
		_key?: string
	) {
		return this.createCommandArtifact(branchId, command).pipe(
			take(1),
			tap(
				(transaction) =>
					(this.createParameterService.commandArtId =
						transaction.results.ids[0])
			),
			switchMap((transaction) =>
				this.createCommandToContextRelation(
					context[1],
					transaction.results.ids[0]
				).pipe(
					take(1),
					switchMap((relation) =>
						this.establishCommandToContextRelation(
							branchId,
							relation
						)
					)
				)
			)
		);
	}

	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}
}
