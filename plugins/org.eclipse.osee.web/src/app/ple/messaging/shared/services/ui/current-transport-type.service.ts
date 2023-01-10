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
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { TransactionService } from '../../../../../transactions/transaction.service';
import { ARTIFACTTYPEID } from '../../../../../types/constants/ArtifactTypeId.enum';
import { transportType } from '../../types/transportType';
import { TransportTypeService } from '../http/transport-type.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentTransportTypeService {
	private _types = this.ui.id.pipe(
		filter((val) => val !== '' && val !== '0'),
		switchMap((id) => this.transportTypeService.getAll(id))
		//shareReplay({ bufferSize: 1, refCount: true })
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

	createType(type: transportType) {
		return this.ui.id.pipe(
			take(1),
			filter((val) => val !== '' && val !== '0'),
			switchMap((id) =>
				of(
					this.transactionBuilder.createArtifact(
						type,
						ARTIFACTTYPEID.TRANSPORTTYPE,
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
}
