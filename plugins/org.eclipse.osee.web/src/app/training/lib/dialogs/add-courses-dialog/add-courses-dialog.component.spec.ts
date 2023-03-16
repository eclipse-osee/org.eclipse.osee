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
import { MatListModule } from '@angular/material/list';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TrainingCourseService } from './../../services/training-course.service';
import { AddCoursesDialogComponent } from './add-courses-dialog.component';
import { UserDataAccountService } from '@osee/auth';
import { MatNativeDateModule, MatOptionModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { TrainingCourseServiceMock } from 'src/app/training/lib/testing/training-course.service.mock';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('AddCoursesDialogComponent', () => {
	let component: AddCoursesDialogComponent;
	let fixture: ComponentFixture<AddCoursesDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				FormsModule,
				ReactiveFormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatDatepickerModule,
				MatNativeDateModule,
				MatDividerModule,
				MatSelectModule,
				MatOptionModule,
				BrowserAnimationsModule,
				MatSelectModule,
				MatListModule,
				AddCoursesDialogComponent,
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
					provide: TrainingCourseService,
					useValue: TrainingCourseServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(AddCoursesDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
