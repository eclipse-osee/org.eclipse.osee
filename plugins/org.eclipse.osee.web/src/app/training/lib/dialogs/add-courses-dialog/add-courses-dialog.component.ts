import { DefaultTrainingCourseRecord } from './../../types/training-course';
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
import { map, Observable } from 'rxjs';
import { MatDialogRef } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';
import { UserDataAccountService } from '@osee/auth';
import { user } from '@osee/shared/types/auth';
import { FormControl, FormGroup } from '@angular/forms';
import {
	TrainingCourse,
	TrainingCourseRecord,
} from './../../types/training-course';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatListModule } from '@angular/material/list';
import { AsyncPipe } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { TrainingCourseService } from './../../services/training-course.service';

@Component({
	standalone: true,
	selector: 'osee-add-courses-dialog',
	templateUrl: './add-courses-dialog.component.html',
	styleUrls: ['./add-courses-dialog.component.sass'],
	imports: [
		MatFormFieldModule,
		MatDividerModule,
		MatSelectModule,
		FormsModule,
		AsyncPipe,
		MatListModule,
		MatDatepickerModule,
		CommonModule,
		MatButtonModule,
		MatDialogModule,
	],
})
export class AddCoursesDialogComponent {
	userInfo: Observable<user> = this.accountService.user;
	test_courses = this.trainingCourseService.getTrainingCourses();

	constructor(
		public dialogRef: MatDialogRef<AddCoursesDialogComponent>,
		private accountService: UserDataAccountService,
		@Inject(MAT_DIALOG_DATA) public data: TrainingCourseRecord,
		private trainingCourseService: TrainingCourseService
	) {}

	onCancelClick() {
		console.log(this.data);
		this.dialogRef.close();
	}
}
