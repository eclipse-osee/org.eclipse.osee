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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { of } from 'rxjs';
import { apiURL } from '@osee/environments';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import type { enumeration, enumerationSet } from '../../types/enum';
import { TransactionService } from '@osee/transactions/services';
import { createArtifact } from '@osee/transactions/functions';

@Injectable({
	providedIn: 'root',
})
export class EnumerationSetService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	createEnumSet(
		set: enumerationSet,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			enumerations,
			id,
			gammaId,
			applicability,
			...remainingAttributes
		} = set;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.ENUMSET,
			applicability,
			[],
			key,
			...attributes
		);
		return results.tx;
	}

	createEnum(
		enumeration: enumeration,
		tx: Required<transaction>,
		key?: string
	) {
		const { id, gammaId, applicability, ...remainingAttributes } =
			enumeration;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		return createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.ENUM,
			applicability,
			[],
			key,
			...attributes
		).tx;
	}

	createPlatformTypeToEnumSetRelation(sideB?: string, sideA?: string) {
		return of<legacyRelation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideA: sideA,
			sideB: sideB,
		});
	}

	createEnumToEnumSetRelation(sideA?: string, sideB?: string) {
		return of<legacyRelation>({
			typeName: 'Interface Enumeration Definition',
			sideA: sideA,
			sideB: sideB,
		});
	}

	getEnumSets(branchId: string) {
		return this.http.get<enumerationSet[]>(
			apiURL + '/mim/branch/' + branchId + '/enumerations/',
			{
				params: {
					orderBy: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	getEnumSet(branchId: string, platformTypeId: string) {
		return this.http.get<enumerationSet>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/types/' +
				platformTypeId +
				'/enumeration'
		);
	}
	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}

	addRelation(
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
				'Relating EnumSet'
			)
		);
	}
}
