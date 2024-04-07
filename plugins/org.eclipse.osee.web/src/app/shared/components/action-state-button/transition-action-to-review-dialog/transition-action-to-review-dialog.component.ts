/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { AsyncPipe, NgFor } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { action } from '@osee/shared/types/configuration-management';
import { ActionUserService } from '../internal/services/action-user.service';
import { TransitionActionDialogData } from '../transition-action-dialog';

/**
 * @todo figure out where this used to be used??
 */
@Component({
	selector: 'osee-transition-action-to-review-dialog',
	templateUrl: './transition-action-to-review-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		NgFor,
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class TransitionActionToReviewDialogComponent {
	actionInfo: action;
	users = this.userService.usersSorted;
	constructor(
		public dialogRef: MatDialogRef<TransitionActionToReviewDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: TransitionActionDialogData,
		private userService: ActionUserService
	) {
		this.actionInfo = this.data.actions[0];
	}

	onNoClick() {
		this.dialogRef.close();
	}
}
