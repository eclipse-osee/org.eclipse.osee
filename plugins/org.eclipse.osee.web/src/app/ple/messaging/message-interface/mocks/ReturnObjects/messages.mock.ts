import { message } from "../../types/messages";
import { subMessagesMock } from "./submessages.mock";

export const messagesMock: message[] = [
    {
        id: '0',
        name: 'message0',
        description: 'description',
        subMessages: subMessagesMock,
        interfaceMessageRate: '1',
        interfaceMessagePeriodicity: 'Periodic',
        interfaceMessageWriteAccess: true,
        interfaceMessageType: 'Connection',
        interfaceMessageNumber: "0",
        applicability: {
            id: '1',
            name: 'Base'
        }
    },
    {
        id: '1',
        name: 'message1',
        description: 'description',
        subMessages: [],
        interfaceMessageRate: '1',
        interfaceMessagePeriodicity: 'Periodic',
        interfaceMessageWriteAccess: true,
        interfaceMessageType: 'Connection',
        interfaceMessageNumber: "1",
        applicability: {
            id: '1',
            name: 'Base'
        }
    },
]