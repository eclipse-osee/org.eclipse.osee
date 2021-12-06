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
import { difference } from "src/app/types/change-report/change-report";
import { ExtendedNameValuePair, ExtendedNameValuePairWithChanges } from "../base-types/ExtendedNameValuePair";
import { NameValuePair } from "../base-types/NameValuePair";

export interface feature {
    name: string,
    description: string,
    valueType: string,
    valueStr?: string,
    defaultValue: string,
    productAppStr?: string,
    values: string[],
    productApplicabilities: string[],
    multiValued: boolean;
    setValueStr(): void;
    setProductAppStr(): void;
    
}
export interface trackableFeature extends feature{
    id: string,
    idIntValue?: number,
    idString?: string,
    type: null | undefined,
}
export interface extendedFeature extends trackableFeature {
    configurations:(ExtendedNameValuePair|ExtendedNameValuePairWithChanges)[]
}
export interface extendedFeatureWithChanges extends extendedFeature{
    added: boolean,
    deleted: boolean,
    changes: {
        name?: difference,
        description?: difference,
        defaultValue?: difference,
        multiValued?: difference,
        productApplicabilities?: difference[],
        valueType?: difference,
        values?: difference[],
        configurations?: {name:difference, value:difference,values:difference[]}[]
    }
}