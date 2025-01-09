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
import { Injectable, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UiService } from '@osee/shared/services';
import { BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CiDashboardUiService {
	private uiService = inject(UiService);
	private router = inject(Router);
	private route = inject(ActivatedRoute);

	private _ciSetId = new BehaviorSubject<string>('-1');

	constructor() {
		this.route.queryParamMap?.subscribe((queryParams) => {
			const setId = queryParams.get('set');
			if (setId !== null) {
				this.CiSetId = setId;
			} else {
				this.CiSetId = '-1';
			}
		});
	}

	get branchType() {
		return this.uiService.type;
	}

	set BranchType(type: '' | 'working' | 'baseline') {
		this.uiService.typeValue = type;
	}

	get branchId() {
		return this.uiService.id;
	}

	set BranchId(branchId: string) {
		this.uiService.idValue = branchId;
	}

	get ciSetId() {
		return this._ciSetId;
	}

	set CiSetId(id: string) {
		this._ciSetId.next(id);
	}

	get updateRequired() {
		return this.uiService.update;
	}

	set update(value: boolean) {
		this.uiService.updated = value;
	}

	routeToSet(id: string) {
		this.CiSetId = id;
		const tree = this.router.parseUrl(this.router.url);
		const queryParams = tree.queryParams;
		if (!id || id === '' || id === '-1') {
			delete queryParams['set'];
		} else {
			queryParams['set'] = id;
		}
		this.router.navigate([], { queryParams: queryParams });
	}
}
