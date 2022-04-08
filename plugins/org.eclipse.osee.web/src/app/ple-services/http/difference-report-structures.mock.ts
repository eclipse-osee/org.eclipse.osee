/*********************************************************************
 * Copyright (c) 2022 Boeing
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
// @ts-nocheck
import { structureDiffItem } from "src/app/ple/messaging/shared/types/DifferenceReport";
export const structureDiffsMock: structureDiffItem[] = [
    {
        "id": "200433",
        "name": "Structure A",
        "elements": [],
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "Added this structure",
        "interfaceMaxSimultaneity": "1",
        "bytesPerSecondMinimum": 0,
        "interfaceStructureCategory": "Tactical Status",
        "interfaceTaskFileType": 6,
        "interfaceMinSimultaneity": "1",
        "incorrectlySized": false,
        "bytesPerSecondMaximum": 0,
        "sizeInBytes": 0,
        "numElements": 0,
        "diffInfo": {
            "added": true,
            "deleted": false,
            "fieldsChanged": {},
            "url": {
                "label": "",
                "url": ""
            }
        },
        "elementChanges": []
    },
    {
        "id": "200407",
        "name": "Structure1 (Edit)",
        "elements": [],
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "This is structure 1",
        "interfaceMaxSimultaneity": "2",
        "bytesPerSecondMinimum": 0,
        "interfaceStructureCategory": "Flight Test",
        "interfaceTaskFileType": 1,
        "interfaceMinSimultaneity": "1",
        "incorrectlySized": false,
        "bytesPerSecondMaximum": 0,
        "sizeInBytes": 0,
        "numElements": 0,
        "diffInfo": {
            "added": false,
            "deleted": false,
            "fieldsChanged": {
                "name": "Structure1"
            },
            "url": {
                "label": "",
                "url": ""
            }
        },
        "elementChanges": []
    },
    {
        "id": "200410",
        "name": "Structure D",
        "elements": [],
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "Delete this structure",
        "interfaceMaxSimultaneity": "2",
        "bytesPerSecondMinimum": 0,
        "interfaceStructureCategory": "BIT Status",
        "interfaceTaskFileType": 5,
        "interfaceMinSimultaneity": "0",
        "incorrectlySized": false,
        "bytesPerSecondMaximum": 0,
        "sizeInBytes": 0,
        "numElements": 0,
        "diffInfo": {
            "added": false,
            "deleted": true,
            "fieldsChanged": {},
            "url": {
                "label": "",
                "url": ""
            }
        },
        "elementChanges": []
    }
]