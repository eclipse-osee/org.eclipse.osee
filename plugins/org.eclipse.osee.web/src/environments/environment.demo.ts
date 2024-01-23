/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { UserHeaderDemoService } from './internal/user-header-demo.service';
import { environment_type } from './environments.types';
export const environment: environment_type = {
	production: true,
	headerService: UserHeaderDemoService,
	authScheme: 'DEMO',
};
export const OSEEAuthURL = apiURL + '/orcs/datastore/user';
