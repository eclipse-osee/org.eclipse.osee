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
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MessageNodesCountDirective } from '@osee/messaging/shared/directives';
import {
	MockMessageTypeDropdownComponent,
	MockRateDropdownComponent,
} from '@osee/messaging/shared/dropdowns/testing';
import {
	CurrentMessagesService,
	EnumsService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	transportTypeUIServiceMock,
} from '@osee/messaging/shared/testing';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';
import { of } from 'rxjs';
import { AddMessageDialog } from '../../types/AddMessageDialog';

import { AddMessageDialogComponent } from './add-message-dialog.component';

describe('AddMessageDialogComponent', () => {
	let component: AddMessageDialogComponent;
	let fixture: ComponentFixture<AddMessageDialogComponent>;
	let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	let dialogData: AddMessageDialog = {
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
		publisherNodes: [
			{
				id: '',
				name: '',
			},
		],
		subscriberNodes: [
			{
				id: '',
				name: '',
			},
		],
		subMessages: [],
	};
	let enumServiceMock: Partial<EnumsService> = {
		periodicities: of(['p1', 'p2', 'p3']),
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
					{ provide: EnumsService, useValue: enumServiceMock },
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
					MockApplicabilitySelectorComponent,
					MockRateDropdownComponent,
					MockMessageTypeDropdownComponent,
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
					{ provide: EnumsService, useValue: enumServiceMock },
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
