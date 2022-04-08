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
import { submessageDiffItem } from "src/app/ple/messaging/shared/types/DifferenceReport";
export const submessageDiffsMock: submessageDiffItem[] = [
    {
        "id": "200403",
        "name": "Submessage1",
        "applicability": {
            "id": "1009971623404681232",
            "name": "Config = Product C"
        },
        "description": "This is submessage 1",
        "interfaceSubMessageNumber": "0",
        "diffInfo": {
            "added": false,
            "deleted": false,
            "fieldsChanged": {
                "interfaceSubMessageNumber": "1",
                "applicability": {
                    "id": "1",
                    "name": "Base"
                }
            },
            "url": {
                "label": "",
                "url": ""
            }
        }
    },
    {
        "id": "200406",
        "name": "Submessage D",
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "Delete this submessage",
        "interfaceSubMessageNumber": "4",
        "diffInfo": {
            "added": false,
            "deleted": true,
            "fieldsChanged": {},
            "url": {
                "label": "",
                "url": ""
            }
        }
    },
    {
        "id": "200405",
        "name": "Submessage UR Edited",
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "Unrelate this submessage",
        "interfaceSubMessageNumber": "3",
        "diffInfo": {
            "added": false,
            "deleted": false,
            "fieldsChanged": {
                "name": "Submessage UR"
            },
            "url": {
                "label": "",
                "url": ""
            }
        }
    },
    {
        "id": "200436",
        "name": "Submessage A",
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "Added this submessage",
        "interfaceSubMessageNumber": "3",
        "diffInfo": {
            "added": true,
            "deleted": false,
            "fieldsChanged": {},
            "url": {
                "label": "",
                "url": ""
            }
        }
    },
    {
        "id": "200404",
        "name": "Submessage2 (Edit)",
        "applicability": {
            "id": "1",
            "name": "Base"
        },
        "description": "This is submessage 2",
        "interfaceSubMessageNumber": "2",
        "diffInfo": {
            "added": false,
            "deleted": false,
            "fieldsChanged": {
                "name": "Submessage2"
            },
            "url": {
                "label": "",
                "url": ""
            }
        }
    }
]