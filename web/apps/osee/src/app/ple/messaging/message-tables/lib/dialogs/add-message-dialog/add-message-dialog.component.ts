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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe } from '@angular/common';
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
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MessageNodesCountDirective } from '@osee/messaging/shared/directives';
import {
	MessageTypeDropdownComponent,
	RateDropdownComponent,
} from '@osee/messaging/shared/dropdowns';
import {
	CurrentMessagesService,
	EnumsService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { AddSubMessageDialogComponent } from '../add-sub-message-dialog/add-sub-message-dialog.component';

@Component({
	selector: 'osee-messaging-add-message-dialog',
	templateUrl: './add-message-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		CdkTextareaAutosize,
		MatSelect,
		MatOption,
		MatSlideToggle,
		MatError,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		FormsModule,
		AsyncPipe,
		MessageNodesCountDirective,
		ApplicabilitySelectorComponent,
		RateDropdownComponent,
		MessageTypeDropdownComponent,
	],
})
export class AddMessageDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<AddSubMessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddMessageDialog,
		private enumService: EnumsService,
		private currentMessagesService: CurrentMessagesService,
		private transportTypeService: TransportTypeUiService
	) {}

	periodicities = this.enumService.periodicities;
	nodes = this.currentMessagesService.connectionNodes;
	transportType = this.transportTypeService.currentTransportType;

	onNoClick() {
		this.dialogRef.close();
	}
	compareIds(o1: { id: string }, o2: { id: string }[]) {
		return (
			o1 !== null && o2 !== null && o2.map((v) => v.id).includes(o1.id)
		);
	}
}
