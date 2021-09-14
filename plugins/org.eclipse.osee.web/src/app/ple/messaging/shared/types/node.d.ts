import { applic } from "../../shared/types/NamedId.applic";

export interface nodeData {
    id: string,
    name: string,
    description?:string
    interfaceNodeBgColor: string,
    interfaceNodeAddress:string,
    applicability?:applic
}

export interface node {
    id?: string,
    name: string,
    description?: string
    applicability?:applic
    
}