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
import type { subMessage } from '../../types/sub-messages';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { TransactionService } from '@osee/transactions/services';
import { createArtifact } from '@osee/transactions/functions';

@Injectable({
	providedIn: 'root',
})
export class SubMessagesService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	getSubMessage(
		branchId: string,
		connectionId: string,
		messageId: string,
		subMessageId: string
	) {
		return this.http.get<subMessage>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/' +
				messageId +
				'/submessages/' +
				subMessageId
		);
	}

	getPaginatedFilteredSubMessages(
		branchId: string | number,
		filter: string,
		pageNum: string | number
	) {
		return this.http.get<Required<subMessage>[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/submessages/filter/' +
				filter,
			{
				params: {
					pageNum: pageNum,
					count: 3,
					orderByAttribute: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	getPaginatedSubmessagesByName(
		branchId: string,
		name: string,
		count: number,
		pageNum: string | number
	) {
		return this.http.get<Required<subMessage>[]>(
			apiURL + '/mim/branch/' + branchId + '/submessages/filter/name',
			{
				params: {
					name: name,
					pageNum: pageNum,
					count: count,
				},
			}
		);
	}

	getSubmessagesByNameCount(branchId: string, name: string) {
		return this.http.get<number>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/submessages/filter/name/count',
			{
				params: {
					name: name,
				},
			}
		);
	}

	createMessageRelation(
		messageId: string,
		subMessageId?: string,
		afterArtifact?: string
	) {
		const relation: legacyRelation = {
			typeName: 'Interface Message SubMessage Content',
			sideA: messageId,
			sideB: subMessageId,
			afterArtifact: afterArtifact || 'end',
		};
		return of(relation);
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
				`Relating SubMessage ${relation.sideB} to ${relation.sideA}`
			)
		);
	}
	deleteRelation(branchId: string, relation: legacyRelation) {
		return of(
			this.builder.deleteRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				undefined,
				undefined,
				branchId,
				`Unrelating submessage ${relation.sideB} from ${relation.sideA}`
			)
		);
	}
	createSubMessage(
		submessage: subMessage,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			id,
			gammaId,
			applicability,
			autogenerated,
			...remainingAttributes
		} = submessage;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.SUBMESSAGE,
			applicability,
			[],
			key,
			...attributes
		);
		return results.tx;
	}
	deleteSubMessage(branchId: string, submessageId: string) {
		return of(
			this.builder.deleteArtifact(
				submessageId,
				undefined,
				branchId,
				`Deleting Submessage ${submessageId}`
			)
		);
	}
	performMutation(
		_branchId: string,
		_connectionId: string,
		_messageId: string,
		body: legacyTransaction
	) {
		return this.transactionService.performMutation(body);
	}
}
