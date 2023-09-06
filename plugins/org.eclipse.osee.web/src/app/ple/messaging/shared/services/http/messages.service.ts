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
import { combineLatest, from, Observable, of } from 'rxjs';
import { apiURL } from '@osee/environments';
import type { message } from '../../types/messages';
import type { connection } from '../../types/connection';
import type { ConnectionNode } from '../../types/connection-nodes';
import { concatMap, map, reduce, switchMap } from 'rxjs/operators';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { HttpParamsType, relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class MessagesService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}

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

	getConnectionNodes(branchId: string, connectionId: string) {
		return this.http.get<ConnectionNode[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/nodes/connection/' +
				connectionId
		);
	}

	createConnectionRelation(connectionId: string, messageId?: string) {
		let relation: relation = {
			typeName: 'Interface Connection Message',
			sideA: connectionId,
			sideB: messageId,
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
		let relation: relation = {
			typeName: type
				? 'Interface Message Publisher Node'
				: 'Interface Message Subscriber Node',
			sideA: messageId,
			sideB: nodeId,
		};
		return of(relation);
	}

	createSubMessageRelation(subMessageId?: string, afterArtifact?: string) {
		let relation: relation = {
			typeName: 'Interface Message SubMessage Content',
			sideB: subMessageId,
			afterArtifact: afterArtifact || 'end',
		};
		return of(relation);
	}

	changeMessage(branchId: string, message: Partial<message>) {
		return of(
			this.builder.modifyArtifact(
				message,
				undefined,
				branchId,
				'Update Message'
			)
		);
	}

	createMessage(
		branchId: string,
		message: Partial<message>,
		relations: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				message,
				ARTIFACTTYPEIDENUM.MESSAGE,
				relations,
				transaction,
				branchId,
				`Creating message ${message.name}`,
				key
			)
		);
	}

	deleteMessage(
		branchId: string,
		messageId: string,
		transaction?: transaction
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
				'Relating Message'
			)
		);
	}

	deleteRelation(
		branchId: string,
		relation: relation,
		transaction?: transaction
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

	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}
}
