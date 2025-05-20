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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateActionWorkingBranchButtonComponent } from './create-action-working-branch-button.component';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { BranchRoutedUIService } from '@osee/shared/services';
import { branchRoutedUiServiceMock } from '@osee/shared/testing';

describe('CreateActionWorkingBranchButtonComponent', () => {
	let component: CreateActionWorkingBranchButtonComponent;
	let fixture: ComponentFixture<CreateActionWorkingBranchButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateActionWorkingBranchButtonComponent],
			providers: [
				{
					provide: ActionService,
					useValue: actionServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			CreateActionWorkingBranchButtonComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
