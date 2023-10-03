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
import { BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class BranchTypeService {
	private _branchType: BehaviorSubject<'working' | 'baseline' | ''> =
		new BehaviorSubject<'working' | 'baseline' | ''>('');
	constructor() {}
	get branchType() {
		return this._branchType;
	}

	set BranchType(value: 'working' | 'baseline' | '') {
		this._branchType.next(value);
	}
}
