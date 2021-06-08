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