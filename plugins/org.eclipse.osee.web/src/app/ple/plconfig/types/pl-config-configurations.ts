import { difference } from "src/app/types/change-report/change-report";
import { NameValuePair } from "./base-types/NameValuePair";

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
export interface configuration {
    name: string,
    copyFrom?: string,
    configurationGroup?: string,
    productApplicabilities?:string[]
}
export interface editConfiguration extends configuration {
    configurationGroup?: string
}
export interface configurationGroup extends configGroup {
    hasFeatureApplicabilities: boolean
    productApplicabilities:string[]
}

export interface configGroup extends NameValuePair{
    configurations:string[],
}
export interface configGroupWithChanges extends configGroup{
    deleted: boolean,
    added: boolean,
    changes: {
        name?: difference,
        configurations?:difference[]
    }
}