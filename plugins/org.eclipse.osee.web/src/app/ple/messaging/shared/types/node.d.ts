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
import { applic } from "../../shared/types/NamedId.applic";

export interface nodeData {
    id: string,
    name: string,
    description?:string
    interfaceNodeBgColor: string,
    interfaceNodeAddress:string,
    applicability?:applic
}

export interface node {
    id?: string,
    name: string,
    description?: string
    applicability?:applic
    
}