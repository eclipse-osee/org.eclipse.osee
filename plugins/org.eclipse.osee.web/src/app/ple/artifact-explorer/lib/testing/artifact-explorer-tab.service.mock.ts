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
import { ArtifactExplorerTabService } from '../services/artifact-explorer-tab.service';
import { signal } from '@angular/core';
import { tab } from '../types/artifact-explorer.data';

export const tabsMock: tab[] = [
	{
		artifact: {
			name: 'Mock Artifact',
			id: '123',
			typeId: '456',
			typeName: 'Mock Type',
			attributes: [
				{
					name: 'Attribute 1',
					value: 'Value 1',
					typeId: '789',
					id: '1',
					baseType: 'String',
				},
			],
			editable: true,
		},
		branchId: '789',
		viewId: '0',
	},
];

export const ArtifactExplorerTabServiceMock: Partial<ArtifactExplorerTabService> =
	{
		Tabs: signal(tabsMock),
	};
