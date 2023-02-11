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
import { HttpMethods } from '@osee/shared/types';
import { apiURL } from 'src/environments/environment';

@Injectable({
	providedIn: 'root',
})
export class AttributeService {
	constructor(private http: HttpClient) {}

	public getMarkDownContent(
		branchId: string,
		artifactID: string,
		attributeID: string
	) {
		return this.http.request(
			HttpMethods.GET,
			`${apiURL}/orcs/branch/${branchId}/artifact/${artifactID}/attribute/type/${attributeID}`,
			{ responseType: 'text' }
		);
	}
}
