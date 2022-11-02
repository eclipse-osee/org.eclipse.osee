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
import { BehaviorSubject } from 'rxjs';
import { UiService } from '../../../../../ple-services/ui/ui.service';

@Injectable({
	providedIn: 'root',
})
export class MimRouteService {
	private readonly _connectionId: BehaviorSubject<string> =
		new BehaviorSubject<string>('0');
	private readonly _messageId: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	private readonly _subMessageId: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	private readonly _subMessageToStructurebreadCrumbs: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	private readonly _singleStructureId: BehaviorSubject<string> =
		new BehaviorSubject<string>('');
	public readonly connectionId = this._connectionId.asObservable();
	public readonly messageId = this._messageId.asObservable();
	public readonly submessageId = this._subMessageId.asObservable();
	public readonly submessageToStructureBreadCrumbs =
		this._subMessageToStructurebreadCrumbs.asObservable();
	public readonly singleStructureId = this._singleStructureId.asObservable();
	constructor(private _ui: UiService) {}

	get type() {
		return this._ui.type;
	}
	get id() {
		return this._ui.idAsObservable;
	}

	set typeValue(value: string) {
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

	set connectionIdString(value: string) {
		this._connectionId.next(value);
	}

	set messageIdString(value: string) {
		this._messageId.next(value);
	}

	set submessageIdString(value: string) {
		this._subMessageId.next(value);
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
