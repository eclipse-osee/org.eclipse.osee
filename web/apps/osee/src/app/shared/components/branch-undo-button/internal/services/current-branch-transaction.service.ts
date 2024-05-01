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
import { map, switchMap, take } from 'rxjs/operators';
import { BranchTransactionService } from './branch-transaction.service';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class CurrentBranchTransactionService {
	private _undoLatest = this._uiService.id.pipe(
		take(1),
		switchMap((id) =>
			this._branchTransactionService.undoLatest(id).pipe(
				map((result) => {
					this._uiService.updated = true;
					return result;
				})
			)
		)
	);
	constructor(
		private _uiService: UiService,
		private _branchTransactionService: BranchTransactionService
	) {}

	get undoLatest() {
		return this._undoLatest;
	}
}
