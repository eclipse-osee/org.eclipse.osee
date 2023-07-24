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
import { BehaviorSubject, ReplaySubject } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { changeInstance } from '@osee/shared/types/change-report';

@Injectable({
	providedIn: 'root',
})
export class PlConfigUIStateService {
	private _differences = new ReplaySubject<changeInstance[] | undefined>(
		undefined
	);
	private _editable = new BehaviorSubject<string>('false');
	private _groups = new BehaviorSubject<string[]>([]);
	constructor(private ui: UiService) {}

	public set viewBranchTypeString(branchType: string) {
		this.ui.typeValue = branchType?.toLowerCase();
		this.updateReqConfig = true;
	}

	public get viewBranchType() {
		return this.ui.type;
	}

	public set branchIdNum(branchId: string) {
		this.ui.idValue = branchId;
	}
	public get branchId() {
		return this.ui.id.pipe(shareReplay({ bufferSize: 1, refCount: true }));
	}
	public set updateReqConfig(updateReq: boolean) {
		this.ui.updated = updateReq;
	}
	public get updateReq() {
		return this.ui.update;
	}
	public get loading() {
		return this.ui.isLoading;
	}
	public set loadingValue(loading: boolean | string) {
		this.ui.loading = loading as boolean;
	}
	public get editable() {
		return this._editable;
	}
	public set editableValue(edit: boolean | string) {
		this.editable.next(edit.toString());
	}
	public get errors() {
		return this.ui.errorText;
	}
	public set error(errorString: string) {
		this.ui.ErrorText = errorString;
	}
	public set groupsString(groups: string[]) {
		this._groups.next(groups);
	}
	public get groups() {
		return this._groups;
	}
	get differences() {
		return this._differences.pipe(
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}
	set difference(value: changeInstance[]) {
		this._differences.next(value);
	}

	get isInDiff() {
		return this.ui.isInDiff.pipe(
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	set diffMode(value: boolean) {
		this.ui.diffMode = value;
	}
}
