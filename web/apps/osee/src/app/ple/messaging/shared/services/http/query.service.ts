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
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import type { MimQuery } from '../../query/MimQuery';

@Injectable({
	providedIn: 'root',
})
export class QueryService {
	private http = inject(HttpClient);

	/**
	 * Returns a typed query element
	 * @param branchId branch to get information from
	 * @param query Typed Query
	 * @returns query result typed as @type Observable<T>
	 */
	query<T>(branchId: string, query: MimQuery<T>) {
		return this.http.post<Required<T>[]>(
			apiURL + '/mim/branch/' + branchId + '/query',
			query
		);
	}

	/**
	 * Returns a typed query element
	 * @param branchId branch to get information from
	 * @param query Typed Query
	 * @returns query result typed as @type Observable<T>
	 */
	queryExact<T>(branchId: string, query: MimQuery<T>) {
		return this.http.post<Required<T>[]>(
			apiURL + '/mim/branch/' + branchId + '/query/exact',
			query
		);
	}
}
