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
		const changedAttributeTypeIds =
			this.deriveChangedAttributeTypeIds(body);
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
						changedAttributeTypeIds,
					};
				}
				return null;
			}
		);
	}

	/**
	 * Collects the distinct attribute type ids the transaction sets/adds on existing or new
	 * artifacts, so the acting tab can do targeted refreshes without waiting for the server echo.
	 * Mirrors the server's changedAttributeTypeIds. Deleted attributes carry only an instance id
	 * (no type id) in the body, so they are not represented here; the server echo covers them.
	 */
	private deriveChangedAttributeTypeIds(
		body: transaction | legacyTransaction
	): string[] {
		const typeIds = new Set<string>();
		const collect = (attrs?: { typeId?: string }[]): void => {
			for (const attr of attrs ?? []) {
				if (attr.typeId) {
					typeIds.add(attr.typeId);
				}
			}
		};
		if ('modifyArtifacts' in body) {
			for (const mod of body.modifyArtifacts ?? []) {
				collect(mod.setAttributes);
				collect(mod.addAttributes);
			}
		}
		if ('createArtifacts' in body) {
			for (const create of body.createArtifacts ?? []) {
				collect(create.attributes);
			}
		}
		return [...typeIds];
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
