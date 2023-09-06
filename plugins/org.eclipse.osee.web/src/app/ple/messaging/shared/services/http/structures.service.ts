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
import { apiURL } from '@osee/environments';
import type { structure } from '../../types/structure';
import {
	ATTRIBUTETYPEIDENUM,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { HttpParamsType, relation, transaction } from '@osee/shared/types';

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
			apiURL + '/mim/branch/' + branchId + '/structures'
		);
	}

	getPaginatedStructuresFilteredByName(
		branchId: string,
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.http.get<Required<structure[]>>(
			apiURL + '/mim/branch/' + branchId + '/structures/name',
			{
				params: {
					count: count,
					pageNum: pageNum,
					name: name,
				},
			}
		);
	}

	getStructuresFilteredByNameCount(branchId: string, name: string) {
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/structures/name/count',
			{
				params: {
					name: name,
				},
			}
		);
	}

	getPaginatedFilteredStructures(
		branchId: string,
		filter: string,
		pageNum: string | number
	) {
		return this.http.get<Required<structure>[]>(
			apiURL + '/mim/branch/' + branchId + '/structures',
			{
				params: {
					count: 3,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
					filter: filter,
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
		viewId: string,
		pageNum: number,
		pageSize: number
	) {
		let params: HttpParamsType = {
			count: pageSize,
			pageNum: pageNum,
		};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
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
				'/structures',
			{
				params: params,
			}
		);
	}

	getFilteredStructuresCount(
		filter: string,
		branchId: string,
		messageId: string,
		subMessageId: string,
		connectionId: string,
		viewId: string
	) {
		let params: HttpParamsType = {
			//
		};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<Required<number>>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/' +
				messageId +
				'/submessages/' +
				subMessageId +
				'/structures/count/',
			{
				params: params,
			}
		);
	}
	getStructure(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		connectionId: string,
		viewId: string,
		filter?: string
	) {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
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
				structureId,
			{ params: params }
		);
	}

	/**
	 *
	 * @param subMessageId undefined is only allowed when creating a new submessage
	 * @param structureId
	 * @param afterArtifact
	 * @returns
	 */
	createSubMessageRelation(
		subMessageId?: string,
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
				relation.afterArtifact,
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
