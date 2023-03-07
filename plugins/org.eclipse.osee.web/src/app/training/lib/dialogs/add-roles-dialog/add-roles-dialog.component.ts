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
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
	MatDialogRef,
	MatDialogModule,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { UserDataAccountService } from '@osee/auth';
import { user } from '@osee/shared/types/auth';
import { Observable } from 'rxjs';
import { AddCoursesDialogComponent } from '../add-courses-dialog/add-courses-dialog.component';
import { CommonModule } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TrainingRoleRecord } from './../../types/training-role';
import { TrainingRoleService } from './../../services/training-role.service';

@Component({
	standalone: true,
	selector: 'osee-add-roles-dialog',
	templateUrl: './add-roles-dialog.component.html',
	styleUrls: ['./add-roles-dialog.component.sass'],
	imports: [
		MatFormFieldModule,
		MatDividerModule,
		MatSelectModule,
		FormsModule,
		AsyncPipe,
		MatListModule,
		MatDatepickerModule,
		CommonModule,
		MatInputModule,
		MatButtonModule,
		MatDialogModule,
	],
})
export class AddRolesDialogComponent {
	userInfo: Observable<user> = this.accountService.user;
	test_roles = this.trainingRoleService.getTrainingRoles();

	constructor(
		public dialogRef: MatDialogRef<AddCoursesDialogComponent>,
		private accountService: UserDataAccountService,
		private trainingRoleService: TrainingRoleService,
		@Inject(MAT_DIALOG_DATA) public data: TrainingRoleRecord
	) {}

	onCancelClick() {
		console.log(this.data);
		this.dialogRef.close();
	}
}
