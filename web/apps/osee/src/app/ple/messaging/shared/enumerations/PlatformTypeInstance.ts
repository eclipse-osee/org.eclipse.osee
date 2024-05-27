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

import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { enumerationSet } from '../types/enum';
import type { PlatformType } from '../types/platformType';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

export class PlatformTypeSentinel implements PlatformType {
	id: `${number}` = '-1';
	gammaId: `${number}` = '-1';
	name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.NAME,
	};
	description: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
	};
	interfaceLogicalType: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.LOGICALTYPE>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.LOGICALTYPE,
	};
	interfacePlatformType2sComplement: Required<
		attribute<
			boolean,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT
		>
	> = {
		id: '-1',
		value: false,
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT,
	};
	interfacePlatformTypeAnalogAccuracy: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
	};
	interfacePlatformTypeBitsResolution: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
	};
	interfacePlatformTypeBitSize: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
	};
	interfacePlatformTypeCompRate: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
	};
	interfaceDefaultValue: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
	};
	interfacePlatformTypeMaxval: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
	};
	interfacePlatformTypeMinval: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
	};
	interfacePlatformTypeMsbValue: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
	};
	interfacePlatformTypeUnits: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
	};
	interfacePlatformTypeValidRangeDescription: Required<
		attribute<
			string,
			typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION
		>
	> = {
		id: '-1',
		value: '',
		gammaId: '-1',
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION,
	};
	applicability: applic = {
		id: '1',
		name: 'Base',
	};
	enumSet: enumerationSet = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			value: '',
			gammaId: '-1',
			typeId: ATTRIBUTETYPEIDENUM.NAME,
		},
		description: {
			id: '-1',
			value: '',
			gammaId: '-1',
			typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		enumerations: [],
	};
	constructor(name?: string) {
		if (name) {
			this.name.value = name;
		}
	}
}

export class ArrayHeaderPlatformType implements PlatformType {
	id: `${number}` = '0';
	gammaId: `${number}` = '-1';
	description = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
	};
	interfaceLogicalType = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.LOGICALTYPE,
	};
	interfacePlatformType2sComplement = {
		id: '-1' as `${number}`,
		value: false,
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT,
	};
	interfacePlatformTypeAnalogAccuracy = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
	};
	interfacePlatformTypeBitsResolution = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
	};
	interfacePlatformTypeBitSize = {
		id: '-1' as `${number}`,
		value: '0',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
	};
	interfacePlatformTypeCompRate = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
	};
	interfaceDefaultValue = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
	};
	enumSet = {
		id: '-1' as const,
		gammaId: '-1' as const,
		name: {
			id: '-1' as const,
			value: '',
			gammaId: '-1' as const,
			typeId: ATTRIBUTETYPEIDENUM.NAME,
		},
		description: {
			id: '-1' as const,
			value: '',
			gammaId: '-1' as const,
			typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
		},
		applicability: applicabilitySentinel,
		enumerations: [],
	}; //typically unavailable, only present on query
	interfacePlatformTypeMaxval = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
	};
	interfacePlatformTypeMinval = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
	};
	interfacePlatformTypeMsbValue = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
	};
	interfacePlatformTypeUnits = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
	};
	interfacePlatformTypeValidRangeDescription = {
		id: '-1' as `${number}`,
		value: '',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION,
	};
	name = {
		id: '-1' as `${number}`,
		value: 'Element Array Header',
		gammaId: '-1' as `${number}`,
		typeId: ATTRIBUTETYPEIDENUM.NAME,
	};
	applicability = applicabilitySentinel;
}
