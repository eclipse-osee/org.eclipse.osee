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
export interface MimPreferences<T={name:''}> {
    id: string,
    name: string,
    columnPreferences: MimColumnPreference<T>[],
    inEditMode: boolean,
    hasBranchPref:boolean
}

export interface MimColumnPreference<T={name:''}>{
    enabled: boolean,
    name:Extract<keyof T,string>
}