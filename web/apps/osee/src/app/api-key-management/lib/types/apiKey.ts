/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { NamedId } from '@osee/shared/types';

export class ApiKey {
	name: string;
	scopes: keyScope[];
	creationDate: string;
	expirationDate: string;
	uniqueID?: string;

	constructor(
		name: string,
		scopes: keyScope[],
		creationDate: string,
		expirationDate: string,
		uniqueID?: string
	) {
		this.name = name;
		this.scopes = scopes.length > 0 ? scopes : [];
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.uniqueID = uniqueID;
	}
}

export type keyScope = NamedId & { selected: boolean };

export const RequiredApiKeysMock: Required<ApiKey>[] = [
	{
		name: '',
		scopes: [],
		creationDate: '',
		expirationDate: '',
		uniqueID: '',
	},
];

export const RequiredKeyScopesMock: Required<keyScope>[] = [];
