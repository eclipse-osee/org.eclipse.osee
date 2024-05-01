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
import { platformTypeImportToken } from '../types';

export const importPlatformTypeHeaderDetails: headerDetail<platformTypeImportToken>[] =
	[
		{
			header: 'name',
			description: 'Platform Type name',
			humanReadable: 'Name',
		},
		{
			header: 'description',
			description: 'Description of the platform type',
			humanReadable: 'Description',
		},
		{
			header: 'interfaceDefaultValue',
			description: 'Default value of the platform type',
			humanReadable: 'Default Value',
		},
		{
			header: 'interfacePlatformTypeMinval',
			description: 'Minimum value of the platform type',
			humanReadable: 'Min. Val',
		},
		{
			header: 'interfacePlatformTypeMaxval',
			description: 'Maximum value of the platform type',
			humanReadable: 'Max. Val',
		},
		{
			header: 'interfacePlatformTypeBitSize',
			description: 'Size of the platform type in bits',
			humanReadable: 'Size (b)',
		},
		{
			header: 'interfacePlatformTypeUnits',
			description: 'Platform type units',
			humanReadable: 'Units',
		},
		{
			header: 'interfacePlatformTypeValidRangeDescription',
			description: 'Platform type valid range description',
			humanReadable: 'Valid Range Description',
		},
		{
			header: 'interfaceLogicalType',
			description: 'Logical type of the platform type',
			humanReadable: 'Logical Type',
		},
	];
