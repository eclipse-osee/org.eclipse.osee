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
import { Component, Inject } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import {
	TransportType,
	transportType,
	transportTypeAttributes,
	TransportTypeForm,
} from '@osee/messaging/shared/types';
import { BehaviorSubject, Subject } from 'rxjs';
import { TransportTypeFormComponent } from '@osee/messaging/shared/forms';

@Component({
	selector: 'osee-edit-transport-type-dialog',
	standalone: true,
	imports: [MatDialogModule, NgIf, AsyncPipe, TransportTypeFormComponent],
	templateUrl: './edit-transport-type-dialog.component.html',
	styles: [],
})
export class EditTransportTypeDialogComponent {
	protected transportType = new BehaviorSubject<TransportTypeForm>(
		new TransportType()
	);
	constructor(
		public dialogRef: MatDialogRef<EditTransportTypeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: transportType
	) {
		this.transportType.next(new TransportType(data));
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
	receiveFormState(
		state:
			| { type: 'CANCEL' }
			| { type: 'SUBMIT'; data: transportTypeAttributes }
	) {
		if (state.type === 'CANCEL') {
			return this.onNoClick();
		}
		return this.dialogRef.close(state.data);
	}
}
