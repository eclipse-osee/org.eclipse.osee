/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { TransportTypeService } from '@osee/messaging/shared/services';
import { transportType } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	filter,
	repeatWhen,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';

@Injectable({
	providedIn: 'root',
})
export class CurrentTransportTypePageService {
	private ui = inject(UiService);
	private transportTypeService = inject(TransportTypeService);

	private _types = this.ui.id.pipe(
		filter((val) => val !== '' && val !== '0'),
		switchMap((id) => this.transportTypeService.getAll(id)),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _transportTypes = this.ui.id.pipe(
		filter((val) => val !== '' && val !== '0'),
		switchMap((id) =>
			this.transportTypeService
				.getAll(id)
				.pipe(repeatWhen((_) => this.ui.update))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _currentTx = inject(CurrentTransactionService);

	get types() {
		return this._types;
	}

	get transportTypes() {
		return this._transportTypes;
	}

	getType(artId: string) {
		return this.ui.id.pipe(
			filter((val) => val !== '' && val !== '0'),
			switchMap((id) => this.transportTypeService.get(id, artId))
		);
	}
	private _currentBranchTake1 = this.ui.id.pipe(
		take(1),
		filter((val) => val !== '' && val !== '0')
	);

	createType(type: transportType) {
		const {
			id,
			gammaId,
			applicability,
			directConnection,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '')
			.map((x) => {
				if (Array.isArray(x.value)) {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: '[' + x.value.toString() + ']',
					};
				}
				if (typeof x.value === 'number') {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: x.value.toString(),
					};
				}
				return x;
			});
		return this._currentTx.createArtifactAndMutate(
			`Creating transport type ${type.name.value}`,
			ARTIFACTTYPEIDENUM.TRANSPORTTYPE,
			applicability,
			[],
			...attributes
		);
	}

	private _getTransportTypeAttributes(type: transportType) {
		const {
			id,
			gammaId,
			applicability,
			directConnection,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '')
			.map((x) => {
				if (Array.isArray(x.value)) {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: '[' + x.value.toString() + ']',
					};
				}
				if (typeof x.value === 'number') {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: x.value.toString(),
					};
				}
				return x;
			});
		return attributes;
	}
	modifyType(type: transportType, previous: transportType) {
		const previousAttributes = this._getTransportTypeAttributes(previous);
		const {
			id,
			gammaId,
			applicability,
			directConnection,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys
			.map((k) => remainingAttributes[k])
			.filter((attr) => attr !== undefined && attr.id !== '')
			.map((x) => {
				if (Array.isArray(x.value)) {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: '[' + x.value.toString() + ']',
					};
				}
				if (typeof x.value === 'number') {
					return {
						id: x.id,
						typeId: x.typeId,
						gammaId: x.gammaId,
						value: x.value.toString(),
					};
				}
				return x;
			});
		const addAttributes = attributes.filter((v) => v.id === '-1');
		const modifyAttributes = attributes
			.filter((v) => v.id !== '-1')
			.filter(
				(v) =>
					previousAttributes.filter(
						(x) =>
							x.id === v.id &&
							x.typeId === v.typeId &&
							x.gammaId === v.gammaId &&
							x.value !== v.value
					).length > 0
			);
		const deleteAttributes = previousAttributes.filter(
			(v) => !attributes.map((x) => x.id).includes(v.id)
		);
		return this._currentTx.modifyArtifactAndMutate(
			`Modifying transport type ${previous.name.value}`,
			id,
			applicability,
			{
				set: modifyAttributes,
				add: addAttributes,
				delete: deleteAttributes,
			}
		);
	}
}
