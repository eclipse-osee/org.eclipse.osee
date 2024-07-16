/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { NgFor } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	dialogRef,
	MockTransportTypeFormComponent,
} from '@osee/messaging/shared/testing';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';

import { NewTransportTypeDialogComponent } from './new-transport-type-dialog.component';

describe('NewTransportTypeDialogComponent', () => {
	let component: NewTransportTypeDialogComponent;
	let fixture: ComponentFixture<NewTransportTypeDialogComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(NewTransportTypeDialogComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatFormFieldModule,
					MatInputModule,
					FormsModule,
					MatSlideToggleModule,
					MatSelectModule,
					MatOptionModule,
					NgFor,
					MatButtonModule,
					MockTransportTypeFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					MatFormFieldModule,
					MatInputModule,
					MatSelectModule,
					MockMatOptionLoadingComponent,
					MatButtonModule,
					FormsModule,
					NoopAnimationsModule,
					MatSlideToggleModule,
				],
				providers: [{ provide: MatDialogRef, useValue: dialogRef }],
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewTransportTypeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
