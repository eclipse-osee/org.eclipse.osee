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
import { Injectable, signal, inject } from '@angular/core';

import { UiService } from '@osee/shared/services';
import { MimRouteService } from './mim-route.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class MessageUiService {
	private _mimRoute = inject(MimRouteService);
	private ui = inject(UiService);

	public filter = signal('');
	public currentPageSize = signal(50);
	public currentPage = signal(0);

	get UpdateRequired() {
		return this.ui.update;
	}

	set updateMessages(value: boolean) {
		this.ui.updated = value;
	}

	get BranchId() {
		return this._mimRoute.id;
	}

	get type() {
		return this._mimRoute.type;
	}

	set typeValue(value: 'working' | 'baseline' | '') {
		this._mimRoute.typeValue = value;
	}

	set BranchIdString(value: string) {
		this._mimRoute.idValue = value;
	}

	connectionIdSignal = this._mimRoute.connectionId;

	connectionId = toObservable(this._mimRoute.connectionId);

	set connectionIdString(value: `${number}`) {
		this._mimRoute.connectionIdString = value;
	}

	get viewId() {
		return this.ui.viewId;
	}

	set DiffMode(value: boolean) {
		this._mimRoute.diffMode = value;
	}

	get isInDiff() {
		return this._mimRoute.isInDiff;
	}

	set messageId(value: string) {
		this._mimRoute.messageIdString = value;
	}

	set subMessageId(value: `${number}`) {
		this._mimRoute.submessageIdString = value;
	}

	set subMessageToStructureBreadCrumbs(value: string) {
		this._mimRoute.submessageToStructureBreadCrumbsString = value;
	}

	set singleStructureId(value: string) {
		this._mimRoute.singleStructureIdValue = value;
	}
}
