/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { apiURL } from '../../../environments';
import { world } from '../world';

@Injectable({
	providedIn: 'root',
})
export class WorldHttpService {
	constructor(private http: HttpClient) {}

	getWorldData(collId: string, custId: string) {
		return this.http.get<world>(
			apiURL + `/ats/world/coll/${collId}/json/${custId}`
		);
	}
	getWorldDataStored(collId: string) {
		return this.http.get<world>(
			apiURL + `/ats/world/coll/${collId}/worldresults`
		);
	}
	publishWorldData(collId: string, custId: string, world: world) {
		return this.http.put<world>(
			apiURL + `/ats/world/coll/${collId}/json/${custId}/publish`,
			world
		);
	}
}
