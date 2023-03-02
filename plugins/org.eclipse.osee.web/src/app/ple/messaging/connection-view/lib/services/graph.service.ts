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
import type {
	OseeNode,
	nodeData,
	OseeEdge,
	connection,
} from '@osee/messaging/shared';
import { HttpParamsType } from '@osee/shared/types';
import { apiURL } from 'src/environments/environment';

@Injectable({
	providedIn: 'root',
})
export class GraphService {
	constructor(private http: HttpClient) {}

	getNodes(id: string, viewId: string) {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		return this.http.get<{
			nodes: OseeNode<nodeData>[];
			edges: OseeEdge<connection>[];
		}>(apiURL + '/mim/branch/' + id + '/graph', { params: params });
	}
}
