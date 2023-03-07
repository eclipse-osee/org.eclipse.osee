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
import { AddCoursesDialogComponent } from './lib/dialogs/add-courses-dialog/add-courses-dialog.component';
import { AddRolesDialogComponent } from './lib/dialogs/add-roles-dialog/add-roles-dialog.component';
import { EditCoursesDropdownComponent } from './lib/dropdowns/edit-courses-dropdown/edit-courses-dropdown.component';
import { EditRolesDropdownComponent } from './lib/dropdowns/edit-roles-dropdown/edit-roles-dropdown.component';
import { TrainingCourseTableComponent } from './lib/tables/training-course-table/training-course-table.component';
import { TrainingRoleTableComponent } from './lib/tables/training-role-table/training-role-table.component';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
	standalone: true,
	selector: 'osee-training',
	templateUrl: './training.component.html',
	styleUrls: ['./training.component.sass'],
	imports: [
		EditCoursesDropdownComponent,
		EditRolesDropdownComponent,
		TrainingRoleTableComponent,
		TrainingCourseTableComponent,
		AddRolesDialogComponent,
		AddCoursesDialogComponent,
		MatDialogModule,
	],
})
export class TrainingComponent {
	constructor() {}
}
export default TrainingComponent;
