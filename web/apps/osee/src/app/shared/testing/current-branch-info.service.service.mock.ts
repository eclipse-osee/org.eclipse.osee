/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { Injectable, signal } from '@angular/core';
import { testBranchInfo, testCommitResponse } from '@osee/shared/testing';
import { Observable, of } from 'rxjs';
import { commitResponse } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class CurrentBranchInfoServiceMock {
	get currentBranch() {
		return of(testBranchInfo);
	}
	private _hasPleCategory = signal(true);
	get branchHasPleCategory() {
		return this._hasPleCategory;
	}
	commitBranch(body: {
		committer: string;
		archive: string;
	}): Observable<commitResponse> {
		return of(testCommitResponse);
	}
}
