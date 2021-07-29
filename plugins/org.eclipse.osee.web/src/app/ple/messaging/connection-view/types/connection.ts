export interface connection {
    id?: string,
    name: string,
    description?: string,
    transportType: transportType
}

export enum transportType {
    Ethernet = "ETHERNET",
    MILSTD1553 ="MILSTD1553_B"
}

export interface newConnection {
    connection: connection,
    nodeId:string
}