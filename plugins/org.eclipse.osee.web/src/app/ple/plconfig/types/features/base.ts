import { ExtendedNameValuePair } from "../base-types/ExtendedNameValuePair";
import { NameValuePair } from "../base-types/NameValuePair";

export interface feature {
    name: string,
    description: string,
    valueType: string,
    valueStr?: string,
    defaultValue: string,
    productAppStr?: string,
    values: string[],
    productApplicabilities: string[],
    multiValued: boolean;
    setValueStr(): void;
    setProductAppStr(): void;
    
}
export interface trackableFeature extends feature{
    id: string,
    idIntValue?: number,
    idString?: string,
    type: null | undefined,
}
export interface extendedFeature extends trackableFeature {
    configurations:ExtendedNameValuePair[]
}