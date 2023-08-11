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
import { TextFieldModule } from '@angular/cdk/text-field';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
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
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MessageNodesCountDirective } from '@osee/messaging/shared/directives';
import {
	EnumsService,
	CurrentMessagesService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { AddSubMessageDialogComponent } from '../add-sub-message-dialog/add-sub-message-dialog.component';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import {
	RateDropdownComponent,
	MessageTypeDropdownComponent,
} from '@osee/messaging/shared/dropdowns';

@Component({
	selector: 'osee-messaging-add-message-dialog',
	templateUrl: './add-message-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogModule,
		MatInputModule,
		MatFormFieldModule,
		MatOptionModule,
		MatSlideToggleModule,
		MatButtonModule,
		MatSelectModule,
		TextFieldModule,
		FormsModule,
		NgFor,
		NgIf,
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
}
