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
import { TextFieldModule } from '@angular/cdk/text-field';
import { AsyncPipe, CommonModule, NgFor, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MessageNodesCountDirective } from '@osee/messaging/shared/directives';
import {
	CurrentMessagesService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	transportTypeUIServiceMock,
} from '@osee/messaging/shared/testing';
import { AddMessageDialog } from '../../types/AddMessageDialog';

import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { AddMessageDialogComponent } from './add-message-dialog.component';
import { MockRateDropdownComponent } from '@osee/messaging/rate/rate-dropdown/testing';
import { MockMessageTypeDropdownComponent } from '@osee/messaging/message-type/message-type-dropdown/testing';
import { MockMessagePeriodicityDropdownComponent } from '@osee/messaging/message-periodicity/message-periodicity-dropdown/testing';
import { MockNodeDropdownComponent } from '@osee/messaging/nodes/dropdown/testing';
import { MatTooltip } from '@angular/material/tooltip';

describe('AddMessageDialogComponent', () => {
	let component: AddMessageDialogComponent;
	let fixture: ComponentFixture<AddMessageDialogComponent>;
	const dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	const dialogData: AddMessageDialog = {
		id: '-1',
		name: '',
		description: '',
		interfaceMessageNumber: '',
		interfaceMessagePeriodicity: '',
		interfaceMessageRate: '',
		interfaceMessageType: '',
		interfaceMessageWriteAccess: false,
		applicability: {
			id: '1',
			name: 'Base',
		},
		publisherNodes: [],
		subscriberNodes: [],
		subMessages: [],
	};

	beforeEach(async () => {
		await TestBed.overrideComponent(AddMessageDialogComponent, {
			set: {
				providers: [
					{
						provide: MatDialogRef,
						useValue: dialogRef,
					},
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
					{
						provide: TransportTypeUiService,
						useValue: transportTypeUIServiceMock,
					},
				],
				imports: [
					MatDialogModule,
					MatInputModule,
					MatFormFieldModule,
					MatOptionModule,
					MatSlideToggleModule,
					MatButtonModule,
					MatSelectModule,
					TextFieldModule,
					FormsModule,
					NgFor,
					NgIf,
					AsyncPipe,
					MessageNodesCountDirective,
					MockApplicabilityDropdownComponent,
					MockRateDropdownComponent,
					MockMessageTypeDropdownComponent,
					MockMessagePeriodicityDropdownComponent,
					MockNodeDropdownComponent,
					MatTooltip,
				],
			},
		})
			.configureTestingModule({
				imports: [
					CommonModule,
					MatDialogModule,
					FormsModule,
					MatFormFieldModule,
					MatInputModule,
					MatSelectModule,
					MatButtonModule,
					MatSlideToggleModule,
					NoopAnimationsModule,
					AddMessageDialogComponent,
				],
				declarations: [],
				providers: [
					{
						provide: MatDialogRef,
						useValue: dialogRef,
					},
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddMessageDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
