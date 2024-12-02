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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatDateRangeInput,
	MatDateRangePicker,
	MatDatepickerActions,
	MatDatepickerApply,
	MatDatepickerCancel,
	MatDatepickerToggle,
	MatEndDate,
	MatStartDate,
} from '@angular/material/datepicker';
import {
	MAT_DIALOG_DATA,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatOption, MatSelect } from '@angular/material/select';
import { UserDataAccountService } from '@osee/auth';
import { user } from '@osee/shared/types/auth';
import { Observable } from 'rxjs';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourseRecord } from '../../types/training-course';

@Component({
	selector: 'osee-add-courses-dialog',
	templateUrl: './add-courses-dialog.component.html',
	styles: [],
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatLabel,
		MatFormField,
		MatSelect,
		MatOption,
		MatDivider,
		MatSelectionList,
		MatListOption,
		MatDateRangeInput,
		MatStartDate,
		MatEndDate,
		MatHint,
		MatDatepickerToggle,
		MatDateRangePicker,
		MatDatepickerActions,
		MatButton,
		MatDatepickerCancel,
		MatDatepickerApply,
	],
})
export class AddCoursesDialogComponent {
	dialogRef = inject<MatDialogRef<AddCoursesDialogComponent>>(MatDialogRef);
	private accountService = inject(UserDataAccountService);
	data = inject<TrainingCourseRecord>(MAT_DIALOG_DATA);
	private trainingCourseService = inject(TrainingCourseService);

	userInfo: Observable<user> = this.accountService.user;
	test_courses = this.trainingCourseService.getTrainingCourses();

	onCancelClick() {
		console.log(this.data);
		this.dialogRef.close();
	}
}
