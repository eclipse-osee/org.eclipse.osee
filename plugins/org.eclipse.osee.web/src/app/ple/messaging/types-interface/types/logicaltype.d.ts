export interface logicalType {
    id: string,
    name: string,
    idString: string,
    idIntValue:number
}

export interface logicalTypeFormDetail extends logicalType {
    fields:logicalTypeFieldInfo[]
}
interface logicalTypeFieldInfo {
    attributeType: string,
    editable: boolean,
    name:string,
    required: boolean,
    defaultValue:string
}