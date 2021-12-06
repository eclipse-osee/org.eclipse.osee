import { difference } from "src/app/types/change-report/change-report";

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
export interface ExtendedNameValuePair {
    id: string,
    name: string,
    value:string,
    values: string[]
}

export interface ExtendedNameValuePairWithChanges extends ExtendedNameValuePair {
    changes: {
        value:difference
    }
}