import { element } from "./element";
import { structure } from "./structure";
import { message } from "../../message-interface/types/messages";
import { subMessage } from "../../message-interface/types/sub-messages";
import { branchSummary, connectionDiffItem, elementDiffItem, messageDiffItem, nodeDiffItem, structureDiffItem, submessageDiffItem } from "./DifferenceReport.d";

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
    header: Extract<keyof T,string>,
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

export interface nodeDiffHeaderDetail extends headerDetail<nodeDiffItem>{
}

export interface connectionDiffHeaderDetail extends headerDetail<connectionDiffItem>{
}

export interface messageDiffHeaderDetail extends headerDetail<messageDiffItem>{
}

export interface submessageDiffHeaderDetail extends headerDetail<submessageDiffItem>{
}

export interface structureDiffHeaderDetail extends headerDetail<structureDiffItem>{
}

export interface elementDiffHeaderDetail extends headerDetail<elementDiffItem>{
}

export interface branchSummaryHeaderDetail extends headerDetail<branchSummary>{
}