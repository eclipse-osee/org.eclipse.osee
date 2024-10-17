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
import { inject, Injectable } from '@angular/core';
import { apiURL } from '@osee/environments';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { HttpParamsType } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { createArtifact } from '@osee/transactions/functions';
import { TransactionService } from '@osee/transactions/services';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import type { connection } from '../../types/connection';
import type { message } from '../../types/messages';

@Injectable({
	providedIn: 'root',
})
export class MessagesService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	/**
	 * Gets an array of messages based on a filter condition
	 * @param filter parameter to filter out messages that don't meet criteria
	 * @param branchId branch to look for messages on
	 * @returns Observable of an array of messages matching filter condition
	 */
	getFilteredMessages(
		filter: string,
		branchId: string,
		connectionId: string,
		viewId: string,
		pageNum?: number,
		pageSize?: number
	): Observable<message[]> {
		let params: HttpParamsType = {};
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<message[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages',
			{
				params: params,
			}
		);
	}

	getFilteredMessagesCount(
		filter: string,
		branchId: string,
		connectionId: string,
		viewId: string
	): Observable<number> {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/count',
			{
				params: params,
			}
		);
	}

	/**
	 * Finds a specific message
	 * @param branchId branch to look for contents on
	 * @param messageId id of message to find
	 * @returns message contents, if found
	 */
	getMessage(
		branchId: string,
		connectionId: string,
		messageId: string,
		viewId: string
	): Observable<message> {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		return this.http.get<message>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/connections/' +
				connectionId +
				'/messages/' +
				messageId,
			{ params: params }
		);
	}

	getConnection(branchId: string, connectionId: string) {
		return this.http.get<connection>(
			apiURL + '/mim/branch/' + branchId + '/connections/' + connectionId
		);
	}

	getConnectionName(branchId: string, connectionId: string) {
		return this.getConnection(branchId, connectionId).pipe(
			map((x) => x.name)
		);
	}

	createConnectionRelation(
		connectionId: string,
		messageId?: string,
		afterArtifactId?: string
	) {
		const relation: legacyRelation = {
			typeName: 'Interface Connection Message',
			sideA: connectionId,
			sideB: messageId,
			afterArtifact: afterArtifactId || 'end',
		};
		return of(relation);
	}

	/**
	 *
	 * @param messageId
	 * @param nodeId
	 * @param type - true = publusher node, false = subscriber node
	 * @returns
	 */
	createMessageNodeRelation(
		messageId: string,
		nodeId: string,
		type?: boolean
	) {
		const relation: legacyRelation = {
			typeName: type
				? 'Interface Message Publisher Node'
				: 'Interface Message Subscriber Node',
			sideA: messageId,
			sideB: nodeId,
		};
		return of(relation);
	}

	createSubMessageRelation(subMessageId?: string, afterArtifact?: string) {
		const relation: legacyRelation = {
			typeName: 'Interface Message SubMessage Content',
			sideB: subMessageId,
			afterArtifact: afterArtifact || 'end',
		};
		return of(relation);
	}

	addNewMessageToTransaction(
		message: message,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			id,
			gammaId,
			applicability,
			publisherNodes,
			subscriberNodes,
			subMessages,
			added,
			deleted,
			changes,
			hasSubMessageChanges,
			...remainingAttributes
		} = message;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.MESSAGE,
			applicability,
			[],
			key,
			...attributes
		);
		return results.tx;
	}

	deleteMessage(
		branchId: string,
		messageId: string,
		transaction?: legacyTransaction
	) {
		return of(
			this.builder.deleteArtifact(
				messageId,
				transaction,
				branchId,
				'Deleting message'
			)
		);
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
				'Relating Message'
			)
		);
	}

	deleteRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(
			this.builder.deleteRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				undefined,
				transaction,
				branchId,
				'Removing message'
			)
		);
	}

	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}
}
