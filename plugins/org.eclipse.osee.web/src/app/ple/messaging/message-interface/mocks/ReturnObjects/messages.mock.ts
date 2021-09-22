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