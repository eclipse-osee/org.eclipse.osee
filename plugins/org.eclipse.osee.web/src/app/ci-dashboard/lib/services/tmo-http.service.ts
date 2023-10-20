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
import { SetReference } from '../types/tmo';
import { DefReference } from '../types/tmo';
import { ResultReference } from '../types/tmo';
import { TestCaseReference } from '../types/tmo';
import { TestPointReference } from '../types/tmo';
import { apiURL } from '@osee/environments';
import { CIStats } from '../types/ci-stats';

@Injectable({
	providedIn: 'root',
})
export class TmoHttpService {
	constructor(private http: HttpClient) {}

	getSetList(branchId: string | number) {
		return this.http.get<SetReference[]>(
			`${apiURL}/script/tmo/${branchId}/set`
		);
	}

	getScriptDefList(branchId: string | number, setId: string | number) {
		return this.http.get<DefReference[]>(
			`${apiURL}/script/tmo/${branchId}/def/set/${setId}`
		);
	}

	getScriptResultList(branchId: string | number) {
		return this.http.get<ResultReference[]>(
			`${apiURL}/script/tmo/${branchId}/result`
		);
	}

	getTestCaseList(branchId: string | number) {
		return this.http.get<TestCaseReference[]>(
			`${apiURL}/script/tmo/${branchId}/case`
		);
	}

	getTestPointList(branchId: string | number) {
		return this.http.get<TestPointReference[]>(
			`${apiURL}/script/tmo/${branchId}/point`
		);
	}
}
