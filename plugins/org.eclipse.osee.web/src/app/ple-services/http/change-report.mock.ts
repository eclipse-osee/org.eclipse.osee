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
import { changeInstance, ignoreType, changeTypeNumber, changeTypeEnum, ModificationType } from "src/app/types/change-report/change-report";


export const changeReportMock: changeInstance[] = [
    {
        ignoreType:ignoreType.NONE,
        changeType: {
            id: changeTypeNumber.ARTIFACT_CHANGE,
            name: changeTypeEnum.ARTIFACT_CHANGE,
            typeId: 2834799904,
            notAttributeChange: true,
            notRelationChange: true,
            idIntValue: 111,
            idString:"111"
        },
        artId: '10',
        itemId: '1234',
        itemTypeId: '7890',
        baselineVersion: {
            transactionToken: {
                id: "-1",
                branchId:"-1"
            },
            gammaId: "-1",
            modType: ModificationType.NEW,
            value: "",
            uri: "",
            valid: true,
            applicabilityToken: {
                id: "1",
                name:"Base"
            }
        },
        firstNonCurrentChange: {
            transactionToken: {
                id: "-1",
                branchId:"-1"
            },
            gammaId: "-1",
            modType: ModificationType.NEW,
            value: "",
            uri: "",
            valid: true,
            applicabilityToken: {
                id: "1",
                name:"Base"
            }
        },
        currentVersion: {
            transactionToken: {
                id: "-1",
                branchId:"-1"
            },
            gammaId: "-1",
            modType: ModificationType.NEW,
            value: "",
            uri: "",
            valid: true,
            applicabilityToken: {
                id: "1",
                name:"Base"
            }
        },
        destinationVersion: {
            transactionToken: {
                id: "-1",
                branchId:"-1"
            },
            gammaId: "-1",
            modType: ModificationType.NEW,
            value: "",
            uri: "",
            valid: true,
            applicabilityToken: {
                id: "1",
                name:"Base"
            }
        },
        netChange: {
            transactionToken: {
                id: "-1",
                branchId:"-1"
            },
            gammaId: "-1",
            modType: ModificationType.NEW,
            value: "",
            uri: "",
            valid: true,
            applicabilityToken: {
                id: "1",
                name:"Base"
            }
        },
        synthetic: false,
        artIdB: '2',
        deleted: false,
        applicabilityCopy:false
    }
]