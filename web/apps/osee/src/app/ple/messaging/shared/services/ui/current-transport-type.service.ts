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
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import {
	filter,
	repeatWhen,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import {
	createTransportType,
	serialize,
	transportType,
} from '@osee/messaging/shared/types';
import { TransportTypeService } from '../http/transport-type.service';
import {
	TransactionService,
	TransactionBuilderService,
} from '@osee/shared/transactions';

@Injectable({
	providedIn: 'root',
})
export class CurrentTransportTypeService {
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
	constructor(
		private ui: UiService,
		private transportTypeService: TransportTypeService,
		private transactionService: TransactionService,
		private transactionBuilder: TransactionBuilderService
	) {}

	get types() {
		return this._types;
	}

	get transportTypes() {
		return this._transportTypes;
	}

	getPaginatedTypes(pageNum: string | number, pageSize: string | number) {
		return this.ui.id.pipe(
			take(1),
			switchMap((id) =>
				this.transportTypeService.getPaginated(id, pageNum, pageSize)
			)
		);
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
		return this._currentBranchTake1.pipe(
			switchMap((id) =>
				of(
					this.transactionBuilder.createArtifact(
						serialize(type),
						ARTIFACTTYPEIDENUM.TRANSPORTTYPE,
						[],
						undefined,
						id,
						'Creating Transport Type',
						undefined
					)
				)
			),
			switchMap((transaction) =>
				this.transactionService.performMutation(transaction).pipe(
					tap((result) => {
						this.ui.updated = true;
					})
				)
			)
		);
	}

	modifyType(type: transportType) {
		return this._currentBranchTake1.pipe(
			switchMap((id) =>
				of(
					this.transactionBuilder.modifyArtifact(
						serialize(type),
						undefined,
						id,
						'Modifying transport type'
					)
				)
			),
			switchMap((tx) =>
				this.transactionService.performMutation(tx).pipe(
					tap((result) => {
						this.ui.updated = true;
					})
				)
			)
		);
	}
}
