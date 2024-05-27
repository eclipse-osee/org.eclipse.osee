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
import { of } from 'rxjs';
import { apiURL } from '@osee/environments';
import type { structure } from '../../types/structure';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import type { HttpParamsType } from '@osee/shared/types';
import type {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { TransactionService } from '@osee/transactions/services';
import { createArtifact } from '@osee/transactions/functions';

@Injectable({
	providedIn: 'root',
})
export class StructuresService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	getStructures(branchId: string) {
		return this.http.get<structure[]>(
			apiURL + '/mim/branch/' + branchId + '/structures'
		);
	}

	getPaginatedStructuresFilteredByName(
		branchId: string,
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.http.get<structure[]>(
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
		return this.http.get<structure[]>(
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
		return this.http.get<structure[]>(
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
		return this.http.get<structure>(
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
		const relation: legacyRelation = {
			typeName: 'Interface SubMessage Content',
			sideA: subMessageId,
			sideB: structureId,
			afterArtifact: afterArtifact || 'end',
		};
		return of(relation);
	}
	addNewStructureToTransaction(
		body: structure,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			id,
			gammaId,
			elements,
			numElements,
			sizeInBytes,
			bytesPerSecondMinimum,
			bytesPerSecondMaximum,
			incorrectlySized,
			autogenerated,
			applicability,
			added,
			deleted,
			hasElementChanges,
			changes,
			...remainingAttributes
		} = body;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.STRUCTURE,
			applicability,
			[],
			key,
			...attributes
		);
		return results.tx;
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
				'Relating SubMessage'
			)
		);
	}
	performMutation(transaction: legacyTransaction) {
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
