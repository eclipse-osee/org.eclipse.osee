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
import { workType } from '@osee/shared/types/configuration-management';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class WorktypeService {
	private _workType = new BehaviorSubject<workType>('None');
	private workType$ = this._workType.asObservable();
	constructor() {}
	get workType(): Observable<workType> {
		return this.workType$;
	}

	set workType(value: workType) {
		this._workType.next(value);
	}
}
