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
import { Injectable } from '@angular/core';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { relation, transaction } from '@osee/shared/types';
import { of } from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	ARTIFACTTYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/shared/types/constants';
import type { enumeration, enumerationSet, enumSet } from '../../types/enum';

@Injectable({
	providedIn: 'root',
})
export class EnumerationSetService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}
	createEnumSet(
		branchId: string,
		type: enumSet | Partial<enumSet>,
		relations: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of<transaction>(
			this.builder.createArtifact(
				type,
				ARTIFACTTYPEIDENUM.ENUMSET,
				relations,
				transaction,
				branchId,
				'Create Enum Set',
				key
			)
		);
	}

	changeEnumSet(
		branchId: string,
		type: Partial<enumSet>,
		transaction?: transaction
	) {
		return of<transaction>(
			this.builder.modifyArtifact(
				type,
				transaction,
				branchId,
				'Change enum set attributes'
			)
		);
	}

	createEnum(
		branchId: string,
		type: enumeration | Partial<enumeration>,
		relations: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of<transaction>(
			this.builder.createArtifact(
				type,
				ARTIFACTTYPEIDENUM.ENUM,
				relations,
				transaction,
				branchId,
				'Create Enum',
				key
			)
		);
	}

	changeEnum(
		branchId: string,
		type: enumeration | Partial<enumeration>,
		transaction?: transaction
	) {
		return of<transaction>(
			this.builder.modifyArtifact(
				type,
				transaction,
				branchId,
				'Change enum attributes'
			)
		);
	}

	createEnumSetToPlatformTypeRelation(sideA?: string) {
		return of<relation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideA: sideA,
		});
	}

	createPlatformTypeToEnumSetRelation(sideB?: string, sideA?: string) {
		return of<relation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideA: sideA,
			sideB: sideB,
		});
	}

	createEnumToEnumSetRelation(sideA?: string, sideB?: string) {
		return of<relation>({
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
	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}

	addRelation(
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
				'Relating EnumSet'
			)
		);
	}
}
