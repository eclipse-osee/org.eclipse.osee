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
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddCoursesDialogComponent } from '../../dialogs/add-courses-dialog/add-courses-dialog.component';
import { TrainingCourseRecord } from './../../types/training-course';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { filter, take } from 'rxjs/operators';
import { OperatorFunction } from 'rxjs';
import { DefaultTrainingCourseRecord } from './../../types/training-course';

@Component({
	standalone: true,
	selector: 'osee-edit-courses-dropdown',
	templateUrl: './edit-courses-dropdown.component.html',
	styles: [],
	imports: [
		MatMenuModule,
		MatIconModule,
		MatButtonModule,
		MatNativeDateModule,
	],
})
export class EditCoursesDropdownComponent {
	constructor(public dialog: MatDialog) {}

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
