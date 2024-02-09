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
import { ArtifactService } from './artifact.service';
import { NamedId } from '@osee/shared/types';
import { of } from 'rxjs';

export const artifactServiceMock: Partial<ArtifactService> = {
	getAttributeTypes(artifactTypes: NamedId[]) {
		return of([
			{
				id: '1',
				name: 'Attribute Type 1',
			},
			{
				id: '2',
				name: 'Attribute Type 2',
			},
		]);
	},
	getArtifactTypes(filter: string) {
		return of([
			{
				id: '1',
				name: 'Artifact Type 1',
			},
			{
				id: '2',
				name: 'Artifact Type 2',
			},
		]);
	},
};
