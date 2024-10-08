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
import { Injectable, signal, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class MimRouteService {
	private _ui = inject(UiService);

	connectionId = signal<`${number}`>('-1'); //'0'
	private readonly _messageId: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	private _subMessageId = signal<`${number}`>('-1');
	private readonly _subMessageToStructurebreadCrumbs: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	private readonly _singleStructureId: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	// public readonly connectionId = this._connectionId.asObservable();
	public readonly messageId = this._messageId.asObservable();
	public submessageId = this._subMessageId;
	public readonly submessageToStructureBreadCrumbs =
		this._subMessageToStructurebreadCrumbs.asObservable();
	public readonly singleStructureId = this._singleStructureId.asObservable();

	get type() {
		return this._ui.type;
	}
	get id() {
		return this._ui.idAsObservable;
	}

	get viewId() {
		return this._ui.viewId;
	}

	set typeValue(value: 'working' | 'baseline' | '') {
		this._ui.typeValue = value;
	}
	set idValue(value: string) {
		this._ui.idValue = value;
	}
	get isInDiff() {
		return this._ui.isInDiff;
	}

	set diffMode(value: boolean) {
		this._ui.diffMode = value;
	}

	set connectionIdString(value: `${number}`) {
		this.connectionId.set(value);
	}

	set messageIdString(value: string) {
		this._messageId.next(value);
	}

	set submessageIdString(value: `${number}`) {
		this._subMessageId.set(value);
	}

	set submessageToStructureBreadCrumbsString(value: string) {
		this._subMessageToStructurebreadCrumbs.next(value);
	}

	set singleStructureIdValue(value: string) {
		this._singleStructureId.next(value);
	}

	get updated() {
		return this._ui.update;
	}

	set update(value: boolean) {
		this._ui.updated = value;
	}
}
