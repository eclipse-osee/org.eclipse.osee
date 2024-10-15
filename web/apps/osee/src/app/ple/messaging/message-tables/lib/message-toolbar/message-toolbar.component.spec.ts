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

import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MessageTableToolbarAddActionsComponent } from '../message-table-toolbar-add-actions/message-table-toolbar-add-actions.component';
import { MockMessageTableToolbarAddActionsComponent } from '../message-table-toolbar-add-actions/message-table-toolbar-add-actions.component.mock';
import { MessageToolbarComponent } from './message-toolbar.component';

describe('MessageToolbarComponent', () => {
	let component: MessageToolbarComponent;
	let fixture: ComponentFixture<MessageToolbarComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessageToolbarComponent, {
			remove: {
				imports: [MessageTableToolbarAddActionsComponent],
			},
			add: {
				imports: [MockMessageTableToolbarAddActionsComponent],
			},
		})
			.configureTestingModule({
				imports: [MessageToolbarComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(MessageToolbarComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
