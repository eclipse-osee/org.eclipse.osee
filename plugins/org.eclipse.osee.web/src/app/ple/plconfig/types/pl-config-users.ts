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
export interface userInterface {
    id: string,
    name: string,
    guid: null,
    active: boolean,
    description: null,
    workTypes: any[],
    tags: any[],
    userId: string,
    email: string,
    loginIds: string[],
    savedSearches: any[],
    userGroups: any[],
    artifactId: string,
    idString: string,
    idIntValue: number,
    uuid:number
}
export class pluser implements userInterface {
    id = '';
    name = '';
    guid = null;
    active = false;
    description = null;
    workTypes = [];
    tags= [];
    userId = '';
    email = '';
    loginIds = [];
    savedSearches = [];
    userGroups = [];
    artifactId = '';
    idString = '';
    idIntValue = 0;
    uuid = 0;
}