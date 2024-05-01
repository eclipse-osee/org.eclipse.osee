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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { UserDataAccountService } from '../../auth/user-data-account.service';
import { userDataAccountServiceMock } from '../../auth/user-data-account.service.mock';

import { MessagingComponent } from './messaging.component';

@Component({
	selector: 'osee-mock-help',
	template: '<p>Dummy</p>',
	standalone: true,
	imports: [MessagingComponent, MatIconModule],
})
class MessagingHelpDummyComponent {}

@Component({
	selector: 'osee-mock-main',
	template: '<p>Dummy</p>',
	standalone: true,
	imports: [MessagingComponent, MatIconModule],
})
class MessagingMainMockComponent {}

@Component({
	selector: 'osee-mock-type-search',
	template: '<p>Dummy</p>',
	standalone: true,
	imports: [MessagingComponent, MatIconModule],
})
class MessagingTypeSearchMockComponent {}

describe('MessagingComponent', () => {
	let component: MessagingComponent;
	let fixture: ComponentFixture<MessagingComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MessagingComponent,
				RouterTestingModule.withRoutes([
					{
						path: 'connections',
						component: MessagingMainMockComponent,
					},
					{
						path: 'typeSearch',
						component: MessagingTypeSearchMockComponent,
					},
					{ path: 'help', component: MessagingHelpDummyComponent },
				]),
				MatIconModule,
				MessagingMainMockComponent,
				MessagingTypeSearchMockComponent,
				MessagingHelpDummyComponent,
			],
			providers: [
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessagingComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
