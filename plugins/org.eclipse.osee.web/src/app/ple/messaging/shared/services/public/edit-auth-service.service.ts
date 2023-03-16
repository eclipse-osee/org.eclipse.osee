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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import type { branchApplicability } from '@osee/messaging/shared/types';
import { BehaviorSubject, iif, Observable, of } from 'rxjs';
import { switchMap, share } from 'rxjs/operators';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class EditAuthService {
	private _branchId: BehaviorSubject<string> = new BehaviorSubject<string>(
		'0'
	);
	_branchEditability: Observable<branchApplicability> = this.BranchId.pipe(
		switchMap((value: string) =>
			iif(
				() =>
					value !== '' &&
					value != '0' &&
					value != '-1' &&
					value !== undefined &&
					value !== null,
				this.getBranchInfo(value).pipe(share()),
				of({
					associatedArtifactId: '-1',
					branch: {
						id: '-1',
						viewId: '-1',
						idIntValue: -1,
						name: '',
					},
					editable: false,
					features: [],
					groups: [],
					parentBranch: {
						id: '-1',
						viewId: '-1',
						idIntValue: -1,
						name: '',
					},
					views: [],
				})
			)
		)
	);
	constructor(private http: HttpClient) {}

	private getBranchInfo(id: string | number) {
		return this.http.get<branchApplicability>(
			apiURL + '/orcs/applicui/branch/' + id
		);
	}

	private get BranchId() {
		return this._branchId;
	}

	set BranchIdString(value: string) {
		this._branchId.next(value);
	}
	get branchEditability() {
		return this._branchEditability;
	}
}
