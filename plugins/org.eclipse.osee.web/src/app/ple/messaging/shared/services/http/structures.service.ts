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
import { of } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import type { structure } from '../../types/structure';
import {
	ATTRIBUTETYPEIDENUM,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class StructuresService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}

	getStructures(branchId: string) {
		return this.http.get<Required<structure>[]>(
			apiURL + '/mim/branch/' + branchId + '/structures/filter'
		);
	}

	getPaginatedFilteredStructures(
		branchId: string,
		filter: string,
		pageNum: string | number
	) {
		return this.http.get<Required<structure>[]>(
			apiURL + '/mim/branch/' + branchId + '/structures/filter/' + filter,
			{
				params: {
					count: 3,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}
	getFilteredStructures(
		filter: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		pageNum: number,
		pageSize: number
	) {
		return this.http.get<Required<structure>[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/' +
				messageId +
				'/submessages/' +
				subMessageId +
				'/structures/filter/' +
				filter,
			{
				params: {
					count: pageSize,
					pageNum: pageNum,
				},
			}
		);
	}
	getStructure(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		connectionId: string,
		filter?: string
	) {
		return this.http.get<Required<structure>>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/' +
				messageId +
				'/submessages/' +
				subMessageId +
				'/structures/' +
				structureId +
				(filter !== undefined ? '/' + filter : '')
		);
	}

	createSubMessageRelation(
		subMessageId: string,
		structureId?: string,
		afterArtifact?: string
	) {
		let relation: relation = {
			typeName: 'Interface SubMessage Content',
			sideA: subMessageId,
			sideB: structureId,
			afterArtifact: afterArtifact || 'end',
		};
		return of(relation);
	}
	createStructure(
		body: Partial<structure>,
		branchId: string,
		relations: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				body,
				ARTIFACTTYPEIDENUM.STRUCTURE,
				relations,
				transaction,
				branchId,
				'Create Structure',
				key
			)
		);
	}
	changeStructure(body: Partial<structure>, branchId: string) {
		return of(
			this.builder.modifyArtifact(
				body,
				undefined,
				branchId,
				'Change Structure'
			)
		);
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
				undefined,
				transaction,
				branchId,
				'Relating SubMessage'
			)
		);
	}
	performMutation(transaction: transaction) {
		return this.transactionService.performMutation(transaction);
	}
	deleteSubmessageRelation(
		branchId: string,
		submessageId: string,
		structureId: string
	) {
		return of(
			this.builder.deleteRelation(
				'Interface SubMessage Content',
				undefined,
				submessageId,
				structureId,
				undefined,
				undefined,
				branchId,
				'Unrelating submessage from message'
			)
		);
	}
	deleteStructure(branchId: string, structureId: string) {
		return of(
			this.builder.deleteArtifact(
				structureId,
				undefined,
				branchId,
				'Deleting structure'
			)
		);
	}
}
