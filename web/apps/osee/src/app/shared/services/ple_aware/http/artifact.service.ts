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
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { HttpParamsType, NamedId, attribute } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ArtifactService {
	private http = inject(HttpClient);

	getArtifactTypes(filter: string) {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<NamedId[]>(apiURL + '/orcs/types/artifact', {
			params: params,
		});
	}

	getAttributeTypes(artifactTypes: NamedId[]) {
		let params: HttpParamsType = {};
		if (artifactTypes) {
			params = {
				...params,
				artifactType: artifactTypes.map((a) => a.id),
			};
		}
		return this.http.get<NamedId[]>(apiURL + '/orcs/types/attribute', {
			params: params,
		});
	}

	public getArtifactTypeAttributes(artifactTypeId: `${number}`) {
		return this.http.get<attribute[]>(
			apiURL + '/orcs/types/artifact/' + artifactTypeId + '/attributes'
		);
	}

	public getAttributeEnums(attributeId: string) {
		return this.http.get<string[]>(
			apiURL + '/orcs/types/attribute/' + attributeId + '/enums'
		);
	}
}
