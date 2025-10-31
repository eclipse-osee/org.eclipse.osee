/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { CreateActionFormComponent } from './create-action-form.component';
import {
	ActionService,
	CreateActionService,
} from '@osee/configuration-management/services';
import {
	actionServiceMock,
	createActionServiceMock,
} from '@osee/configuration-management/testing';
import { actionUserServiceMock } from '../../../../configuration-management/testing/action-user.service.mock';
import { ActionUserService } from '../create-action-dialog/internal/action-user.service';
import { CreateAction } from '@osee/configuration-management/types';
import { MockUserResponse } from '@osee/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('CreateActionFormComponent', () => {
	let component: CreateActionFormComponent;
	let fixture: ComponentFixture<CreateActionFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateActionFormComponent],
			providers: [
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
				{
					provide: ActionUserService,
					useValue: actionUserServiceMock,
				},
				{
					provide: ActionService,
					useValue: actionServiceMock,
				},
				provideNoopAnimations(),
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateActionFormComponent);
		component = fixture.componentInstance;
		const create = new CreateAction(MockUserResponse);
		fixture.componentRef.setInput('data', create);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
