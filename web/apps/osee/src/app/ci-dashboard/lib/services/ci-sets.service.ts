/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { CiSetsHttpService } from './ci-sets-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	repeat,
	shareReplay,
	switchMap,
	take,
} from 'rxjs';
import { CISet } from '../types/tmo';
import { CurrentTransactionService } from '@osee/transactions/services';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Injectable({
	providedIn: 'root',
})
export class CiSetsService {
	private ciSetsService = inject(CiSetsHttpService);
	private ui = inject(CiDashboardUiService);
	private _currentTx = inject(CurrentTransactionService);

	private _activeOnly = new BehaviorSubject<boolean>(true);

	private _ciSets = combineLatest([this._activeOnly, this.ui.branchId]).pipe(
		filter(([_, branchId]) => branchId !== '' && branchId !== '-1'),
		switchMap(([active, branchId]) =>
			this.ciSetsService
				.getCiSets(branchId, active)
				.pipe(repeat({ delay: () => this.ui.updateRequired }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _adminCiSets = this.ui.branchId.pipe(
		filter((branchId) => branchId !== '' && branchId !== '-1'),
		switchMap((branchId) =>
			this.ciSetsService
				.getCiSets(branchId, false, ATTRIBUTETYPEIDENUM.NAME)
				.pipe(repeat({ delay: () => this.ui.updateRequired }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	createCISet(ciSet: CISet) {
		const { id, gammaId, ...remainingAttributes } = ciSet;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		return this._currentTx.createArtifactAndMutate(
			`Creating CI Set ${ciSet.name.value}`,
			ARTIFACTTYPEIDENUM.SCRIPTSET,
			applicabilitySentinel,
			[],
			...attributes
		);
	}

	deleteCISet(ciSet: CISet) {
		return this._currentTx
			.deleteArtifactAndMutate(
				`Delete CI Set ${ciSet.name.value}`,
				ciSet.id
			)
			.pipe(take(1))
			.subscribe();
	}

	get ciSets() {
		return this._ciSets;
	}

	get adminCiSets() {
		return this._adminCiSets;
	}

	get activeOnly() {
		return this._activeOnly.asObservable();
	}

	set ActiveOnly(activeOnly: boolean) {
		this._activeOnly.next(activeOnly);
	}
}
