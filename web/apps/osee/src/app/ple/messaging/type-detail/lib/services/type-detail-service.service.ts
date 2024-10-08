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
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class TypeDetailService {
	private _ui = inject(UiService);

	private _typeId: BehaviorSubject<string> = new BehaviorSubject<string>('');

	get typeId() {
		return this._typeId;
	}

	set type(value: string) {
		this._typeId.next(value);
	}
	get id() {
		return this._ui.id;
	}

	set idValue(value: string) {
		this._ui.idValue = value;
	}

	get branchType() {
		return this._ui.type;
	}

	set typeValue(value: 'working' | 'baseline' | '') {
		this._ui.typeValue = value;
	}
}
