/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import {
	TransportType,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';
import { TransportTypeFormComponent } from '@osee/messaging/transports/forms';

@Component({
	selector: 'osee-new-transport-type-dialog',
	template: `<h1 mat-dialog-title>Create New Transport Type</h1>
		<osee-transport-type-form
			[transportType]="transportType"
			(completion)="
				receiveFormState($event)
			"></osee-transport-type-form>`,
	styles: [],
	imports: [FormsModule, TransportTypeFormComponent, MatDialogTitle],
})
export class NewTransportTypeDialogComponent {
	dialogRef =
		inject<MatDialogRef<NewTransportTypeDialogComponent>>(MatDialogRef);

	transportType = new TransportType();
	validation = this.transportType.byteAlignValidation
		? this.transportType.byteAlignValidationSize.value !== 0
		: true;

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
