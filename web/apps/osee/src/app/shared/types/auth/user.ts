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
export type user = {
	id: `${number}`;
	name: string;
	guid: null;
	active: boolean;
	description: null;
	workTypes: unknown[];
	tags: unknown[];
	userId: string;
	email: string;
	loginIds: string[];
	savedSearches: unknown[];
	userGroups: unknown[];
	artifactId: string;
	idString: string;
	idIntValue: number;
	uuid: number;
	roles: userRole[];
};

export type userRole = {
	id: string;
	name: string;
};
