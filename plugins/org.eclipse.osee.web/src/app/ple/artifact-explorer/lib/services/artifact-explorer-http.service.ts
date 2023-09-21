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
import { Observable } from 'rxjs';
import { artifactWithDirectRelations } from '../types/artifact-explorer.data';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerHttpService {
	constructor(private http: HttpClient) {}

	public getDirectRelations(
		branchId: string,
		artifactId: string,
		viewId: string
	): Observable<artifactWithDirectRelations> {
		return this.http.get<artifactWithDirectRelations>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/artifact/' +
				artifactId +
				'/related/direct',
			{
				params: { viewId: viewId },
			}
		);
	}

	public getAttributeEnums(
		branchId: string,
		artifactId: string,
		attributeId: string
	): Observable<string[]> {
		return this.http.get<string[]>(
			apiURL +
				'/orcs/branch/' +
				branchId +
				'/artifact/' +
				artifactId +
				'/attribute/' +
				attributeId +
				'/enums'
		);
	}
}
