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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TrainingRoleServiceMock } from './../../testing/training-role.service.mock';
import { TrainingRoleService } from './../../services/training-role.service';
import { AddRolesDialogComponent } from './add-roles-dialog.component';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

describe('AddRolesDialogComponent', () => {
	let component: AddRolesDialogComponent;
	let fixture: ComponentFixture<AddRolesDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				MatInputModule,
				FormsModule,
				ReactiveFormsModule,
				MatDividerModule,
				MatSelectModule,
				MatListModule,
				MatDatepickerModule,
				MatNativeDateModule,
				MatSelectModule,
				BrowserAnimationsModule,
				AddRolesDialogComponent,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {},
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: TrainingRoleService,
					useValue: TrainingRoleServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(AddRolesDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
