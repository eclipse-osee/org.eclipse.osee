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
import { BehaviorSubject, Subject } from 'rxjs';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { MimRouteService } from '../../shared/services/ui/mim-route.service';

@Injectable({
	providedIn: 'root',
})
export class MessageUiService {
	private _filter: BehaviorSubject<string> = new BehaviorSubject<string>('');
	constructor(private _mimRoute: MimRouteService, private ui: UiService) {}

	get filter() {
		return this._filter;
	}

	set filterString(filter: string) {
		if (filter !== this._filter.getValue()) {
			this._filter.next(filter);
		}
	}

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

	set typeValue(value: string) {
		this._mimRoute.typeValue = value;
	}

	set BranchIdString(value: string) {
		this._mimRoute.idValue = value;
	}

	get connectionId() {
		return this._mimRoute.connectionId;
	}

	set connectionIdString(value: string) {
		this._mimRoute.connectionIdString = value;
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

	set subMessageId(value: string) {
		this._mimRoute.submessageIdString = value;
	}

	set subMessageToStructureBreadCrumbs(value: string) {
		this._mimRoute.submessageToStructureBreadCrumbsString = value;
	}

	set singleStructureId(value: string) {
		this._mimRoute.singleStructureIdValue = value;
	}
}
