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

import { enumerationSet } from './enum'

/**
 * Platform Type as defined by the API, ids are required when fetching or updating a platform type
 */
export interface PlatformType {
    id?: string,
    description:string,
    interfaceLogicalType: string,
    interfacePlatform2sComplement: boolean,
    interfacePlatformTypeAnalogAccuracy: string,
    interfacePlatformTypeBitsResolution: string,
    interfacePlatformTypeBitSize: string,
    interfacePlatformTypeCompRate: string,
    interfacePlatformTypeDefaultValue: string,
    enumSet?:enumerationSet //typically unavailable, only present on query
    interfacePlatformTypeMaxval: string,
    interfacePlatformTypeMinval: string,
    interfacePlatformTypeMsbValue: string,
    interfacePlatformTypeUnits: string,
    interfacePlatformTypeValidRangeDescription: string,
    name: string
    
}

export interface platformTypeImportToken extends Pick<PlatformType, 
'id' | 'name' | 'description' | 
'interfacePlatformTypeDefaultValue' |
'interfacePlatformTypeMinval' |
'interfacePlatformTypeMaxval' |
'interfacePlatformTypeBitSize' |
'interfacePlatformTypeUnits' |
'interfaceLogicalType' | 
'interfacePlatformTypeValidRangeDescription'>{}