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
import { applic } from "./NamedId.applic";

export interface enumeration{
    id?:string,
    name: string,
    ordinal: number,
    applicability: applic,
    applicabilityId?:string
}

export interface enumerationSet extends enumSet {
    id?: string,
    name: string,
    applicability: applic,
    description: string,
    enumerations?: enumeration[],
    /**
     * Only used in api creation
     */
    applicabilityId?: string
}
export interface enumSet{
    id?: string,
    name: string,
    applicability: applic,
    description: string,
    applicabilityId?:string
}