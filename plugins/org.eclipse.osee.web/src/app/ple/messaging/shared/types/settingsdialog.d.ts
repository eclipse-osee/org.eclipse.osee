import { element } from "../../message-element-interface/types/element";
import { structure } from "../../message-element-interface/types/structure";

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
export interface settingsDialogData{
    branchId:string,
    allowedHeaders1: (keyof element)[],
    allHeaders1: (keyof element)[],
    allHeaders2: (keyof structure)[],
    allowedHeaders2: (keyof structure)[],
    editable: boolean,
    headers1Label: string,
    headers2Label: string,
    headersTableActive:boolean
    
}