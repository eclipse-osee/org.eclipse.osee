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
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';
import { CurrentTransactionService } from '@osee/transactions/services';
import { transactionResultMock, txMock } from '@osee/transactions/testing';
import {
	transaction,
	relation,
	createArtifact,
	transactionResult,
	modifyRelation,
} from '@osee/transactions/types';
import { Observable, UnaryFunction, of, pipe, switchMap, tap } from 'rxjs';
import {
	createArtifact as __createArtifact,
	modifyArtifact,
	deleteArtifact,
	deleteRelation,
	deleteRelations,
	addRelation,
	addRelations,
} from '@osee/transactions/operators';
import { createArtifact as _createArtifact } from '@osee/transactions/functions';
export const currentTransactionServiceMock: Partial<CurrentTransactionService> =
	{
		createTransaction: function (comment: string): Required<transaction> {
			return txMock;
		},
		createTx: function (
			comment: string
		): Observable<Required<transaction>> {
			//@ts-ignore
			return of(this.createTransaction(comment));
		},
		createArtifact: function (
			comment: string,
			artTypeId: ARTIFACTTYPEID,
			applicability: applic,
			relations: relation[],
			...attributes: attribute<unknown, ATTRIBUTETYPEID>[]
		): { tx: Required<transaction>; _newArtifact: createArtifact } {
			//@ts-ignore
			let tx = this.createTransaction(comment);
			return _createArtifact(
				tx,
				artTypeId,
				applicability,
				relations,
				undefined,
				...attributes
			);
		},
		performMutation: function (): UnaryFunction<
			Observable<Required<transaction>>,
			Observable<Required<transactionResult>>
		> {
			return pipe(
				switchMap((_) => of(transactionResultMock)),
				tap((_) => {})
			);
		},
		createArt: function (
			comment: string,
			artTypeId: ARTIFACTTYPEID,
			applicability: applic,
			relations: relation[],
			...attributes: attribute<
				string | number | boolean | any[] | unknown,
				ATTRIBUTETYPEID
			>[]
		) {
			//@ts-ignore
			return this.createTx(comment).pipe(
				__createArtifact(
					artTypeId,
					applicability,
					relations,
					...attributes
				)
			);
		},
		createArtifactAndMutate: function (
			comment: string,
			artTypeId: ARTIFACTTYPEID,
			applicability: applic,
			relations: relation[],
			...attributes: attribute<
				string | number | boolean | any[] | unknown,
				ATTRIBUTETYPEID
			>[]
		) {
			//@ts-ignore
			return this.createArt(
				comment,
				artTypeId,
				applicability,
				relations,
				...attributes
			).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
		modifyArt: function (
			comment: string,
			artId: `${number}`,
			applicability: applic,
			attrConfig: {
				set?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
				add?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
				delete?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
			}
		) {
			//@ts-ignore
			return this.createTx(comment).pipe(
				modifyArtifact(artId, applicability, attrConfig)
			);
		},
		modifyArtifactAndMutate: function (
			comment: string,
			artId: `${number}`,
			applicability: applic,
			attrConfig: {
				set?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
				add?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
				delete?: attribute<
					string | number | boolean | any[] | unknown,
					ATTRIBUTETYPEID
				>[];
			}
		) {
			//@ts-ignore
			return this.modifyArt(
				comment,
				artId,
				applicability,
				attrConfig
			).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
		deleteArt: function (comment: string, artId: `${number}`) {
			//@ts-ignore
			return this.createTx(comment).pipe(deleteArtifact(artId));
		},
		deleteRelation: function (comment: string, relation: modifyRelation) {
			//@ts-ignore
			return this.createTx(comment).pipe(deleteRelation(relation));
		},
		deleteRelations: function (
			comment: string,
			relations: modifyRelation[]
		) {
			//@ts-ignore
			return this.createTx(comment).pipe(deleteRelations(relations));
		},
		deleteRelationAndMutate: function (
			comment: string,
			relation: modifyRelation
		) {
			//@ts-ignore
			return this.deleteRelation(comment, relation).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
		deleteRelationsAndMutate: function (
			comment: string,
			relations: modifyRelation[]
		) {
			//@ts-ignore
			return this.deleteRelations(comment, relations).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
		deleteArtifactAndMutate: function (
			comment: string,
			artId: `${number}`
		) {
			//@ts-ignore
			return this.deleteArt(comment, artId).pipe(this.performMutation());
		},
		addRelation: function (comment: string, relation: modifyRelation) {
			//@ts-ignore
			return this.createTx(comment).pipe(addRelation(relation));
		},
		addRelationAndMutate: function (
			comment: string,
			relation: modifyRelation
		) {
			//@ts-ignore
			return this.addRelation(comment, relation).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
		addRelations: function (comment: string, relations: modifyRelation[]) {
			//@ts-ignore
			return this.createTx(comment).pipe(addRelations(relations));
		},
		addRelationsAndMutate: function (
			comment: string,
			relations: modifyRelation[]
		) {
			//@ts-ignore
			return this.addRelations(comment, relations).pipe(
				//@ts-ignore
				this.performMutation()
			);
		},
	};
