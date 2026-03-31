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
import { Injectable, inject } from '@angular/core';
import { ArtifactService } from '../http/artifact.service';
import { NamedId } from '@osee/shared/types';

type SavedSearchFilters = {
	artifactTypes: NamedId[];
	attributeTypes: NamedId[];
	exactMatch: boolean;
	searchById: boolean;
	global?: boolean;
};

@Injectable({
	providedIn: 'root',
})
export class ArtifactUiService {
	private artifactService = inject(ArtifactService);

	getArtifactTypes(filter: string) {
		return this.artifactService.getArtifactTypes(filter);
	}

	getAttributeTypes(artifactTypes: NamedId[]) {
		return this.artifactService.getAttributeTypes(artifactTypes);
	}

	getArtifactTypeAttributes(artifactTypeId: `${number}`) {
		return this.artifactService.getArtifactTypeAttributes(artifactTypeId);
	}

	getAttributeEnums(attributeId: string) {
		return this.artifactService.getAttributeEnums(attributeId);
	}

	get allArtifactTypes() {
		return this.artifactService.getArtifactTypes('');
	}

	get allAttributeTypes() {
		return this.artifactService.getAttributeTypes([]);
	}

	/**
	 * Save an advanced search via the underlying HTTP service
	 */
	saveSearch(
		title: string,
		query: string,
		searchCriteria: SavedSearchFilters
	) {
		return this.artifactService.saveSearch(title, query, searchCriteria);
	}
}
