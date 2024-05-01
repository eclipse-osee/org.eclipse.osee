/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { TestBed } from '@angular/core/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { actionServiceMock, BranchInfoServiceMock } from '@osee/shared/testing';
import { ActionService } from '../http/action.service';
import { BranchInfoService } from '../http/branch-info.service';

import { CurrentActionService } from './current-action.service';

describe('CurrentActionServiceService', () => {
	let service: CurrentActionService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentActionService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
