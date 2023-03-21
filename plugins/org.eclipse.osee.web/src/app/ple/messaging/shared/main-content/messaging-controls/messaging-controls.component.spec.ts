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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import {
	preferencesUiServiceMock,
	ViewSelectorMockComponent,
} from '@osee/messaging/shared/testing';
import {
	ActionDropdownStub,
	BranchPickerStub,
	UndoButtonBranchMockComponent,
} from '@osee/shared/components/testing';

import { MessagingControlsComponent } from './messaging-controls.component';

describe('MessagingControlsComponent', () => {
	let component: MessagingControlsComponent;
	let fixture: ComponentFixture<MessagingControlsComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessagingControlsComponent, {
			set: {
				imports: [
					CommonModule,
					RouterLink,
					MatButtonModule,
					ActionDropdownStub,
					BranchPickerStub,
					UndoButtonBranchMockComponent,
					ViewSelectorMockComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MessagingControlsComponent,
					MatButtonModule,
					ActionDropdownStub,
					BranchPickerStub,
					UndoButtonBranchMockComponent,
					ViewSelectorMockComponent,
				],
				providers: [
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(MessagingControlsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
