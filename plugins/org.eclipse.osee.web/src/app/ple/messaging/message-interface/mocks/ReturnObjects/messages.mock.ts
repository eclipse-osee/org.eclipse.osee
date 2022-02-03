/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { message, messageWithChanges } from "../../types/messages";
import { subMessagesMock } from "./submessages.mock";

export const messagesMock: (message|messageWithChanges)[] = [
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
        },
        initiatingNode: {
            id: '1',
            name: 'Node 1'
        },
        changes: {
            name: {
              previousValue: '',
              currentValue: 'name',
              transactionToken: {
                id: '-1',
                branchId:'-1'
              }
            }
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
        },
        initiatingNode: {
            id: '1',
            name: 'Node 1'
        },
    },
    {
        id: "201304",
        name: 'message2',
        description: 'description',
        subMessages: [],
        interfaceMessageRate: '5',
        interfaceMessagePeriodicity: 'Periodic',
        interfaceMessageWriteAccess: true,
        interfaceMessageType: 'Connection',
        interfaceMessageNumber: "2",
        applicability: {
            id: '1',
            name: 'Base'
        },
        initiatingNode: {
            id: '1',
            name: 'Node 1'
        },
    },
    {
        id: '201300',
        name: 'message3',
        description: 'description',
        subMessages: [
            {
                id: '201305',
                name: 'abcdef',
                description: 'ghijk',
                interfaceSubMessageNumber: '25',
                applicability: {
                    id: '1',
                    name: 'Base'
                }
            }
        ],
        interfaceMessageRate: '5',
        interfaceMessagePeriodicity: 'Periodic',
        interfaceMessageWriteAccess: true,
        interfaceMessageType: 'Connection',
        interfaceMessageNumber: "2",
        applicability: {
            id: '1',
            name: 'Base'
        },
        initiatingNode: {
            id: '1',
            name: 'Node 1'
        },
    },
    {
        id: '201289',
        name: 'message4',
        description: 'description',
        subMessages: [],
        interfaceMessageRate: '5',
        interfaceMessagePeriodicity: 'Periodic',
        interfaceMessageWriteAccess: true,
        interfaceMessageType: 'Connection',
        interfaceMessageNumber: "2",
        applicability: {
            id: '1',
            name: 'Base'
        },
        initiatingNode: {
            id: '1',
            name: 'Node 1'
        },
    }
]