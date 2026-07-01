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
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	artifactWithRelations,
	artifactTypeIcon,
} from '@osee/artifact-with-relations/types';
import { applicabilitySentinel } from '@osee/applicability/types';

export const artifactTypeIconMock: artifactTypeIcon = {
	icon: 'insert_drive_file',
	color: 'primary',
	lightShade: '500',
	darkShade: '500',
	variant: '',
};

export const artifactTypeAttributesMock: attribute<string, ATTRIBUTETYPEID>[] =
	[
		{
			name: 'subsystem',
			value: 'data management',
			typeId: '7' as ATTRIBUTETYPEID,
			id: '11111',
			gammaId: '-1',
			storeType: 'Enumeration',
		},
	];

export const artifactWithRelationsMock: artifactWithRelations = {
	name: 'test',
	id: '1234',
	gammaId: '-1',
	typeId: '1111',
	typeName: 'requirement',
	icon: artifactTypeIconMock,
	attributes: artifactTypeAttributesMock,
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
							gammaId: '-1',
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
									typeId: '789' as ATTRIBUTETYPEID,
									id: '1',
									gammaId: '-1',
									storeType: 'String',
								},
							],
							relations: [],
							editable: true,
							operationTypes: [],
							applicability: applicabilitySentinel,
						},
					],
					isSideA: true,
					isSideB: false,
					gammaIds: ['-1'],
				},
			],
		},
	],
	editable: true,
	operationTypes: [],
	applicability: applicabilitySentinel,
};
