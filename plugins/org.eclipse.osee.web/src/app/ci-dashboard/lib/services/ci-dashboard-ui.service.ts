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
import { Router } from '@angular/router';
import { UiService } from '@osee/shared/services';
import { BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CiDashboardUiService {
	constructor(
		private uiService: UiService,
		private router: Router
	) {}

	private _ciSetId = new BehaviorSubject<string>('-1');

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

	// Sets the current CI Set and adds the CI Set ID to the current url.
	// Assumes the CI Set ID always belongs at the end of the url
	routeToSet(id: string) {
		const formattedSetId = this.ciSetId.getValue().replace('-', '%2D');
		const formattedId = id.replace('-', '%2D');
		let url = this.router.url;
		if (url.includes(formattedSetId)) {
			url = url.split(formattedSetId)[0] + formattedId;
		} else if (url.endsWith(this.branchId.getValue())) {
			url = url + '/' + formattedId;
		}
		this.router.navigateByUrl(url);
	}
}
