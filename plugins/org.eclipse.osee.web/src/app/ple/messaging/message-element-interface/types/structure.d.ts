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
import { applic } from "src/app/types/applicability/applic";
import { difference } from "src/app/types/change-report/change-report";
import { element, elementWithChanges } from "./element";

export interface structure {
    [index:string]:string|(element|elementWithChanges)[]|number|applic|boolean|undefined|structureChanges,
    id: string,
    name: string,
    elements?: (element|elementWithChanges)[],
    description: string,
    interfaceMaxSimultaneity: string,
    interfaceMinSimultaneity: string,
    interfaceTaskFileType: number,
    interfaceStructureCategory: string,
    numElements?: number,
    sizeInBytes?: number,
    bytesPerSecondMinimum?: number,
    bytesPerSecondMaximum?: number,
    applicability?:applic,
}
export interface structureChanges{
    name?: difference,
    description?: difference,
    interfaceMaxSimultaneity?: difference,
    interfaceMinSimultaneity?: difference,
    interfaceTaskFileType?: difference,
    interfaceStructureCategory?: difference,
    applicability?: difference,
    numElements?:boolean
}
export interface structureWithChanges extends Required<structure>{
    added: boolean,
    deleted: boolean,
    hasElementChanges:boolean,
    changes: structureChanges
}