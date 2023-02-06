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
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ActionUserService } from '../internal/services/action-user.service';
import { TransitionActionDialogData } from '../transition-action-dialog';
import { action } from '../../../../types/configuration-management/action';

/**
 * @todo figure out where this used to be used??
 */
@Component({
	selector: 'osee-transition-action-to-review-dialog',
	templateUrl: './transition-action-to-review-dialog.component.html',
	styleUrls: ['./transition-action-to-review-dialog.component.sass'],
	standalone: true,
	imports: [
		MatDialogModule,
		MatFormFieldModule,
		FormsModule,
		MatSelectModule,
		MatOptionModule,
		NgFor,
		AsyncPipe,
		MatButtonModule,
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
