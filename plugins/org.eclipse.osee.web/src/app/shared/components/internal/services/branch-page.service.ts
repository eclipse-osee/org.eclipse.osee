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
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class BranchPageService {
	private _pageSize = new BehaviorSubject<number>(10);
	private pageSize$ = this._pageSize.asObservable();
	constructor() {}

	get pageSize(): Observable<number> {
		return this.pageSize$;
	}

	set pageSize(value: number) {
		this._pageSize.next(value);
	}
}
