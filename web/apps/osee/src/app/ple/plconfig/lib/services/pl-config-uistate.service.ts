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
import { Injectable, inject, signal } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { changeInstance } from '@osee/shared/types/change-report';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class PlConfigUIStateService {
	private ui = inject(UiService);

	private _differences = new ReplaySubject<changeInstance[] | undefined>(
		undefined
	);
	public filter = signal('');

	public filter$ = toObservable(this.filter);
	public currentPage = signal<number>(0);
	public currentPage$ = toObservable(this.currentPage);
	public currentPageSize = signal<number>(100);
	public currentPageSize$ = toObservable(this.currentPageSize);

	public set viewBranchTypeString(branchType: 'working' | 'baseline' | '') {
		this.ui.typeValue = branchType;
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
	public get errors() {
		return this.ui.errorText;
	}
	public set error(errorString: string) {
		this.ui.ErrorText = errorString;
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

	returnToFirstPage() {
		this.currentPage.set(0);
	}
}
