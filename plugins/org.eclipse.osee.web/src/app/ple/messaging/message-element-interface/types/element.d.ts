export interface element {
    id: string,
    name: string,
    description: string,
    notes: string,
    interfaceElementIndexEnd: number,
    interfaceElementIndexStart: number,
    interfaceElementAlterable: boolean,
    platformTypeName2?: string,
    platformTypeId?: number
    beginWord?: number,
    beginByte?: number,
    endWord?: number,
    endByte?:number
}