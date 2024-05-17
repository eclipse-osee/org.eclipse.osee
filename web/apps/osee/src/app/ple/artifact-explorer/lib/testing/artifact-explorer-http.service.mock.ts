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
import { artifactWithDirectRelations } from '../types/artifact-explorer.data';
import { artifactMock, artifactTypeAttributesMock } from '@osee/shared/testing';

export const ArtifactExplorerHttpServiceMock: Partial<ArtifactExplorerHttpService> =
	{
		getDirectRelations(branchId, artifactId, viewId) {
			return of(artifactWithDirectRelationsMock);
		},

		getArtifactTypeAttributes(artifactId) {
			return of(artifactTypeAttributesMock);
		},

		getArtifactForTab(branchId, artifactId) {
			return of(artifactMock);
		},
	};

export const artifactWithDirectRelationsMock: artifactWithDirectRelations = {
	artId: '777',
	artName: 'Test Artifact',
	artType: '',
	relations: [
		{
			relationTypeToken: {
				id: '1234',
				idIntValue: 1234,
				idString: '1234',
				multiplicity: '',
				name: 'rel',
				newRelationTable: true,
				order: '',
				ordered: false,
				relationArtifactType: '',
			},
			relationSides: [
				{
					name: 'relSide',
					artifacts: [
						{
							name: 'Mock Artifact',
							id: '123',
							typeId: '456',
							typeName: 'Mock Type',
							icon: {
								icon: 'folder',
								color: 'accent',
								lightShade: '400',
								darkShade: '400',
								variant: '',
							},
							attributes: [
								{
									name: 'Attribute 1',
									value: 'Value 1',
									typeId: '789',
									id: '1',
									storeType: 'String',
									multiplicityId: '2',
								},
							],
							editable: true,
						},
					],
					isSideA: true,
					isSideB: false,
				},
			],
		},
	],
};
