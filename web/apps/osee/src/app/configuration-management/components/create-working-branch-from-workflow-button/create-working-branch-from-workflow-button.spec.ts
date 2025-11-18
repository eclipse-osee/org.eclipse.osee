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

import { CreateWorkingBranchFromWorkflowButtonComponent } from './create-working-branch-from-workflow-button';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { BranchRoutedUIService } from '@osee/shared/services';
import {
	branchRoutedUiServiceMock,
	teamWorkflowDetailsMock,
} from '@osee/shared/testing';

describe('CreateWorkingBranchFromWorkflowButtonComponent', () => {
	let component: CreateWorkingBranchFromWorkflowButtonComponent;
	let fixture: ComponentFixture<CreateWorkingBranchFromWorkflowButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateWorkingBranchFromWorkflowButtonComponent],
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
			CreateWorkingBranchFromWorkflowButtonComponent
		);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('teamWorkflow', teamWorkflowDetailsMock);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
