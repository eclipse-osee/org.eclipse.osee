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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { OperatorFunction } from 'rxjs';
import { filter, take } from 'rxjs/operators';
import { AddCoursesDialogComponent } from '../../dialogs/add-courses-dialog/add-courses-dialog.component';
import {
	DefaultTrainingCourseRecord,
	TrainingCourseRecord,
} from '../../types/training-course';

@Component({
	selector: 'osee-edit-courses-dropdown',
	templateUrl: './edit-courses-dropdown.component.html',
	styles: [],
	imports: [MatButton, MatMenuTrigger, MatIcon, MatMenu, MatMenuItem],
})
export class EditCoursesDropdownComponent {
	dialog = inject(MatDialog);

	addCourses() {
		this.dialog
			.open(AddCoursesDialogComponent, {
				minWidth: '60%',
				data: new DefaultTrainingCourseRecord(),
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined) as OperatorFunction<
					TrainingCourseRecord | undefined,
					TrainingCourseRecord
				>
				//switchmap stuff here calling the training course service
			)
			.subscribe();
	}
}
