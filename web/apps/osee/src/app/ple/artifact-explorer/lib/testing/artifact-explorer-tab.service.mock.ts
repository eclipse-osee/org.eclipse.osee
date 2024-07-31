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
import { tab } from '../types/artifact-explorer';

export const tabsMock: tab[] = [
	{
		tabId: '1',
		tabType: 'Artifact',
		tabTitle: '',
		artifact: {
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
			relations: [],
			editable: true,
			operationTypes: [],
		},
		branchId: '789',
		viewId: '0',
	},
];

export const ArtifactExplorerTabServiceMock: Partial<ArtifactExplorerTabService> =
	{
		Tabs: signal(tabsMock),
	};
