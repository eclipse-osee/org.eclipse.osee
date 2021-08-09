import { applic } from "../../shared/types/NamedId.applic";

export interface connection {
    id?: string,
    name: string,
    description?: string,
    transportType: transportType
    applicability?:applic
}

export enum transportType {
    Ethernet = "ETHERNET",
    MILSTD1553 ="MILSTD1553_B"
}

export interface newConnection {
    connection: connection,
    nodeId:string
}