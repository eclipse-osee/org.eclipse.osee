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

import { MessageTableToolbarAddActionsComponent } from './message-table-toolbar-add-actions.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { CurrentMessageServiceMock } from '@osee/messaging/shared/testing';

describe('MessageTableToolbarAddActionsComponent', () => {
	let component: MessageTableToolbarAddActionsComponent;
	let fixture: ComponentFixture<MessageTableToolbarAddActionsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MessageTableToolbarAddActionsComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			MessageTableToolbarAddActionsComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
