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
import { of } from "rxjs";
import { messagesMock } from "../../../message-interface/mocks/ReturnObjects/messages.mock";
import { subMessagesMock } from "../../../message-interface/mocks/ReturnObjects/submessages.mock";
import { MessagesService } from "../../services/messages.service";

export const messageServiceMock: Partial<MessagesService> = {
    getSubMessage(branchId: string, messageId: string, subMessageId: string,connectionId:string) {
        return of(subMessagesMock[0]);
    },
    getMessages(branchId: string, connectionId: string) {
        return of(messagesMock);
    }
}