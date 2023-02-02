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

@Injectable({
	providedIn: 'root',
})
export class GCBranchIdService {
	constructor() {}

	/**anything User related or ATS related stays on 570 everything else would need to use the UI service
	 * as more commands are added to GC that do things on different branches
	 * we will have to leverage the UiService to determine the branch
	 * **/

	private _branchId = '570';

	public get branchId() {
		return this._branchId;
	}
	public set branchId(value) {
		this._branchId = value;
	}
}
