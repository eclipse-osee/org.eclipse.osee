/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import {
	applic,
	applicabilitySentinel,
} from '@osee/shared/types/applicability';
import { enumerationSet } from '../types/enum';
import type { PlatformType } from '../types/platformType';

export class PlatformTypeSentinel implements PlatformType {
	[index: string]: string | boolean | enumerationSet | applic;
	id: string = '-1';
	description: string = '';
	interfaceLogicalType: string = '';
	interfacePlatformType2sComplement: boolean = false;
	interfacePlatformTypeAnalogAccuracy: string = '';
	interfacePlatformTypeBitsResolution: string = '';
	interfacePlatformTypeBitSize: string = '';
	interfacePlatformTypeCompRate: string = '';
	interfaceDefaultValue: string = '';
	interfacePlatformTypeMaxval: string = '';
	interfacePlatformTypeMinval: string = '';
	interfacePlatformTypeMsbValue: string = '';
	interfacePlatformTypeUnits: string = '';
	interfacePlatformTypeValidRangeDescription: string = '';
	name: string = '';
	applicability: applic = {
		id: '1',
		name: 'Base',
	};
	enumSet: enumerationSet = {
		id: '-1',
		name: '',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	};
}

export class ArrayHeaderPlatformType implements ArrayHeaderPlatformType {
	[index: string]: string | boolean | enumerationSet | applic;
	id = '0' as const;
	description = '' as const;
	interfaceLogicalType = '' as const;
	interfacePlatformType2sComplement = false as const;
	interfacePlatformTypeAnalogAccuracy = '' as const;
	interfacePlatformTypeBitsResolution = '' as const;
	interfacePlatformTypeBitSize = '0' as const;
	interfacePlatformTypeCompRate = '' as const;
	interfaceDefaultValue = '' as const;
	enumSet = {
		id: '-1',
		name: '',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	}; //typically unavailable, only present on query
	interfacePlatformTypeMaxval = '' as const;
	interfacePlatformTypeMinval = '' as const;
	interfacePlatformTypeMsbValue = '' as const;
	interfacePlatformTypeUnits = '' as const;
	interfacePlatformTypeValidRangeDescription = '' as const;
	name = 'Element Array Header' as const;
	applicability = applicabilitySentinel;
}
