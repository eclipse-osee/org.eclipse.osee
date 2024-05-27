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

import { MessageInterfaceComponent } from './message-interface.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { CurrentMessageServiceMock } from '@osee/messaging/shared/testing';
import { MessageToolbarComponent } from '../message-toolbar/message-toolbar.component';
import { MockMessageToolbarComponent } from '../message-toolbar/message-toolbar.component.mock';
import { MessageTableComponent } from '../tables/message-table/message-table.component';
import { MockMessageTableComponent } from '@osee/messaging/message-tables/testing';
import { MockMessagingControlsComponent } from '@osee/messaging/shared/main-content/testing';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { CurrentViewSelectorComponent } from '@osee/shared/components';
import { MockCurrentViewSelectorComponent } from '@osee/shared/components/testing';

describe('MessageInterfaceComponent', () => {
	let component: MessageInterfaceComponent;
	let fixture: ComponentFixture<MessageInterfaceComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessageInterfaceComponent, {
			remove: {
				imports: [
					MessageToolbarComponent,
					MessageTableComponent,
					MessagingControlsComponent,
					CurrentViewSelectorComponent,
				],
			},
			add: {
				imports: [
					MockMessageToolbarComponent,
					MockMessageTableComponent,
					MockMessagingControlsComponent,
					MockCurrentViewSelectorComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [MessageInterfaceComponent],
				providers: [
					provideNoopAnimations(),
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(MessageInterfaceComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
