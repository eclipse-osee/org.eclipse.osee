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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';

import { MockMessageTableComponent } from '@osee/messaging/message-tables/testing';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { messagesMock } from '@osee/messaging/shared/testing';
import { MessagePageComponent } from './message-page.component';
import { MockMessageInterfaceComponent } from './lib/message-interface/message-interface.component.mock';

describe('MessageInterfaceComponent', () => {
	let component: MessagePageComponent;
	let fixture: ComponentFixture<MessagePageComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessagePageComponent, {
			set: {
				imports: [MockMessageInterfaceComponent, RouterTestingModule],
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: {
							filter: '',
							string: '',
							messages: of(messagesMock),
							BranchId: new BehaviorSubject('10'),

							//eslint-disable-next-line @typescript-eslint/no-empty-function
							clearRows() {},
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [
					MessagePageComponent,
					MockMessageTableComponent,
					RouterTestingModule,
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessagePageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
