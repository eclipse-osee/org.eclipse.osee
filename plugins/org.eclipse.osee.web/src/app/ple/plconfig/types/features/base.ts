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
import { ExtendedNameValuePair } from "../base-types/ExtendedNameValuePair";
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
    configurations:ExtendedNameValuePair[]
}