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

import { PlatformType } from './platformType';

export class PlatformTypeSentinel implements PlatformType {
	id?: string | undefined = '-1';
	description: string = '';
	interfaceLogicalType: string = '';
	interfacePlatform2sComplement: boolean = false;
	interfacePlatformTypeAnalogAccuracy: string = '';
	interfacePlatformTypeBitsResolution: string = '';
	interfacePlatformTypeBitSize: string = '';
	interfacePlatformTypeCompRate: string = '';
	interfaceDefaultValue: string = '';
	interfacePlatformTypeEnumLiteral: string = '';
	interfacePlatformTypeMaxval: string = '';
	interfacePlatformTypeMinval: string = '';
	interfacePlatformTypeMsbValue: string = '';
	interfacePlatformTypeUnits: string = '';
	interfacePlatformTypeValidRangeDescription: string = '';
	name: string = '';
}
