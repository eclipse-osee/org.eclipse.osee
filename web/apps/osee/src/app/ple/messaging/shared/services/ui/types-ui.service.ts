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
import { applicabilitySentinel } from '@osee/applicability/types';
import type {
	PlatformType,
	enumeration,
	enumerationSet,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import {
	ARTIFACTTYPEIDENUM,
	RELATIONTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	createArtifact as _createArtifact,
	modifyArtifact,
} from '@osee/transactions/functions';
import { CurrentTransactionService } from '@osee/transactions/services';
import { transaction, transactionResult } from '@osee/transactions/types';
import { Observable, combineLatest, of } from 'rxjs';
import {
	filter,
	map,
	repeatWhen,
	share,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';
import { TypesService } from '../http/types.service';
import { EnumerationUIService } from './enumeration-ui.service';

const _artTypes = [
	ARTIFACTTYPEIDENUM.ENUM,
	ARTIFACTTYPEIDENUM.ENUMSET,
	ARTIFACTTYPEIDENUM.PLATFORMTYPE,
] as const;

type CreationRels = (typeof _artTypes)[number];
function _isCreationRel(rel2: string): rel2 is CreationRels {
	return !!_artTypes.find((rel) => rel2 === rel);
}

@Injectable({
	providedIn: 'root',
})
export class TypesUIService {
	private _ui = inject(UiService);
	private _typesService = inject(TypesService);
	private _enumSetService = inject(EnumerationUIService);

	private _types = this._ui.id.pipe(
		filter((id) => id !== ''),
		share(),
		switchMap((x) =>
			this._typesService.getTypes(x).pipe(
				repeatWhen((_) => this._ui.update),
				share()
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _typeDetailLocation = combineLatest([
		this._ui.type,
		this._ui.id,
	]).pipe(
		map(([type, id]) => '/ple/messaging/' + type + '/' + id + '/type/')
	);

	private _typeSearchLocation = combineLatest([
		this._ui.type,
		this._ui.id,
	]).pipe(map(([type, id]) => '/ple/messaging/types/' + type + '/' + id));

	private _currentTx = inject(CurrentTransactionService);
	get types() {
		return this._types;
	}

	get detailLocation() {
		return this._typeDetailLocation;
	}

	get searchLocation() {
		return this._typeSearchLocation;
	}

	getPaginatedFilteredTypes(
		filterString: string,
		count: number,
		pageNum: string
	) {
		return this._ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) =>
				this._typesService.getPaginatedFilteredTypes(
					filterString,
					id,
					count,
					pageNum
				)
			)
		);
	}

	getFilteredTypesCount(filterString: string) {
		return this._ui.id.pipe(
			filter((id) => id !== ''),
			switchMap((id) =>
				this._typesService.getFilteredTypesCount(filterString, id)
			)
		);
	}

	getType(typeId: string) {
		return this._ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			share(),
			switchMap((branch) =>
				this._typesService.getType(branch, typeId).pipe(share())
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	getTypeFromBranch(branchId: string, typeId: string) {
		return this._typesService.getType(branchId, typeId);
	}
	changeType(type: PlatformType) {
		const {
			id,
			applicability,
			gammaId,
			added,
			deleted,
			changes,
			enumSet,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		return this._currentTx.modifyArtifactAndMutate(
			'Modifying Platform Type',
			id,
			applicability,
			{ set: attributes }
		);
	}

	createPlatformType(
		type: PlatformType,
		existingTx?: Required<transaction>,
		elementKey?: string
	) {
		const {
			id,
			gammaId,
			added,
			deleted,
			changes,
			applicability,
			enumSet,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		//if the enumset's id > 0 and not '' , we should reuse the existing enum set, else we should create a new enum set
		const enumSetRels =
			remainingAttributes.interfaceLogicalType.value === 'enumeration' &&
			enumSet.id !== '-1' &&
			enumSet.id !== '0'
				? [
						{
							typeId: '2455059983007225794' as const,
							sideB: enumSet.id,
						},
					]
				: [];
		const elementRels =
			elementKey && existingTx
				? [
						{
							typeId: RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
							sideA: elementKey,
						},
					]
				: [];
		const { tx, ...createdType } =
			elementKey && existingTx
				? _createArtifact(
						existingTx,
						ARTIFACTTYPEIDENUM.PLATFORMTYPE,
						applicability,
						[...enumSetRels, ...elementRels],
						undefined,
						...attributes
					)
				: this._currentTx.createArtifact(
						'Creating Platform Type',
						ARTIFACTTYPEIDENUM.PLATFORMTYPE,
						applicability,
						enumSetRels,
						...attributes
					);
		if (remainingAttributes.interfaceLogicalType.value === 'enumeration') {
			if (enumSet.id === '-1' || enumSet.id === '0') {
				this.createEnumerationSet(
					enumSet,
					createdType._newArtifact.key,
					tx
				);
			}
		}
		return { tx, createdType };
	}
	createType(body: PlatformType): Observable<Required<transactionResult>>;
	createType(
		body: PlatformType,
		elementKey: string,
		existingTx: Required<transaction>
	): Observable<Required<transaction>>;
	createType(
		body: PlatformType,
		elementKey?: string,
		existingTx?: Required<transaction>
	) {
		const { tx } = this.createPlatformType(body, existingTx, elementKey);
		if (!(elementKey && existingTx)) {
			return of(tx).pipe(take(1), this._currentTx.performMutation());
		}
		return of(tx).pipe(take(1));
	}
	private _getPlatformTypeAttributes(platformType: PlatformType) {
		const {
			id,
			applicability,
			gammaId,
			added,
			deleted,
			changes,
			enumSet,
			...remainingAttributes
		} = platformType;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr.id !== '');
		return attributes;
	}
	partialUpdate(current: PlatformType, previous: PlatformType) {
		let tx = this._currentTx.createTransaction(
			`Changing Platform Type ${previous.name.value}`
		);
		const currentAttr = this._getPlatformTypeAttributes(current);
		const previousAttr = this._getPlatformTypeAttributes(previous);
		const attrToAdd = currentAttr.filter((x) => x.id === '-1');
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
		if (
			previous.applicability.id !== current.applicability.id ||
			attrToAdd.length > 0 ||
			modifyAttr.length > 0 ||
			deleteAttr.length > 0
		) {
			tx = modifyArtifact(tx, previous.id, current.applicability, {
				add: attrToAdd,
				set: modifyAttr,
				delete: deleteAttr,
			});
		}
		tx = this._enumSetService.updateEnumSet(
			current.enumSet,
			previous.enumSet,
			tx
		);
		return of(tx).pipe(this._currentTx.performMutation());
	}
	copyType(platformType: PlatformType) {
		const attributes = this._getPlatformTypeAttributes(platformType);
		const results = this._currentTx.createArtifact(
			`Copying Platform Type`,
			ARTIFACTTYPEIDENUM.PLATFORMTYPE,
			platformType.applicability,
			[],
			...attributes
		);
		let tx = results.tx;
		tx = this._enumSetService.updateEnumSet(
			platformType.enumSet,
			{
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				applicability: applicabilitySentinel,
				enumerations: [],
			},
			tx,
			results._newArtifact.key
		);
		return of(tx).pipe(this._currentTx.performMutation());
	}

	createEnum(
		enumeration: enumeration,
		enumKey: string,
		existingTx: Required<transaction>
	) {
		const { id, gammaId, applicability, ...remainingAttributes } =
			enumeration;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		return _createArtifact(
			existingTx,
			ARTIFACTTYPEIDENUM.ENUM,
			applicability,
			[
				{
					typeId: RELATIONTYPEIDENUM.INTERFACEENUMTOENUMSET,
					sideA: enumKey,
				},
			],
			undefined,
			...attributes
		);
	}

	createEnumerationSet(
		set: enumerationSet,
		platformTypeKey?: string,
		existingTx?: Required<transaction>
	) {
		const {
			enumerations,
			id,
			gammaId,
			applicability,
			...remainingAttributes
		} = set;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		const { tx, _newArtifact } =
			platformTypeKey && existingTx
				? _createArtifact(
						existingTx,
						ARTIFACTTYPEIDENUM.ENUMSET,
						applicability,
						[
							{
								typeId: RELATIONTYPEIDENUM.INTERFACEENUMSETTOPLATFORMTYPE,
								sideA: platformTypeKey,
							},
						],
						undefined,
						...attributes
					)
				: this._currentTx.createArtifact(
						'Creating enumset',
						ARTIFACTTYPEIDENUM.ENUMSET,
						applicability,
						[],
						...attributes
					);
		enumerations.forEach((enumeration) => {
			this.createEnum(enumeration, _newArtifact.key, tx);
		});
		return tx;
	}

	createEnumSet(
		set: enumerationSet,
		platformTypeKey?: string,
		existingTx?: Required<transaction>
	) {
		const tx = this.createEnumerationSet(set, platformTypeKey, existingTx);
		if (!(platformTypeKey && existingTx)) {
			return of(tx).pipe(take(1), this._currentTx.performMutation());
		}
		return of(tx).pipe(take(1));
	}
}
