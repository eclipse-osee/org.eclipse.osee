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
import { element } from "../types/element";
import { structure } from "../types/structure";
import { message } from "../../message-interface/types/messages";
import { subMessage } from "../../message-interface/types/sub-messages";
import { MimPreferences } from "../types/mim.preferences";

export const MimPreferencesMock: MimPreferences<structure&message&subMessage&element> = {
    id: "61106791",
    name: "Joe Smith",
    columnPreferences: [
        {
            name: "name",
            enabled: true
        },
        {
            name: "description",
            enabled: true
        },
        {
            name: "interfaceMaxSimultaneity",
            enabled: true
        },
        {
            name: "interfaceMinSimultaneity",
            enabled: true
        },
        {
            name: "interfaceTaskFileType",
            enabled: true
        },
        {
            name: "interfaceStructureCategory",
            enabled: true
        },
        {
            name: "platformTypeName2",
            enabled: true
        },
        {
            name: "interfaceElementAlterable",
            enabled: true
        },
        {
            name: "notes",
            enabled: true
        },
        {
            name: "numElements",
            enabled: false
        },
        {
            name: "sizeInBytes",
            enabled: false
        },
        {
            name: "bytesPerSecondMinimum",
            enabled: false
        },
        {
            name: "bytesPerSecondMaximum",
            enabled: false
        },
        {
            name: "GenerationIndicator",
            enabled: false
        },
        {
            name: "applicability",
            enabled: true
        },
        {
            name: "beginWord",
            enabled: false
        },
        {
            name: "endWord",
            enabled: false
        },
        {
            name: "beginByte",
            enabled: false
        },
        {
            name: "endByte",
            enabled: false
        }
    ],
    inEditMode: true,
    hasBranchPref:true,
}