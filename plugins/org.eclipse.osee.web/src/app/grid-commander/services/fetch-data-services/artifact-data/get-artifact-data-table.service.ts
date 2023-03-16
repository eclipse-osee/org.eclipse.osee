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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { apiURL } from '@osee/environments';
import { UiService } from '@osee/shared/services';
import { ResponseTableData } from '../../../types/grid-commander-types/table-data-types';
import { BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class GetArtifactDataTableService {
	defaultArtifactType = new BehaviorSubject<string>('784');
	defaultBranchId = this.uiService.id;

	constructor(private http: HttpClient, private uiService: UiService) {}

	getArtifactTableData(
		artifactType: string = this.defaultArtifactType.value,
		branchId: string = this.defaultBranchId.value
	) {
		return this.http.get<ResponseTableData>(
			`${apiURL}/orcs/branch/${branchId}/artifact/table?artifactType=${artifactType}`
		);
	}
}
