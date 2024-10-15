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

import { applic } from '@osee/applicability/types';
import type { enumerationSet } from './enum';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { hasChanges } from '@osee/shared/types/change-report';

export const potentialPlatformTypeHeaders: (keyof PlatformType)[] = [
	'id',
	'gammaId',
	'name',
	'description',
	'interfaceLogicalType',
	'interfacePlatformType2sComplement',
	'interfacePlatformTypeAnalogAccuracy',
	'interfacePlatformTypeBitsResolution',
	'interfacePlatformTypeBitSize',
	'interfacePlatformTypeCompRate',
	'interfaceDefaultValue',
	'enumSet',
	'interfacePlatformTypeMaxval',
	'interfacePlatformTypeMinval',
	'interfacePlatformTypeMsbValue',
	'interfacePlatformTypeUnits',
	'interfacePlatformTypeValidRangeDescription',
];

/**
 * Platform Type as defined by the API, ids are required when fetching or updating a platform type
 */
export type PlatformType = Required<{
	id: `${number}`;
	gammaId: `${number}`;
}> &
	DiffablePlatformTypeProps &
	_platformTypeRelations &
	Partial<platformTypeChanges>;

export type PlatformTypeAttr = Required<
	{
		name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>;
		description: Required<
			attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
		>;
		interfaceLogicalType: Required<
			attribute<string, typeof ATTRIBUTETYPEIDENUM.LOGICALTYPE>
		>;
		interfacePlatformType2sComplement: Required<
			attribute<
				boolean,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT
			>
		>;
		interfacePlatformTypeAnalogAccuracy: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY
			>
		>;
		interfacePlatformTypeBitsResolution: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION
			>
		>;
		interfacePlatformTypeBitSize: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE
			>
		>;
		interfacePlatformTypeCompRate: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE
			>
		>;
		interfaceDefaultValue: Required<
			attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL>
		>;
		interfacePlatformTypeMaxval: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL
			>
		>;
		interfacePlatformTypeMinval: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL
			>
		>;
		interfacePlatformTypeMsbValue: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL
			>
		>;
		interfacePlatformTypeUnits: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS
			>
		>;
		interfacePlatformTypeValidRangeDescription: Required<
			attribute<
				string,
				typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION
			>
		>;
	} & { interfacePlatformTypeBitSize: bitSize }
>;

type _platformTypeRelations = Required<{
	enumSet: enumerationSet;
}>;

export type DiffablePlatformTypeProps = PlatformTypeAttr &
	Required<{ applicability: applic }>;
export type DisplayablePlatformTypeProps = DiffablePlatformTypeProps &
	_platformTypeRelations;

export type EditablePlatformTypeProps = DiffablePlatformTypeProps;

export type bitSize = Required<
	attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE>
>;

type _platformTypeChanges = hasChanges<DiffablePlatformTypeProps>;
type platformTypeChanges = {
	added: boolean;
	deleted: boolean;
	changes: _platformTypeChanges;
};

export type platformTypeImportToken = {} & Pick<
	PlatformType,
	| 'id'
	| 'name'
	| 'description'
	| 'interfaceDefaultValue'
	| 'interfacePlatformTypeMinval'
	| 'interfacePlatformTypeMaxval'
	| 'interfacePlatformTypeBitSize'
	| 'interfacePlatformTypeUnits'
	| 'interfaceLogicalType'
	| 'interfacePlatformTypeValidRangeDescription'
>;

export type ArrayHeaderPlatformType = {
	id: '-1';
	description: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.DESCRIPTION;
	};
	interfaceLogicalType: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.LOGICALTYPE;
	};
	interfacePlatformType2sComplement: {
		id: '-1';
		value: false;
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT;
	};
	interfacePlatformTypeAnalogAccuracy: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY;
	};
	interfacePlatformTypeBitsResolution: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION;
	};
	interfacePlatformTypeBitSize: {
		id: '-1';
		value: '0';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE;
	};
	interfacePlatformTypeCompRate: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE;
	};
	interfaceDefaultValue: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL;
	};
	enumSet: enumerationSet; //typically unavailable, only present on query
	interfacePlatformTypeMaxval: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL;
	};
	interfacePlatformTypeMinval: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL;
	};
	interfacePlatformTypeMsbValue: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL;
	};
	interfacePlatformTypeUnits: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS;
	};
	interfacePlatformTypeValidRangeDescription: {
		id: '-1';
		value: '';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION;
	};
	name: {
		id: '-1';
		value: 'Element Array Header';
		gammaId: '-1';
		typeId: typeof ATTRIBUTETYPEIDENUM.NAME;
	};
	applicability: applic;
} & PlatformType;
