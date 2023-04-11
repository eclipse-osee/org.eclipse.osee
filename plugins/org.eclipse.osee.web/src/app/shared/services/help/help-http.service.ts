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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from '@osee/environments';
import { HelpPage } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class HelpHttpService {
	constructor(private http: HttpClient) {}

	getHelpPage(id: string) {
		return this.http.get<HelpPage>(apiURL + '/orcs/help/' + id);
	}

	getHelpPages(appName: string) {
		return this.http.get<HelpPage[]>(apiURL + '/orcs/help', {
			params: { app: appName },
		});
	}
}
