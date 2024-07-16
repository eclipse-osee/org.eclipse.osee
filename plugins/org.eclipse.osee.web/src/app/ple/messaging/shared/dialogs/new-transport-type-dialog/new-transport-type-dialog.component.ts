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
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { TransportTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	TransportType,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-transport-type-dialog',
	templateUrl: './new-transport-type-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [FormsModule, TransportTypeFormComponent, MatDialogTitle],
})
export class NewTransportTypeDialogComponent {
	transportType = new TransportType();
	validation = this.transportType.byteAlignValidation
		? this.transportType.byteAlignValidationSize !== 0
		: true;

	constructor(
		public dialogRef: MatDialogRef<NewTransportTypeDialogComponent>
	) {}

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
