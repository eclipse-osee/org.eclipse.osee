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
import { artifactContextMenuOption } from './artifact-explorer.data';

export const DEFUALT_ARTIFACT_CONTEXT_MENU_OPTIONS: artifactContextMenuOption[] =
	[
		{
			name: 'Create Child Artifact',
			icon: {
				icon: 'add',
				color: 'success',
				lightShade: '500',
				darkShade: '500',
				variant: '',
			},
			excludedArtifactTypes: [],
		},
		{
			name: 'Delete Artifact',
			icon: {
				icon: 'delete',
				color: 'warning',
				lightShade: '500',
				darkShade: '500',
				variant: '',
			},
			excludedArtifactTypes: [],
		},
	];
