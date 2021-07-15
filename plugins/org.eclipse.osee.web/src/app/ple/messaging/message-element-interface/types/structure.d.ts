import { element } from "./element";

export interface structure {
    id: string,
    name: string,
    elements: element[],
    description: string,
    interfaceMaxSimultaneity: string,
    interfaceMinSimultaneity: string,
    interfaceTaskFileType: number,
    interfaceStructureCategory: string,
    numElements?: number,
    sizeInBytes?: number,
    bytesPerSecondMinimum?: number,
    bytesPerSecondMaximum?: number,
}