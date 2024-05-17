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
import { ActionsPanelComponent } from './actions-panel.component';
import { ActionService } from '@osee/shared/services';
import { actionServiceMock } from '@osee/shared/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('ActionsPanelComponent', () => {
	let component: ActionsPanelComponent;
	let fixture: ComponentFixture<ActionsPanelComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ActionsPanelComponent],
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				provideNoopAnimations(),
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ActionsPanelComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
