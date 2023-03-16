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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';

import { MessagingHelpNavigationComponent } from './messaging-help-navigation.component';

describe('MessagingHelpNavigationComponent', () => {
	let component: MessagingHelpNavigationComponent;
	let fixture: ComponentFixture<MessagingHelpNavigationComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MessagingHelpNavigationComponent, RouterTestingModule],
			providers: [
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MessagingHelpNavigationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
