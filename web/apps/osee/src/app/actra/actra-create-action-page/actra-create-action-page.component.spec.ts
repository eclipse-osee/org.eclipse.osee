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

import { ActraCreateActionPageComponent } from './actra-create-action-page.component';
import {
	ActionService,
	CreateActionService,
} from '@osee/configuration-management/services';
import {
	actionServiceMock,
	createActionServiceMock,
} from '@osee/configuration-management/testing';
import { ActionUserService } from '../../configuration-management/components/create-action-button/create-action-dialog/internal/action-user.service';
import { actionUserServiceMock } from '../../configuration-management/testing/action-user.service.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ActraCreateActionPageComponent', () => {
	let component: ActraCreateActionPageComponent;
	let fixture: ComponentFixture<ActraCreateActionPageComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ActraCreateActionPageComponent],
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

		fixture = TestBed.createComponent(ActraCreateActionPageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
