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
import type { node, nodeData } from '../../types/node';
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
export class NodeService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}

	getNodes(branchId: string) {
		return this.http.get<node[]>(
			apiURL + '/mim/branch/' + branchId + '/nodes/'
		);
	}

	getPaginatedNodes(
		branchId: string,
		pageNum: string | number,
		pageSize: number
	) {
		return this.http.get<node[]>(
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
		pageSize: number
	) {
		return this.http.get<nodeData[]>(
			apiURL + '/mim/branch/' + branchId + '/nodes/name',
			{
				params: {
					name: name,
					count: pageSize,
					pageNum: pageNum,
				},
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
		return this.http.get<node>(
			apiURL + '/mim/branch/' + branchId + '/nodes/' + nodeId
		);
	}

	changeNode(branchId: string, node: Partial<node>) {
		return of(
			this.builder.modifyArtifact(
				node,
				undefined,
				branchId,
				'Update Node'
			)
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

	createNode(
		branchId: string,
		node: Partial<node | nodeData>,
		relations?: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of(
			this.builder.createArtifact(
				node,
				ARTIFACTTYPEIDENUM.NODE,
				relations || [],
				transaction,
				branchId,
				'Create Node',
				key
			)
		);
	}

	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}
}
