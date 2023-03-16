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
import { Component, Inject } from '@angular/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { TextFieldModule } from '@angular/cdk/text-field';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import type { subMessage } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-messaging-add-sub-message-dialog',
	templateUrl: './add-sub-message-dialog.component.html',
	styleUrls: ['./add-sub-message-dialog.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		FormsModule,
		MatFormFieldModule,
		MatStepperModule,
		MatOptionLoadingComponent,
		MatOptionModule,
		MatButtonModule,
		MatSelectModule,
		MatInputModule,
		TextFieldModule,
		MatInputModule,
		MatDialogModule,
	],
})
export class AddSubMessageDialogComponent {
	availableSubMessages = (pageNum: string | number) =>
		this.messageService.getPaginatedSubMessages(pageNum);
	storedId: string = '-1';
	constructor(
		public dialogRef: MatDialogRef<AddSubMessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddSubMessageDialog,
		private messageService: CurrentMessagesService
	) {}

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	createNew() {
		this.data.subMessage.id = '-1';
	}

	storeId(value: subMessage) {
		this.storedId = value.id || '-1';
	}

	moveToReview(stepper: MatStepper) {
		this.data.subMessage.id = this.storedId;
		this.moveToStep(3, stepper);
	}
}
