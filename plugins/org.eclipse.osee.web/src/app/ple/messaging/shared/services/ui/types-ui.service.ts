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
import { iif, of, from, combineLatest } from 'rxjs';
import {
	share,
	switchMap,
	repeatWhen,
	shareReplay,
	take,
	tap,
	mergeMap,
	reduce,
	concatMap,
	map,
	filter,
} from 'rxjs/operators';
import { transaction } from 'src/app/transactions/transaction';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../types/enum';
import { enumeratedPlatformType } from '../../types/enumeratedPlatformType';
import { PlatformType } from '../../types/platformType';
import { TypesService } from '../http/types.service';
import { EnumerationUIService } from './enumeration-ui.service';

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

	getPaginatedFilteredTypes(filterString: string, pageNum: string) {
		return this._ui.id.pipe(
			filter((id) => id !== ''),
			share(),
			switchMap((id) =>
				this._typesService
					.getPaginatedFilteredTypes(filterString, id, pageNum)
					.pipe(
						repeatWhen((_) => this._ui.update),
						share()
					)
			),
			shareReplay({ bufferSize: 1, refCount: true })
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
	partialUpdate(body: Partial<PlatformType>) {
		return this._typesService
			.changePlatformType(this._ui.id.getValue(), body)
			.pipe(
				take(1),
				switchMap((transaction) =>
					this._typesService.performMutation(transaction).pipe(
						tap(() => {
							this._ui.updated = true;
						})
					)
				)
			);
	}

	copyType<T extends PlatformType | Partial<PlatformType>>(body: T) {
		this.removeId(body);
		delete body.enumSet;
		return body.interfaceLogicalType === 'enumeration' &&
			'enumerationSet' in body
			? this.copyEnumeratedType(body as enumeratedPlatformType)
			: this._typesService
					.createPlatformType(this._ui.id.getValue(), body, [])
					.pipe(
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
					);
	}

	copyEnumeratedType(body: enumeratedPlatformType) {
		const { enumerationSet, ...type } = body;
		return combineLatest([
			this._ui.id,
			this.createEnumSet(enumerationSet),
		]).pipe(
			take(1),
			filter(
				([id, enumerationSetResults]) =>
					id !== '' && enumerationSetResults.results.success
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
								type,
								[relation]
							)
						)
					)
			),
			take(1),
			switchMap((transaction) =>
				this._typesService.performMutation(transaction).pipe(
					tap(() => {
						this._ui.updated = true;
					})
				)
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
