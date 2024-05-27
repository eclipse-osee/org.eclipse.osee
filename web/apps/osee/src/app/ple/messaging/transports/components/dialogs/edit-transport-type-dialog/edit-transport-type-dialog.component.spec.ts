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
import { NgIf, AsyncPipe } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	MockTransportTypeFormComponent,
	dialogRef,
	transportTypes,
} from '@osee/messaging/shared/testing';

import { EditTransportTypeDialogComponent } from './edit-transport-type-dialog.component';

describe('EditTransportTypeDialogComponent', () => {
	let component: EditTransportTypeDialogComponent;
	let fixture: ComponentFixture<EditTransportTypeDialogComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(EditTransportTypeDialogComponent, {
			set: {
				imports: [
					MatDialogModule,
					NgIf,
					AsyncPipe,
					MockTransportTypeFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					NoopAnimationsModule,
					EditTransportTypeDialogComponent,
				],
				providers: [
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: transportTypes[0] },
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(EditTransportTypeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
