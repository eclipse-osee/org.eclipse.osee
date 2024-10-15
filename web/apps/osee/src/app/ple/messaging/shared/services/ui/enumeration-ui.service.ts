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
import { Injectable, inject } from '@angular/core';
import type { enumeration, enumerationSet } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { CurrentTransactionService } from '@osee/transactions/services';
import { Observable, of } from 'rxjs';
import { share, shareReplay, switchMap, take } from 'rxjs/operators';
import { EnumerationSetService } from '../http/enumeration-set.service';
import {
	createArtifact,
	deleteArtifact,
	deleteRelation,
	modifyArtifact,
} from '@osee/transactions/functions';
import { transaction } from '@osee/transactions/types';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';

@Injectable({
	providedIn: 'root',
})
export class EnumerationUIService {
	private enumSetService = inject(EnumerationSetService);

	private _tx = inject(CurrentTransactionService);
	private ui = inject(UiService);

	private _enumSets = this.ui.id.pipe(
		share(),
		switchMap((id) => this.enumSetService.getEnumSets(id).pipe(share())),
		shareReplay({ bufferSize: 1, refCount: true })
	);

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

	private _getEnumSetAttributes(enumerationSet: enumerationSet) {
		const {
			id,
			gammaId,
			applicability,
			enumerations,
			...remainingAttributes
		} = enumerationSet;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		return attributes;
	}

	private _getEnumAttributes(enumeration: enumeration) {
		const { id, gammaId, applicability, ...remainingAttributes } =
			enumeration;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		return attributes;
	}
	changeEnumerationsForEnumSet(
		tx: Required<transaction>,
		key: string,
		currentEnums: enumeration[],
		previousEnums: enumeration[]
	) {
		/* look through both enumeration lists to find the following:
			if currentEnumSet enumeration is not present in previousEnumSet enumeration, addArtifact + relate
			if previousEnumSet enumeration is not present in the currentEnumSet enumeration, deleteRelation TODO: do we want to delete artifact?
			if currentEnumSet enumeration is present in previousEnumSet enumeration, and the attributes + applicability are the same, do nothing
			if currentEnumSet enumeration is present in previousEnumSet enumeration, and the attributes || applicability are the different, modifyArtifact
			*/
		const previousEnumIds = previousEnums.map((v) => v.id);
		const currentEnumIds = currentEnums.map((v) => v.id);
		const enumsToAdd = currentEnums.filter(
			(v) => v.id === '-1' || !previousEnumIds.includes(v.id)
		);
		enumsToAdd.forEach((enumeration) => {
			const results = createArtifact(
				tx,
				ARTIFACTTYPEIDENUM.ENUM,
				enumeration.applicability,
				[
					{
						typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
						sideA: key,
					},
				],
				undefined,
				...[
					enumeration.name,
					enumeration.ordinal,
					enumeration.ordinalType,
				]
			);
			tx = results.tx;
		});
		const enumsToRemove = previousEnums.filter(
			(v) => !currentEnumIds.includes(v.id)
		);
		enumsToRemove.forEach((enumeration) => {
			tx = deleteRelation(tx, {
				typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
				aArtId: key,
				bArtId: enumeration.id,
			});
		});
		const enumsEligibleToModify = currentEnums.filter(
			(v) => !enumsToAdd.includes(v) && !enumsToRemove.includes(v)
		);
		enumsEligibleToModify.forEach((enumeration) => {
			const previousEnum = previousEnums.find(
				(x) => x.id === enumeration.id
			);
			if (previousEnum) {
				if (
					previousEnum.applicability.id !==
						enumeration.applicability.id ||
					previousEnum.name.value !== enumeration.name.value ||
					previousEnum.ordinal.value !== enumeration.ordinal.value ||
					previousEnum.ordinalType.value !==
						enumeration.ordinalType.value
				) {
					const currentAttr = this._getEnumAttributes(enumeration);
					const previousAttr = this._getEnumAttributes(previousEnum);
					const attrToAdd = currentAttr.filter((v) => v.id === '-1');
					const modifyAttr = currentAttr
						.filter((v) => v.id !== '-1')
						.filter(
							(v) =>
								previousAttr.filter(
									(x) =>
										x.id === v.id &&
										x.typeId === v.typeId &&
										x.gammaId === v.gammaId &&
										x.value !== v.value
								).length > 0
						);
					const deleteAttr = previousAttr.filter(
						(v) => !currentAttr.map((x) => x.id).includes(v.id)
					);
					tx = modifyArtifact(
						tx,
						enumeration.id,
						enumeration.applicability,
						{
							add: attrToAdd,
							set: modifyAttr,
							delete: deleteAttr,
						}
					);
				}
			}
		});
		return tx;
	}
	updateEnumSet(
		currentEnumSet: enumerationSet,
		previousEnumSet: enumerationSet,
		existingTx?: Required<transaction>,
		key?: string
	) {
		let tx =
			existingTx ??
			this._tx.createTransaction(
				`Changing enumeration set ${previousEnumSet.name.value}`
			);
		if (
			currentEnumSet.id === '-1' &&
			currentEnumSet.id !== previousEnumSet.id &&
			previousEnumSet.id !== '-1'
		) {
			//this indicates the enum set should be deleted, alongside it's enumerations...
			//TODO: should we also check that enumerations be zeroized? i.e. is there a situation in which we would want to share enums
			tx = deleteArtifact(tx, previousEnumSet.id);
			previousEnumSet.enumerations.forEach((enumeration) => {
				tx = deleteArtifact(tx, enumeration.id);
			});
			return tx;
		}
		if (
			currentEnumSet.id !== '-1' &&
			currentEnumSet.id === previousEnumSet.id
		) {
			//this indicates the enum set changed, or the enumerations themselves
			const currentEnumSetAttributes =
				this._getEnumSetAttributes(currentEnumSet);
			const previousEnumSetAttributes =
				this._getEnumSetAttributes(previousEnumSet);
			const addEnumSetAttributes = currentEnumSetAttributes.filter(
				(v) => v.id === '-1'
			);
			const modifyEnumSetAttributes = currentEnumSetAttributes
				.filter((v) => v.id !== '-1')
				.filter(
					(v) =>
						previousEnumSetAttributes.filter(
							(x) =>
								x.id === v.id &&
								x.typeId === v.typeId &&
								x.gammaId === v.gammaId &&
								x.value !== v.value
						).length > 0
				);
			const deleteEnumSetAttributes = previousEnumSetAttributes.filter(
				(v) => !currentEnumSetAttributes.map((x) => x.id).includes(v.id)
			);
			if (
				currentEnumSet.applicability.id !==
					previousEnumSet.applicability.id ||
				addEnumSetAttributes.length > 0 ||
				modifyEnumSetAttributes.length > 0 ||
				deleteEnumSetAttributes.length > 0
			) {
				tx = modifyArtifact(
					tx,
					currentEnumSet.id,
					currentEnumSet.applicability,
					{
						set: modifyEnumSetAttributes,
						add: addEnumSetAttributes,
						delete: deleteEnumSetAttributes,
					}
				);
			}
			tx = this.changeEnumerationsForEnumSet(
				tx,
				currentEnumSet.id,
				currentEnumSet.enumerations,
				previousEnumSet.enumerations
			);
			return tx;
		}
		if (
			currentEnumSet.id === '-1' &&
			currentEnumSet.id === previousEnumSet.id &&
			previousEnumSet.id === '-1' &&
			currentEnumSet.name.value !== ''
		) {
			// create a new enum set
			const rel =
				existingTx && key
					? [
							{
								typeId: RELATIONTYPEIDENUM.INTERFACEENUMSETTOPLATFORMTYPE,
								sideA: key,
							},
						]
					: [];
			const results = createArtifact(
				tx,
				ARTIFACTTYPEIDENUM.ENUMSET,
				currentEnumSet.applicability,
				rel,
				undefined,
				...[currentEnumSet.description, currentEnumSet.name]
			);
			tx = this.changeEnumerationsForEnumSet(
				tx,
				results._newArtifact.key,
				currentEnumSet.enumerations,
				previousEnumSet.enumerations
			);
			return tx;
		}
		if (existingTx) {
			return existingTx;
		}
		throw new Error(
			`Did not meet conditions for a transaction. Current Enum Set Id: ${currentEnumSet.id} Previous Enum Set Id: ${previousEnumSet.id}`
		);
	}
	changeEnumSet(
		currentEnumSet: enumerationSet,
		previousEnumSet: enumerationSet
	) {
		const tx = this.updateEnumSet(currentEnumSet, previousEnumSet);
		return of(tx).pipe(this._tx.performMutation());
	}
}
