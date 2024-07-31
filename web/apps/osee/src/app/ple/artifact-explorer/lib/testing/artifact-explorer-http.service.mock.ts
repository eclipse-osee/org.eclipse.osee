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
import { of } from 'rxjs';
import { ArtifactExplorerHttpService } from '../services/artifact-explorer-http.service';
import {
	artifactTypeAttributesMock,
	artifactWithRelationsMock,
} from '@osee/artifact-with-relations/testing';
import { publishingTemplateKeyGroupsMock } from './artifact-explorer.data.mock';

export const ArtifactExplorerHttpServiceMock: Partial<ArtifactExplorerHttpService> =
	{
		getartifactWithRelations(
			branchId: string,
			artifactId: string,
			viewId: string,
			includeRelations: boolean
		) {
			return of(artifactWithRelationsMock);
		},

		getArtifactTypeAttributes(artifactId) {
			return of(artifactTypeAttributesMock);
		},

		getPublishingTemplateKeyGroups() {
			return of(publishingTemplateKeyGroupsMock);
		},
	};
