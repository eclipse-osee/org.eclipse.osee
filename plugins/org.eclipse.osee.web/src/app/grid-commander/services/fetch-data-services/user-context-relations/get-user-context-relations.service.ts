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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from '../../../../../environments/environment';
import { UsersContext } from '../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { GCBranchIdService } from '../branch/gc-branch-id.service';

@Injectable({
	providedIn: 'root',
})
export class GetUserContextRelationsService {
	private defaultBranchId = this.branchIdService.branchId;

	constructor(
		private http: HttpClient,
		private branchIdService: GCBranchIdService
	) {}

	getResponseUserContextData(): Observable<UsersContext[]> {
		return this.http.get<UsersContext[]>(
			`${apiURL}/orcs/branch/${this.defaultBranchId}/gc/user/commands`
		);
	}
}
