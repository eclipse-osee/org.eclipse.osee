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
import { Injectable } from '@angular/core';
import { of, switchMap, take } from 'rxjs';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { relation, createArtifact, transaction } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { GCRELATIONTYPEID } from '../../types/grid-commander-constants/gcRelationTypeId.enum';

@Injectable({
	providedIn: 'root',
})
export class CreateParameterAndRelationsService {
	constructor(
		private transactionService: TransactionService,
		private builder: TransactionBuilderService
	) {}

	createParameterArtifact(
		branchId: string,
		parameter: Partial<createArtifact>,
		parameterType: string,
		transaction?: transaction,
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
		let relation: relation = {
			typeId: GCRELATIONTYPEID.DEFAULT_HIERARCHICAL,
			sideA: commandId,
			sideB: parameterId,
		};
		return of(relation);
	}

	establishParameterTypeToCommandRelation(
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
		parameter: Partial<createArtifact>,
		commandId: string,
		parameterType: string = ARTIFACTTYPEIDENUM.PARAMETERSTRING,
		transaction?: transaction,
		key?: string
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

	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}
}
