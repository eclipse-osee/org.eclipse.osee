/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import type { PlatformType } from '@osee/messaging/shared/types';

export const platformTypesMock: PlatformType[] = [
	{
		interfaceLogicalType: 'boolean',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '8',
		interfacePlatformTypeBitSize: '8',
		interfacePlatformTypeBitsResolution: '8',
		interfacePlatformTypeCompRate: '20Hz',
		interfaceDefaultValue: 'false',
		interfacePlatformTypeMaxval: '6',
		interfacePlatformTypeMinval: '2',
		interfacePlatformTypeMsbValue: '4',
		interfacePlatformTypeUnits: 'N/A',
		interfacePlatformTypeValidRangeDescription: '',
		name: 'First',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
	{
		interfaceLogicalType: 'boolean',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '8',
		interfacePlatformTypeBitSize: '8',
		interfacePlatformTypeBitsResolution: '8',
		interfacePlatformTypeCompRate: '20Hz',
		interfaceDefaultValue: 'false',
		interfacePlatformTypeMaxval: '8',
		interfacePlatformTypeMinval: '4',
		interfacePlatformTypeMsbValue: '6',
		interfacePlatformTypeUnits: 'N/A',
		interfacePlatformTypeValidRangeDescription: '',
		name: 'First2',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];
export const platformTypes1: PlatformType[] = [
	{
		interfaceLogicalType: '',
		description: '',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitSize: '8',
		interfacePlatformTypeBitsResolution: '8',
		interfacePlatformTypeCompRate: '',
		interfaceDefaultValue: '',
		interfacePlatformTypeMaxval: '1',
		interfacePlatformTypeMinval: '0',
		interfacePlatformTypeMsbValue: '0',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
		id: '0',
		name: 'Name',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
	{
		interfaceLogicalType: '',
		description: '',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitSize: '8',
		interfacePlatformTypeBitsResolution: '8',
		interfacePlatformTypeCompRate: '',
		interfaceDefaultValue: '',
		interfacePlatformTypeMaxval: '1',
		interfacePlatformTypeMinval: '0',
		interfacePlatformTypeMsbValue: '0',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
		id: '1',
		name: 'Name2',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
	{
		interfaceLogicalType: '',
		description: '',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitSize: '8',
		interfacePlatformTypeBitsResolution: '8',
		interfacePlatformTypeCompRate: '',
		interfaceDefaultValue: '',
		interfacePlatformTypeMaxval: '1',
		interfacePlatformTypeMinval: '0',
		interfacePlatformTypeMsbValue: '0',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
		id: '2',
		name: 'Name3',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];
