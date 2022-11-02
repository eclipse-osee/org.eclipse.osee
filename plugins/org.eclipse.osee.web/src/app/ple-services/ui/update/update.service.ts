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
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Injectable({
	providedIn: 'root',
})
export class UpdateService {
	private _updateRequired: Subject<boolean> = new Subject<boolean>();

	private _updateOccurred = this._updateRequired.pipe(debounceTime(100));
	constructor() {}

	get update() {
		return this._updateOccurred;
	}

	set updated(value: boolean) {
		this._updateRequired.next(value);
	}
}
