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
import { shareReplay } from 'rxjs/operators';
import { MimRouteService } from './mim-route.service';
import { changeInstance } from '@osee/shared/types/change-report';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class StructuresUiService {
	private _filter: BehaviorSubject<string> = new BehaviorSubject<string>('');

	private _messageId: BehaviorSubject<string> = new BehaviorSubject<string>(
		'0'
	);
	private _subMessageId: BehaviorSubject<string> =
		new BehaviorSubject<string>('0');
	private _connectionId: BehaviorSubject<string> =
		new BehaviorSubject<string>('0');
	private _differences = new BehaviorSubject<changeInstance[] | undefined>(
		undefined
	);
	private _done = new Subject();
	constructor(
		private ui: UiService,
		private _mimRoute: MimRouteService
	) {}

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

	set BranchIdString(value: string) {
		this._mimRoute.idValue = value;
	}

	get messageId() {
		return this._mimRoute.messageId;
	}

	set messageIdString(value: string) {
		this._mimRoute.messageIdString = value;
	}

	get subMessageId() {
		return this._mimRoute.submessageId;
	}

	set subMessageIdString(value: string) {
		this._mimRoute.submessageIdString = value;
	}

	get connectionId() {
		return this._mimRoute.connectionId;
	}

	set connectionIdString(value: string) {
		this._mimRoute.connectionIdString = value;
	}

	get viewId() {
		return this.ui.viewId;
	}

	set ViewId(id: string) {
		this.ui.viewIdValue = id;
	}

	get branchType() {
		return this._mimRoute.type;
	}

	set BranchType(value: string) {
		this._mimRoute.typeValue = value;
	}

	set DiffMode(value: boolean) {
		this._mimRoute.diffMode = value;
	}

	get isInDiff() {
		return this._mimRoute.isInDiff;
	}
	get differences() {
		return this._differences.pipe(
			shareReplay({ refCount: true, bufferSize: 1 })
		);
	}
	set difference(value: changeInstance[]) {
		this._differences.next(value);
	}
	set toggleDone(value: any) {
		this._done.next(value);
		this._done.complete();
	}

	get done() {
		return this._done;
	}

	get subMessageBreadCrumbs() {
		return this._mimRoute.submessageToStructureBreadCrumbs;
	}

	set subMessageBreadCrumbsString(value: string) {
		this._mimRoute.submessageToStructureBreadCrumbsString = value;
	}
	get singleStructureId() {
		return this._mimRoute.singleStructureId;
	}

	set singleStructureIdValue(value: string) {
		this._mimRoute.singleStructureIdValue = value;
	}
}
