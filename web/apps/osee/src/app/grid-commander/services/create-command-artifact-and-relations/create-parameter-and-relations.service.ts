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
import { of, switchMap, take } from 'rxjs';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import {
	legacyRelation,
	legacyCreateArtifact,
	legacyTransaction,
	legacyArtifact,
} from '@osee/transactions/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { GCRELATIONTYPEID } from '../../types/grid-commander-constants/gcRelationTypeId.enum';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class CreateParameterAndRelationsService {
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	createParameterArtifact(
		branchId: string,
		parameter: Partial<legacyCreateArtifact & legacyArtifact>,
		parameterType: string,
		transaction?: legacyTransaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				parameter,
				parameterType,
				[],
				transaction,
				branchId,
				`Create ${parameterType} Artifact`,
				key
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	createParameterTypeToCommandRelation(
		commandId: string,
		parameterId: string
	) {
		const relation: legacyRelation = {
			typeId: GCRELATIONTYPEID.DEFAULT_HIERARCHICAL,
			sideA: commandId,
			sideB: parameterId,
		};
		return of(relation);
	}

	establishParameterTypeToCommandRelation(
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
				'Create Parameter Artifact to Command relationship'
			)
		).pipe(
			take(1),
			switchMap((transaction) => this.performMutation(transaction))
		);
	}

	createParameterAndEstablishCommandRelation(
		branchId: string,
		parameter: Partial<legacyCreateArtifact & legacyArtifact>,
		commandId: string,
		parameterType: string = ARTIFACTTYPEIDENUM.PARAMETERSTRING,
		_transaction?: legacyTransaction,
		_key?: string
	) {
		return this.createParameterArtifact(
			branchId,
			parameter,
			parameterType
		).pipe(
			take(1),
			switchMap((transaction) =>
				this.createParameterTypeToCommandRelation(
					commandId,
					transaction.results.ids[0]
				).pipe(
					take(1),
					switchMap((relation) =>
						this.establishParameterTypeToCommandRelation(
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
