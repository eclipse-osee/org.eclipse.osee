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
import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { Observable, map } from 'rxjs';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourse } from '../../types/training-course';

@Component({
	selector: 'osee-course-info-dialog',
	standalone: true,
	imports: [
		CommonModule,
		MatDialogTitle,
		MatDialogContent,
		MatIcon,
		MatDialogActions,
		MatButton,
	],
	templateUrl: './course-info-dialog.component.html',
	styles: [],
})
export class CourseInfoDialogComponent {
	courses$: Observable<TrainingCourse[]>;

	constructor(
		@Inject(MAT_DIALOG_DATA) public data: string,
		public dialogRef: MatDialogRef<CourseInfoDialogComponent>,
		private trainingCourseService: TrainingCourseService
	) {
		this.courses$ = this.trainingCourseService
			.getTrainingCourses()
			.pipe(
				map((items) =>
					items.filter((item) => item.courseID === this.data)
				)
			);
	}

	goToLink(url: string) {
		window.open(url, '_blank');
	}

	onCancelClick() {
		this.dialogRef.close();
	}
}
