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
import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import {
	CurrentMessagesService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	transportTypeUIServiceMock,
} from '@osee/messaging/shared/testing';
import { EditMessageNodesFieldComponent } from './edit-message-nodes-field.component';

describe('EditMessageNodesFieldComponent', () => {
	let component: EditMessageNodesFieldComponent;
	let fixture: ComponentFixture<EditMessageNodesFieldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{
					provide: TransportTypeUiService,
					useValue: transportTypeUIServiceMock,
				},
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
			],
			imports: [
				EditMessageNodesFieldComponent,
				FormsModule,
				MatFormFieldModule,
				MatSelectModule,
				MatOptionModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(EditMessageNodesFieldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
