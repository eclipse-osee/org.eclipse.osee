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
import { Type } from '@angular/core';
import { AUTH_TYPE } from './auth_types';
import { UserHeaderService } from './user-header.service';

export type environment_type = {
	production: boolean;
	headerService: Type<UserHeaderService>;
	authScheme: AUTH_TYPE;
};
