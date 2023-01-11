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
import { apiURL } from '../../../../../../environments/environment';
import { elementWithPathsAndButtons } from '../../../shared/types/element.d';

@Injectable({
	providedIn: 'root',
})
export class PlatformTypesService {
	constructor(private http: HttpClient) {}

	getFilteredElements(filter: string, branchId: string) {
		return this.http.get<elementWithPathsAndButtons[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/elements/types/filter/' +
				filter
		);
	}
}
