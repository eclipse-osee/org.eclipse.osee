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
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import { artifactContextMenuOption } from './artifact-explorer';

export const DEFAULT_ARTIFACT_CONTEXT_MENU_OPTIONS: artifactContextMenuOption[] =
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
		},
	];

export const DEFAULT_HIERARCHY_ROOT_ARTIFACT: artifactWithRelations = {
	id: '197818',
	name: 'Default Hierarchy Root',
	typeId: '10',
	typeName: 'Root Artifact',
	icon: {
		icon: '',
		color: '',
		lightShade: '',
		darkShade: '',
		variant: '',
	},
	attributes: [],
	relations: [],
	editable: false,
};
