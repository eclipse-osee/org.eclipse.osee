import { applic } from "../../shared/types/NamedId.applic";
import { subMessage } from "./sub-messages";

export interface message {
    id: string,
    name: string,
    description: string ,
    subMessages: Array<Required<subMessage>>,
    interfaceMessageRate: string ,
    interfaceMessagePeriodicity: string ,
    interfaceMessageWriteAccess: boolean ,
    interfaceMessageType: string ,
    interfaceMessageNumber: string,
    applicability?:applic
}