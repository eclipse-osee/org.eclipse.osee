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
import type { elementDiffItem } from '@osee/messaging/shared/types';
import type { headerDetail } from '@osee/shared/types';

export const elementDiffHeaderDetails: headerDetail<elementDiffItem>[] = [
	{
		header: 'name',
		description: 'Name of element',
		humanReadable: 'Name',
	},
	{
		header: 'description',
		description: 'Description of element',
		humanReadable: 'Description',
	},
	{
		header: 'logicalType',
		description: 'Logical type of element',
		humanReadable: 'Logical Type',
	},
	{
		header: 'elementSizeInBits',
		description: 'Bit size of element',
		humanReadable: 'Bit Size',
	},
	{
		header: 'interfaceElementIndexStart',
		description: 'Minimum value of element',
		humanReadable: 'Start Index',
	},
	{
		header: 'interfaceElementIndexEnd',
		description: 'Minimum value of element',
		humanReadable: 'End Index',
	},
	{
		header: 'interfacePlatformTypeMinval',
		description: 'Minimum value of element',
		humanReadable: 'Min. Val',
	},
	{
		header: 'interfacePlatformTypeMaxval',
		description: 'Maximum value of element',
		humanReadable: 'Max. Val',
	},
	{
		header: 'interfaceDefaultValue',
		description: 'Default value of element',
		humanReadable: 'Default Val',
	},
	{
		header: 'units',
		description: 'Element units',
		humanReadable: 'Units',
	},
	{
		header: 'enumeration',
		description: 'Element enumeration',
		humanReadable: 'Enumeration',
	},
	{
		header: 'interfaceElementAlterable',
		description: 'Alterability of element',
		humanReadable: 'Alterable',
	},
	{
		header: 'enumLiteral',
		description: 'Enumerated Literals of element',
		humanReadable: 'Enumerated Literals',
	},
	{
		header: 'notes',
		description: 'Element notes',
		humanReadable: 'Notes',
	},
	{
		header: 'applicability',
		description: 'Applicability of the element',
		humanReadable: 'Applicability',
	},
];
