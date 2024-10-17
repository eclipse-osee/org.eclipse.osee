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

import { Injectable, inject } from '@angular/core';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { UiService } from '@osee/shared/services';
import { applic } from '@osee/applicability/types';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';
import {
	createArtifact,
	modifyArtifact,
	deleteArtifact,
	deleteRelation,
	deleteRelations,
	addRelation,
	addRelations,
} from '@osee/transactions/operators';
import { createArtifact as _createArtifact } from '@osee/transactions/functions';
import {
	relation,
	modifyRelation,
	transaction,
	transactionResult,
} from '@osee/transactions/types';
import { take, map, pipe, Observable, switchMap, tap, filter } from 'rxjs';
import { TransactionService } from './transaction.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class CurrentTransactionService {
	private _uiService = inject(UiService);
	private _txService = inject(TransactionService);

	//note: anything using _id should be assumed to be non-reactive(i.e. called once)
	private _id = toSignal(
		this._uiService.id.pipe(
			filter((v) => v !== '' && v !== '0' && v !== '-1')
		),
		{ initialValue: '' }
	);

	createTransaction(comment: string): Required<transaction> {
		return {
			branch: this._id(),
			txComment: comment,
			createArtifacts: [],
			modifyArtifacts: [],
			deleteArtifacts: [],
			deleteRelations: [],
			addRelations: [],
		};
	}

	createTx(comment: string): Observable<Required<transaction>> {
		return this._uiService.id.pipe(
			take(1),
			map((id) => {
				return {
					branch: id,
					txComment: comment,
					createArtifacts: [],
					modifyArtifacts: [],
					deleteArtifacts: [],
					deleteRelations: [],
					addRelations: [],
				};
			})
		);
	}

	createArtifact(
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) {
		const tx = this.createTransaction(comment);
		return _createArtifact(
			tx,
			artTypeId,
			applicability,
			relations,
			undefined,
			...attributes
		);
	}

	createArt = (
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) => {
		return this.createTx(comment).pipe(
			createArtifact(artTypeId, applicability, relations, ...attributes)
		);
	};

	createArtifactAndMutate = (
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) => {
		return this.createArt(
			comment,
			artTypeId,
			applicability,
			relations,
			...attributes
		).pipe(this.performMutation());
	};

	modifyArt = (
		comment: string,
		artId: `${number}`,
		applicability: applic,
		attrConfig: {
			set?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
			add?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
			delete?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
		}
	) => {
		return this.createTx(comment).pipe(
			modifyArtifact(artId, applicability, attrConfig)
		);
	};

	modifyArtifactAndMutate = (
		comment: string,
		artId: `${number}`,
		applicability: applic,
		attrConfig: {
			set?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
			add?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
			delete?: attribute<
				string | number | boolean | unknown[] | unknown,
				ATTRIBUTETYPEID
			>[];
		}
	) => {
		return this.modifyArt(comment, artId, applicability, attrConfig).pipe(
			this.performMutation()
		);
	};

	deleteArt = (comment: string, artId: `${number}`) => {
		return this.createTx(comment).pipe(deleteArtifact(artId));
	};

	deleteRelation = (comment: string, relation: modifyRelation) => {
		return this.createTx(comment).pipe(deleteRelation(relation));
	};
	deleteRelations = (comment: string, relations: modifyRelation[]) => {
		return this.createTx(comment).pipe(deleteRelations(relations));
	};

	deleteRelationAndMutate = (comment: string, relation: modifyRelation) => {
		return this.deleteRelation(comment, relation).pipe(
			this.performMutation()
		);
	};

	deleteRelationsAndMutate = (
		comment: string,
		relations: modifyRelation[]
	) => {
		return this.deleteRelations(comment, relations).pipe(
			this.performMutation()
		);
	};

	deleteArtifactAndMutate = (comment: string, artId: `${number}`) => {
		return this.deleteArt(comment, artId).pipe(this.performMutation());
	};

	addRelation = (comment: string, relation: modifyRelation) => {
		return this.createTx(comment).pipe(addRelation(relation));
	};

	addRelationAndMutate = (comment: string, relation: modifyRelation) => {
		return this.addRelation(comment, relation).pipe(this.performMutation());
	};

	addRelations = (comment: string, relations: modifyRelation[]) => {
		return this.createTx(comment).pipe(addRelations(relations));
	};

	addRelationsAndMutate = (comment: string, relations: modifyRelation[]) => {
		return this.addRelations(comment, relations).pipe(
			this.performMutation()
		);
	};

	performMutation() {
		return pipe<
			Observable<Required<transaction>>,
			Observable<Required<transactionResult>>,
			Observable<Required<transactionResult>>
		>(
			switchMap((tx) => this._txService.performMutation(tx)),
			tap(() => (this._uiService.updated = true))
		);
	}
}
