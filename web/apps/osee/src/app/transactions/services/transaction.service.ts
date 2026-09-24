/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { apiURL } from '@osee/environments';
import {
	MutationService,
	artifactChangeType,
} from '@osee/shared/services/network';
import {
	legacyTransaction,
	transaction,
	transactionResult,
} from '@osee/transactions/types';

@Injectable({
	providedIn: 'root',
})
export class TransactionService {
	private http = inject(HttpClient);
	private mutation = inject(MutationService);

	performMutation(body: transaction | legacyTransaction) {
		const changeTypes = this.deriveChangeTypes(body);
		return this.mutation.mutateAndNotify(
			this.http.post<transactionResult>(apiURL + '/orcs/txs', body),
			(result) => {
				const txId = result.tx?.id;
				const artifactIds = result.results?.ids ?? [];
				if (
					!!txId &&
					txId !== '0' &&
					txId !== '-1' &&
					(result.failedGammas?.length ?? 0) === 0 &&
					artifactIds.length > 0
				) {
					return {
						type: 'artifact',
						branchId: body.branch,
						artifactIds,
						transactionId: txId,
						changeTypes,
					};
				}
				return null;
			}
		);
	}

	private deriveChangeTypes(
		body: transaction | legacyTransaction
	): artifactChangeType[] {
		const types: artifactChangeType[] = [];
		if ('createArtifacts' in body && body.createArtifacts?.length) {
			types.push('artifact_created');
		}
		if ('deleteArtifacts' in body && body.deleteArtifacts?.length) {
			types.push('artifact_deleted');
		}
		if ('addRelations' in body && body.addRelations?.length) {
			types.push('relation_added');
		}
		if ('deleteRelations' in body && body.deleteRelations?.length) {
			types.push('relation_deleted');
		}
		if (types.length === 0) {
			types.push('attribute_modified');
		}
		if (
			types.includes('artifact_created') &&
			'createArtifacts' in body &&
			body.createArtifacts?.some((a) => a.relations?.length)
		) {
			if (!types.includes('relation_added')) {
				types.push('relation_added');
			}
		}
		return types;
	}
}
