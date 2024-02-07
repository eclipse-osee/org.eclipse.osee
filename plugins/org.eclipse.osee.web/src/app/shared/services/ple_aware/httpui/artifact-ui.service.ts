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
import { Injectable } from '@angular/core';
import { ArtifactService } from '../http/artifact.service';
import { NamedId } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ArtifactUiService {
	constructor(private artifactService: ArtifactService) {}

	getAttributeTypes(artifactTypes: NamedId[]) {
		return this.artifactService.getAttributeTypes(artifactTypes);
	}

	get artifactTypes() {
		return this.artifactService.getArtifactTypes();
	}

	get allAttributeTypes() {
		return this.artifactService.getAttributeTypes([]);
	}
}
