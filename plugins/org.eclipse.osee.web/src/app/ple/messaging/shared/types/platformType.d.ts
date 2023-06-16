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

import { applic } from '@osee/shared/types/applicability';
import type { enumerationSet } from './enum';

/**
 * Platform Type as defined by the API, ids are required when fetching or updating a platform type
 */
export interface PlatformType {
	[index: string]: string | boolean | enumerationSet | applic | undefined;
	id?: string;
	description: string;
	interfaceLogicalType: string;
	interfacePlatformType2sComplement: boolean;
	interfacePlatformTypeAnalogAccuracy: string;
	interfacePlatformTypeBitsResolution: string;
	interfacePlatformTypeBitSize: string;
	interfacePlatformTypeCompRate: string;
	interfaceDefaultValue: string;
	enumSet?: enumerationSet; //typically unavailable, only present on query
	interfacePlatformTypeMaxval: string;
	interfacePlatformTypeMinval: string;
	interfacePlatformTypeMsbValue: string;
	interfacePlatformTypeUnits: string;
	interfacePlatformTypeValidRangeDescription: string;
	name: string;
	applicability: applic;
}

export interface platformTypeImportToken
	extends Pick<
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
	> {}
