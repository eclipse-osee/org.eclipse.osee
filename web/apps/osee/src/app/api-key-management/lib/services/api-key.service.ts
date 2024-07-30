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
import { apiURL } from '@osee/environments';
import { ApiKey, keyScope } from '../types/apiKey';

@Injectable({
	providedIn: 'root',
})
export class ApiKeyService {
	constructor(private http: HttpClient) {}

	getApiKeys() {
		return this.http.get<Required<ApiKey>[]>(apiURL + `/orcs/apikeys`);
	}

	getApiScopes() {
		return this.http.get<Required<keyScope>[]>(
			apiURL + `/orcs/apikeys/scopes`
		);
	}

	createApiKey(apiKey: ApiKey) {
		return this.http.post<{ [key: string]: string }>(
			apiURL + `/orcs/apikeys`,
			apiKey
		);
	}

	revokeApiKey(uniqueID: string) {
		return this.http.delete(apiURL + `/orcs/apikeys/${uniqueID}`, {
			responseType: 'text',
		});
	}
}
