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
import type { enumerationSet } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import {
	legacyCreateArtifact,
	legacyModifyArtifact,
	legacyModifyRelation,
	legacyTransaction,
} from '@osee/transactions/types';
import { Observable } from 'rxjs';
import { map, share, shareReplay, switchMap, take } from 'rxjs/operators';
import { EnumerationSetService } from '../http/enumeration-set.service';

@Injectable({
	providedIn: 'root',
})
export class EnumerationUIService {
	private enumSetService = inject(EnumerationSetService);
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
	changeEnumSet(dialogResponse: {
		createArtifacts: legacyCreateArtifact[];
		modifyArtifacts: legacyModifyArtifact[];
		deleteRelations: legacyModifyRelation[];
	}) {
		return this.ui.id.pipe(
			take(1),
			map((id) => {
				const tx: legacyTransaction = {
					branch: id,
					txComment: 'Updating enumeration',
					createArtifacts: dialogResponse.createArtifacts,
					modifyArtifacts: dialogResponse.modifyArtifacts,
					deleteRelations: dialogResponse.deleteRelations,
				};
				return tx;
			}),
			switchMap((transaction) =>
				this.enumSetService.performMutation(transaction)
			)
		);
	}
}
