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
import { share, shareReplay } from 'rxjs/operators';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class EnumsService {
	constructor(private http: HttpClient) {}

	private _baseURL = apiURL + '/mim/enums/';
	private _periodicities = this.http
		.get<string[]>(this.baseURL + 'MessagePeriodicities')
		.pipe(share());
	private _categories = this.http
		.get<string[]>(this.baseURL + 'StructureCategories')
		.pipe(share(), shareReplay(1));

	get baseURL() {
		return this._baseURL;
	}

	get periodicities() {
		return this._periodicities;
	}

	get categories() {
		return this._categories;
	}
}
