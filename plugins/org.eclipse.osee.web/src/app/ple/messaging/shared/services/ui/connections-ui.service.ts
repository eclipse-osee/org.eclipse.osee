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
import { UiService } from '@osee/shared/services';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class ConnectionsUiService {
	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);
	private _filter: BehaviorSubject<string> = new BehaviorSubject<string>('');
	constructor(private ui: UiService) {}

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

	set update(value: boolean) {
		this.ui.updated = value;
	}

	get BranchId() {
		return this.ui.id;
	}

	set branchId(value: string) {
		this.ui.idValue = value;
	}

	get viewId() {
		return this.ui.viewId;
	}

	set ViewId(id: string) {
		this.ui.viewIdValue = id;
	}

	get currentPage() {
		return this._currentPage$;
	}

	set page(page: number) {
		this._currentPage$.next(page);
	}

	get currentPageSize(): Observable<number> {
		return this._currentPageSize$;
	}
	set pageSize(page: number) {
		this._currentPageSize$.next(page);
	}
	returnToFirstPage() {
		this.page = 0;
	}
}
