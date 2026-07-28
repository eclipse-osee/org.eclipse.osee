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
import {
	HttpClient,
	httpResource,
	HttpResourceRef,
} from '@angular/common/http';
import { Injectable, Signal, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { HttpParamsType, NamedId } from '@osee/shared/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

@Injectable({
	providedIn: 'root',
})
export class ArtifactService {
	private http = inject(HttpClient);

	getArtifactTypes(filter: string, excludeAbstract?: boolean) {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		if (excludeAbstract) {
			params = { ...params, excludeAbstract: 'true' };
		}
		return this.http.get<NamedId[]>(apiURL + '/orcs/types/artifact', {
			params: params,
		});
	}

	getArtifactTypesResource(
		filter: Signal<string>,
		excludeAbstract = false
	): HttpResourceRef<NamedId[] | undefined> {
		return httpResource<NamedId[]>(() => {
			const filterValue = filter();
			const params: Record<string, string> = {};
			if (filterValue !== '') {
				params['filter'] = filterValue;
			}
			if (excludeAbstract) {
				params['excludeAbstract'] = 'true';
			}
			return {
				url: apiURL + '/orcs/types/artifact',
				params,
			};
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
		return this.http.get<attribute<string, ATTRIBUTETYPEID>[]>(
			apiURL + '/orcs/types/artifact/' + artifactTypeId + '/attributes'
		);
	}

	public getAttributeEnums(attributeId: string) {
		return this.http.get<string[]>(
			apiURL + '/orcs/types/attribute/' + attributeId + '/enums'
		);
	}
}
