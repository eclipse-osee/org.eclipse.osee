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

import { MessageTablePaginatorComponent } from './message-table-paginator.component';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { CurrentMessageServiceMock } from '@osee/messaging/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('MessageTablePaginatorComponent', () => {
	let component: MessageTablePaginatorComponent;
	let fixture: ComponentFixture<MessageTablePaginatorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MessageTablePaginatorComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MessageTablePaginatorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('messagesCount', 10);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
