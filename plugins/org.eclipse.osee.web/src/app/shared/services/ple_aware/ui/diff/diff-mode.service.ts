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
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class DiffModeService {
	private _isInDiff: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
		false
	);
	constructor() {}

	get isInDiff(): Observable<boolean> {
		return this._isInDiff;
	}

	set DiffMode(value: boolean) {
		this._isInDiff.next(value);
	}
}
