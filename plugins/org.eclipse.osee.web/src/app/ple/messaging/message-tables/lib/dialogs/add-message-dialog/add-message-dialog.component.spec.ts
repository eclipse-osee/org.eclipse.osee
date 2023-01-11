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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
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
import { of } from 'rxjs';
import { EnumsService } from '../../../../shared/services/http/enums.service';
import { CurrentMessagesService } from '../../../../shared/services/ui/current-messages.service';
import { CurrentMessageServiceMock } from '../../../../shared/testing/current-messages.service.mock';
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
		initiatingNode: {
			id: '',
			name: '',
		},
	};
	let enumServiceMock: Partial<EnumsService> = {
		types: of(['type1', 'type2', 'type3']),
		rates: of(['r1', 'r2', 'r3']),
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
