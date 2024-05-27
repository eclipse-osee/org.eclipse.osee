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

import { MessageFilterComponent } from './message-filter.component';
import { MimHeaderComponent } from '@osee/messaging/shared/headers';
import { MockMimHeaderComponent } from '@osee/messaging/shared/headers/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('MessageFilterComponent', () => {
	let component: MessageFilterComponent;
	let fixture: ComponentFixture<MessageFilterComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessageFilterComponent, {
			remove: {
				imports: [MimHeaderComponent],
			},
			add: {
				imports: [MockMimHeaderComponent],
			},
		})
			.configureTestingModule({
				imports: [MessageFilterComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(MessageFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
