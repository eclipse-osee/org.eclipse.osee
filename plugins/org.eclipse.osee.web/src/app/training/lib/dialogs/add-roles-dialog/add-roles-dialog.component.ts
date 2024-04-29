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
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
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
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import {
	MatFormField,
	MatHint,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatSelect } from '@angular/material/select';
import { UserDataAccountService } from '@osee/auth';
import { user } from '@osee/shared/types/auth';
import { Observable } from 'rxjs';
import { TrainingRoleService } from '../../services/training-role.service';
import { TrainingRoleRecord } from '../../types/training-role';
import { AddCoursesDialogComponent } from '../add-courses-dialog/add-courses-dialog.component';

@Component({
	standalone: true,
	selector: 'osee-add-roles-dialog',
	templateUrl: './add-roles-dialog.component.html',
	styles: [],
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatLabel,
		MatDivider,
		MatFormField,
		MatSelect,
		MatOption,
		MatSelectionList,
		MatListOption,
		MatDateRangeInput,
		MatStartDate,
		MatEndDate,
		MatHint,
		MatDatepickerToggle,
		MatSuffix,
		MatDateRangePicker,
		MatDatepickerActions,
		MatButton,
		MatDatepickerCancel,
		MatDatepickerApply,
		MatDialogActions,
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
