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
import { of } from 'rxjs';
import { UserRoles } from '@osee/shared/types/auth';
import { UserDataAccountService } from './user-data-account.service';
import { MockUserResponse } from '@osee/shared/testing';

export const userDataAccountServiceMock: Partial<UserDataAccountService> = {
	user: of(MockUserResponse),
	userHasRoles(roles: UserRoles[]) {
		return of(roles.length === 0);
	},
};

export const userDataAccountAdminServiceMock: Partial<UserDataAccountService> =
	{
		user: of(MockUserResponse),
		userHasRoles(roles: UserRoles[]) {
			return of(true);
		},
	};
