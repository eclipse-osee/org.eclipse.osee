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
import { attribute } from '@osee/shared/types';
import {
	artifactTypeIcon,
	artifactContextMenuOption,
	artifact,
} from '../types/artifact-explorer.data';

export const artifactTypeIconMock: artifactTypeIcon = {
	icon: 'insert_drive_file',
	color: 'primary',
	lightShade: '500',
	darkShade: '500',
	variant: '',
};

export const artifactContextMenuOptionMock: artifactContextMenuOption = {
	name: 'create',
	icon: artifactTypeIconMock,
};

export const artifactTypeAttributesMock: attribute[] = [
	{
		name: 'subsystem',
		value: 'data management',
		typeId: '7',
		id: '11111',
		storeType: 'Enumeration',
		multiplicityId: '2',
	},
];

export const artifactMock: artifact = {
	name: 'test',
	id: '1234',
	typeId: '1111',
	typeName: 'requirement',
	icon: artifactTypeIconMock,
	attributes: artifactTypeAttributesMock,
	editable: true,
};
