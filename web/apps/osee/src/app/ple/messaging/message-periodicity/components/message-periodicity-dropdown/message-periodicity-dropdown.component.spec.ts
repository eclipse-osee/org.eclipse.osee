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

import { MessagePeriodicityDropdownComponent } from './message-periodicity-dropdown.component';
import { CurrentMessagePeriodicityService } from '@osee/messaging/message-periodicity/services';
import { CurrentMessagePeriodicitiesServiceMock } from '@osee/messaging/message-periodicity/services/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('MessagePeriodicityDropdownComponent', () => {
	let component: MessagePeriodicityDropdownComponent;
	let fixture: ComponentFixture<MessagePeriodicityDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MessagePeriodicityDropdownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentMessagePeriodicityService,
					useValue: CurrentMessagePeriodicitiesServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MessagePeriodicityDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
