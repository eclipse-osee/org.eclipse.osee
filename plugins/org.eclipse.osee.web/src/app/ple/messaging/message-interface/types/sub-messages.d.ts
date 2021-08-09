import { applic } from "../../shared/types/NamedId.applic";

export interface subMessage {
    id?: string,
    name: string,
    description: string,
    interfaceSubMessageNumber: string,
    applicability?:applic
}