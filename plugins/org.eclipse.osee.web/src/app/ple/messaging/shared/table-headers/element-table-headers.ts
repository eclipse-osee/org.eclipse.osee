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
import { headerDetail } from '@osee/shared/types';
import { elementImportToken } from '../types';

export const importElementHeaderDetails: headerDetail<elementImportToken>[] = [
	{
		header: 'name',
		description: 'Name of element',
		humanReadable: 'Name',
	},
	{
		header: 'interfaceElementIndexStart',
		description: 'Starting Index of Element Array',
		humanReadable: 'Start Index',
	},
	{
		header: 'interfaceElementIndexEnd',
		description: 'End Index of Element Array',
		humanReadable: 'End Index',
	},
	{
		header: 'interfaceDefaultValue',
		description: 'Default value of Element or Element Array',
		humanReadable: 'Default',
	},
	{
		header: 'interfaceElementAlterable',
		description: 'Whether or not a given Element is alterable',
		humanReadable: 'Alterable',
	},
	{
		header: 'description',
		description: 'Description of a given element',
		humanReadable: 'Description',
	},
	{
		header: 'notes',
		description:
			'Notes corresponding to a given element, for example, specific enum literal descriptions for a given element',
		humanReadable: 'Notes',
	},
	{
		header: 'enumLiteral',
		description: 'Enumerated Literals of Element',
		humanReadable: 'Enumerated Literals',
	},
];
