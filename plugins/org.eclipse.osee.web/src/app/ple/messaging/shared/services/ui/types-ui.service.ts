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
	PlatformType,
	enumeration,
	enumerationSet,
	enumeratedPlatformType,
} from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	transaction,
} from '@osee/shared/types';
import { of, from, combineLatest } from 'rxjs';
import {
	share,
	switchMap,
	repeatWhen,
	shareReplay,
	take,
	tap,
	reduce,
	concatMap,
	map,
	filter,
} from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { applic } from '@osee/shared/types/applicability';
import { TypesService } from '../http/types.service';
import { EnumerationUIService } from './enumeration-ui.service';
import {
	ARTIFACTTYPEID,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';

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
	constructor(
		private _ui: UiService,
		private _typesService: TypesService,
		private _enumSetService: EnumerationUIService
	) {}
	get types() {
		return this._types;
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
	changeType(type: Partial<PlatformType>) {
		return this._ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((branchId) =>
				this._typesService.changePlatformType(branchId, type)
			)
		);
	}
	performMutation(body: transaction) {
		return this._ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((branchId) => this._typesService.performMutation(body))
		);
	}

	/**
	 *
	 * @TODO replace enumSetData with actual enumerationSet
	 */
	createType(
		body: PlatformType | Partial<PlatformType>,
		isNewEnumSet: boolean,
		enumSetData: {
			enumSetId: string;
			enumSetName: string;
			enumSetDescription: string;
			enumSetApplicability: applic;
			enums: enumeration[];
		}
	) {
		delete body.id;
		const enumInfo: enumerationSet = {
			id: enumSetData.enumSetId,
			name: enumSetData.enumSetName,
			applicability: enumSetData.enumSetApplicability,
			description: enumSetData.enumSetDescription,
			enumerations: enumSetData.enums,
		};
		return body.interfaceLogicalType === 'enumeration'
			? isNewEnumSet
				? combineLatest([
						this._ui.id,
						this.createEnumSet(enumInfo),
				  ]).pipe(
						take(1),
						filter(
							([id, enumerationSetResults]) =>
								id !== '' &&
								enumerationSetResults.results.success
						),
						switchMap(([branchId, enumerationSetResults]) =>
							this._enumSetService
								.createPlatformTypeToEnumSetRelation(
									enumerationSetResults.results.ids[0]
								)
								.pipe(
									switchMap((relation) =>
										this._typesService.createPlatformType(
											branchId,
											body,
											[relation]
										)
									)
								)
						),
						take(1),
						switchMap((transaction) =>
							this._typesService
								.performMutation(transaction)
								.pipe(
									tap(() => {
										this._ui.updated = true;
									})
								)
						)
				  )
				: this._enumSetService
						.createPlatformTypeToEnumSetRelation(
							enumSetData.enumSetId
						)
						.pipe(
							take(1),
							switchMap((relation) =>
								this._typesService.createPlatformType(
									this._ui.id.getValue(),
									body,
									[relation]
								)
							),
							take(1),
							switchMap((transaction) =>
								this._typesService
									.performMutation(transaction)
									.pipe(
										tap(() => {
											this._ui.updated = true;
										})
									)
							)
						)
			: this._ui.id.pipe(
					take(1),
					switchMap((id) =>
						this._typesService.createPlatformType(id, body, [])
					),
					switchMap((tx) => this._typesService.performMutation(tx)),
					take(1),
					tap((_) => (this._ui.updated = true))
			  );
	}
	private fixEnum(enumeration: enumeration) {
		enumeration.applicabilityId = enumeration.applicability.id;
		return of<enumeration>(enumeration);
	}

	private mergeEnumArray(transactions: transaction[]) {
		let currentTransaction: transaction = {
			branch: '',
			txComment: '',
			createArtifacts: [],
		};
		if (transactions?.[0]) {
			currentTransaction = transactions.shift() || {
				branch: '',
				txComment: '',
				createArtifacts: [],
			};
		}
		transactions.forEach((transaction) => {
			currentTransaction.createArtifacts?.push(
				...(transaction?.createArtifacts || [])
			);
		});
		return of<transaction>(currentTransaction);
	}
	private mergeEnumTransactionWithPlatformType(
		transactionA: transaction,
		transactionB: transaction
	) {
		transactionA.createArtifacts?.push(
			...(transactionB.createArtifacts || [])
		);
		return of<transaction>(transactionA);
	}
	partialUpdate(dialogResponse: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		return this._ui.id.pipe(
			take(1),
			map((id) => {
				const tx: transaction = {
					branch: id,
					txComment: 'Updating platform type',
					createArtifacts: dialogResponse.createArtifacts,
					modifyArtifacts: dialogResponse.modifyArtifacts,
					deleteRelations: dialogResponse.deleteRelations,
				};
				return tx;
			}),
			switchMap((transaction) =>
				this._typesService.performMutation(transaction)
			)
		);
	}

	copyType(dialogResponse: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		const createArtifacts = dialogResponse.createArtifacts.sort((a, b) => {
			if (_isCreationRel(a.typeId) && _isCreationRel(b.typeId)) {
				return (
					_artTypes.indexOf(b.typeId) - _artTypes.indexOf(a.typeId)
				);
			} else {
				return 0;
			}
		});
		return this._ui.id.pipe(
			take(1),
			map((id) => {
				const tx: transaction = {
					branch: id,
					txComment: 'Creating platform type',
					createArtifacts: createArtifacts,
					modifyArtifacts: dialogResponse.modifyArtifacts,
					deleteRelations: dialogResponse.deleteRelations,
				};
				return tx;
			}),
			switchMap((transaction) =>
				this._typesService.performMutation(transaction)
			)
		);
	}

	createEnums(set: enumerationSet, id: string) {
		return this._ui.id.pipe(
			filter((branchId) => branchId !== ''),
			switchMap((branchId) =>
				of(set).pipe(
					take(1),
					concatMap((enumSet) =>
						from(enumSet.enumerations || []).pipe(
							switchMap((enumeration) =>
								this.fixEnum(enumeration)
							),
							switchMap((enumeration) =>
								this._enumSetService
									.createEnumToEnumSetRelation(id)
									.pipe(
										switchMap((relation) =>
											this._enumSetService.createEnum(
												branchId,
												enumeration,
												[relation]
											)
										)
									)
							)
						)
					),
					reduce((acc, curr) => [...acc, curr], [] as transaction[]),
					switchMap((enumTransactions) =>
						this.mergeEnumArray(enumTransactions)
					)
				)
			)
		);
	}

	createEnumSet(set: enumerationSet) {
		const { enumerations, ...body } = set;
		if (
			body.applicabilityId === undefined &&
			body.applicability !== undefined &&
			body.applicability.id !== undefined &&
			body.applicability.id !== '-1'
		) {
			//make sure applicabilityId is always set
			body.applicabilityId = body.applicability.id;
		}
		const enumSet = this._ui.id.pipe(
			take(1),
			filter((id) => id !== ''),
			switchMap((id) => this._enumSetService.createEnumSet(id, body, [])),
			switchMap((enumTransaction) =>
				this._typesService.performMutation(enumTransaction)
			)
		);
		return combineLatest([this._ui.id, enumSet]).pipe(
			take(1),
			filter(
				([id, enumSetResults]) =>
					id !== '' && enumSetResults.results.success
			),
			switchMap(([id, enumSetResults]) =>
				this.createEnums(set, enumSetResults.results.ids[0]).pipe(
					switchMap((enumTransaction) =>
						this.mergeEnumArray([enumTransaction])
					),
					switchMap((enumTransaction) =>
						this._typesService.performMutation(enumTransaction)
					),
					map((_) => enumSetResults)
				)
			)
		);
	}

	/**
	 * recursively remove the id property from an object using JSON.parse(JSON.stringify())
	 * typically used before doing a creation rest call
	 * @param object
	 */
	private removeId(object: Object) {
		JSON.parse(
			JSON.stringify(object, (k, v) => (k === 'id' ? undefined : v))
		);
	}
}
