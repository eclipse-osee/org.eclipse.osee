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
import { Component, inject, linkedSignal, signal } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	TransportType,
	transportType,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';
import { TransportTypeFormComponent } from '@osee/messaging/transports/forms';

@Component({
	selector: 'osee-edit-transport-type-dialog',
	imports: [MatDialogTitle, TransportTypeFormComponent],
	template: `
		<h1 mat-dialog-title>
			Editing Transport Type {{ transportType().name }}
		</h1>
		<osee-transport-type-form
			[transportType]="transportType()"
			(completion)="receiveFormState($event)"></osee-transport-type-form>
	`,
})
export class EditTransportTypeDialogComponent {
	dialogRef =
		inject<MatDialogRef<EditTransportTypeDialogComponent>>(MatDialogRef);
	data = signal(inject<transportType>(MAT_DIALOG_DATA));
	protected transportType = linkedSignal(
		() => new TransportType(this.data())
	);

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
