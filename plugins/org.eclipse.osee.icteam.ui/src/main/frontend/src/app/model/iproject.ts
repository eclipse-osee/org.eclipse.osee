/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
export interface IProject {
    artifactType: String;
    attributeMap: Map<any, Array<String>>;
    currentLoggedInUser: String;
    currentUserId: String;
    name: String;
    relationMap: Map<any, Array<Map<String, String>>>;
}