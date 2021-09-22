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
export interface configurationGroup {
    configurations:string[],
    id: string,
    name: string,
    hasFeatureApplicabilities: boolean
    productApplicabilities:string[]
}