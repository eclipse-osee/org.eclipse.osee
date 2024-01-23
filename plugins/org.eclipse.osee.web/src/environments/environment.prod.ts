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

import { apiURL } from './api';
import { environment_type } from './environments.types';
import { UserHeaderProdService } from './internal/user-header-prod.service';
export const environment: environment_type = {
	production: true,
	headerService: UserHeaderProdService,
	authScheme: 'FORCED_SSO',
};
export const OSEEAuthURL = apiURL + '/orcs/datastore/user';
