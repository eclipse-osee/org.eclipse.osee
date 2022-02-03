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
/**
 * Platform Type as defined by the API, ids are required when fetching or updating a platform type
 */
export interface PlatformType {
    id?: string,
    interfaceLogicalType: string,
    interfacePlatform2sComplement: boolean,
    interfacePlatformTypeAnalogAccuracy: string | null,
    interfacePlatformTypeBitsResolution: string | null,
    interfacePlatformTypeBitSize: string | null,
    interfacePlatformTypeCompRate: string | null,
    interfacePlatformTypeDefaultValue: string | null,
    interfacePlatformTypeEnumLiteral: string | null,
    interfacePlatformTypeMaxval: string | null,
    interfacePlatformTypeMinval: string | null,
    interfacePlatformTypeMsbValue: string | null,
    interfacePlatformTypeUnits: string | null,
    interfacePlatformTypeValidRangeDescription: string | null,
    name: string
    
}