/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Injectable, inject } from '@angular/core';
import { apiURL } from '../../../../environments';
import { world } from '../actra-world';

@Injectable({
	providedIn: 'root',
})
export class WorldHttpService {
	private http = inject(HttpClient);

	getWorldDataMy() {
		return this.http.get<world>(apiURL + `/ats/world/my`);
	}
	getWorldData(collId: string, custId: string) {
		return this.http.get<world>(
			apiURL + `/ats/world/coll/${collId}/json/${custId}`
		);
	}
}
