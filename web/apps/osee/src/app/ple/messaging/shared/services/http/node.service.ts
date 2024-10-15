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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { apiURL } from '@osee/environments';
import type { nodeData } from '@osee/messaging/shared/types';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { HttpParamsType } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { createArtifact } from '@osee/transactions/functions';
import { TransactionService } from '@osee/transactions/services';
import { legacyTransaction, transaction } from '@osee/transactions/types';
import { of } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class NodeService {
	private http = inject(HttpClient);
	private builder = inject(TransactionBuilderService);

	private transactionService = inject(TransactionService);

	getNodes(branchId: string) {
		return this.http.get<nodeData[]>(
			apiURL + '/mim/branch/' + branchId + '/nodes/'
		);
	}

	getPaginatedNodes(
		branchId: string,
		pageNum: string | number,
		pageSize: number
	) {
		return this.http.get<nodeData[]>(
			apiURL + '/mim/branch/' + branchId + '/nodes/',
			{
				params: {
					count: pageSize,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	getPaginatedNodesByName(
		branchId: string,
		name: string,
		pageNum: string | number,
		pageSize: number,
		connectionId: `${number}`
	) {
		let params: HttpParamsType = {
			name: name,
			count: pageSize,
			pageNum: pageNum,
		};
		if (connectionId !== '-1') {
			params = { ...params, connectionId };
		}
		return this.http.get<nodeData[]>(
			apiURL + '/mim/branch/' + branchId + '/nodes/name',
			{
				params,
			}
		);
	}

	getNodesByNameCount(branchId: string, name: string) {
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/nodes/name/count',
			{
				params: {
					name: name,
				},
			}
		);
	}

	getNode(branchId: string, nodeId: string) {
		return this.http.get<nodeData>(
			apiURL + '/mim/branch/' + branchId + '/nodes/' + nodeId
		);
	}

	deleteArtifact(branchId: string, artId: string) {
		return of(
			this.builder.deleteArtifact(
				artId,
				undefined,
				branchId,
				'Delete Node'
			)
		);
	}

	addNewNodeToTransaction(
		node: nodeData,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			id,
			gammaId,
			applicability,
			deleted,
			added,
			changes,
			...remainingAttributes
		} = node;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '');
		return createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.NODE,
			applicability,
			[],
			key,
			...attributes
		).tx;
	}

	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}
}
