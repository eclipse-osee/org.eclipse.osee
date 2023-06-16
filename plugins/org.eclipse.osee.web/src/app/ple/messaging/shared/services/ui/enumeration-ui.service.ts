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
import { Injectable } from '@angular/core';
import type {
	enumSet,
	enumeration,
	enumerationSet,
} from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
	transaction,
} from '@osee/shared/types';
import { iif, Observable, of } from 'rxjs';
import { share, switchMap, shareReplay, take, map } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { EnumerationSetService } from '../http/enumeration-set.service';

@Injectable({
	providedIn: 'root',
})
export class EnumerationUIService {
	private _enumSets = this.ui.id.pipe(
		share(),
		switchMap((id) => this.enumSetService.getEnumSets(id).pipe(share())),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	constructor(
		private enumSetService: EnumerationSetService,
		private ui: UiService
	) {}
	createEnumSetToPlatformTypeRelation(sideA?: string) {
		return this.enumSetService.createEnumSetToPlatformTypeRelation(sideA);
	}
	createEnumSet(
		branchId: string,
		type: enumSet | Partial<enumSet>,
		relations: relation[],
		transaction?: transaction
	) {
		return this.enumSetService.createEnumSet(
			branchId,
			type,
			relations,
			transaction
		);
	}
	createPlatformTypeToEnumSetRelation(sideB?: string) {
		return this.enumSetService.createPlatformTypeToEnumSetRelation(sideB);
	}
	createEnumToEnumSetRelation(sideA?: string) {
		return this.enumSetService.createEnumToEnumSetRelation(sideA);
	}
	createEnum(
		branchId: string,
		type: enumeration | Partial<enumeration>,
		relations: relation[],
		transaction?: transaction
	) {
		return this.enumSetService.createEnum(
			branchId,
			type,
			relations,
			transaction
		);
	}
	// type inference is failing here
	get enumSets(): Observable<enumerationSet[]> {
		return this._enumSets;
	}
	getEnumSet(platformTypeId: string) {
		return this.ui.id.pipe(
			take(1),
			switchMap((branchId) =>
				this.enumSetService.getEnumSet(branchId, platformTypeId)
			)
		);
	}
	changeEnumSet(dialogResponse: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		return this.ui.id.pipe(
			take(1),
			map((id) => {
				const tx: transaction = {
					branch: id,
					txComment: 'Updating enumeration',
					createArtifacts: dialogResponse.createArtifacts,
					modifyArtifacts: dialogResponse.modifyArtifacts,
					deleteRelations: dialogResponse.deleteRelations,
				};
				return tx;
			}),
			switchMap((transaction) =>
				this.enumSetService.performMutation(transaction)
			)
		);
	}

	private createOrChangeEnum(
		branchId: string,
		enumSetId: string,
		value: enumeration
	) {
		return iif(
			() => 'id' in value && value.id !== '',
			this.enumSetService.changeEnum(branchId, value),
			this.enumSetService
				.createEnumToEnumSetRelation(enumSetId)
				.pipe(
					switchMap((relation) =>
						this.enumSetService.createEnum(branchId, value, [
							relation,
						])
					)
				)
		);
	}
	private _mergeTransactions(transactions: transaction[]) {
		let currentTransaction: transaction = {
			branch: '',
			txComment: '',
			createArtifacts: [],
			modifyArtifacts: [],
		};
		if (transactions?.[0]) {
			currentTransaction = {
				branch: transactions[0].branch,
				txComment: transactions[0].branch,
				createArtifacts: transactions[0].createArtifacts || [],
				modifyArtifacts: transactions[0].modifyArtifacts || [],
				deleteArtifacts: transactions[0].deleteArtifacts || [],
				deleteRelations: transactions[0].deleteRelations || [],
			};
			transactions.shift();
		}
		transactions.forEach((transaction) => {
			currentTransaction.createArtifacts?.push(
				...(transaction?.createArtifacts || [])
			);
			currentTransaction.modifyArtifacts?.push(
				...(transaction?.modifyArtifacts || [])
			);
			currentTransaction.deleteArtifacts?.push(
				...(transaction?.deleteArtifacts || [])
			);
			currentTransaction.deleteRelations?.push(
				...(transaction?.deleteRelations || [])
			);
		});
		return of<transaction>(currentTransaction);
	}
}
