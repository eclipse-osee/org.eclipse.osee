import { element, elementWithChanges } from "../../message-element-interface/types/element";
import { structure, structureWithChanges } from "../../message-element-interface/types/structure";
import { message, messageWithChanges } from "../../message-interface/types/messages";
import { subMessage, subMessageWithChanges } from "../../message-interface/types/sub-messages";

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
export interface headerDetail<T>{
    header: keyof T,
    description: string,
    humanReadable:string
}

export interface messageHeaderDetail extends headerDetail<message>{
}
export interface subMessageHeaderDetail extends headerDetail<subMessage>{
}
export interface structureHeaderDetail extends headerDetail<structure>{
}

export interface elementHeaderDetail extends headerDetail<element>{
}