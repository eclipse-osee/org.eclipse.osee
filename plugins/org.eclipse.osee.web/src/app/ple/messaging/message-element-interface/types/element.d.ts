import { difference } from "src/app/types/change-report/change-report";
import { applic } from "../../../../types/applicability/applic";

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
export interface element {
    [index:string]:any,
    id: string,
    name: string,
    description: string,
    notes: string,
    interfaceElementIndexEnd: number,
    interfaceElementIndexStart: number,
    interfaceElementAlterable: boolean,
    platformTypeName2?: string,
    platformTypeId?: number
    beginWord?: number,
    beginByte?: number,
    endWord?: number,
    endByte?: number,
    logicalType?: string,
    applicability?:applic,
}

export interface elementWithChanges extends element{
    added: boolean,
    deleted: boolean,
    changes: {
        name?: difference,
        description?: difference,
        notes?: difference,
        platformTypeName2?:difference,
        interfaceElementIndexEnd?: difference,
        interfaceElementIndexStart?: difference,
        interfaceElementAlterable?: difference,
        applicability?:difference
    }
}