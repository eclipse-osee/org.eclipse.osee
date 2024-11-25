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
import { Component, inject } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	TransportType,
	TransportTypeForm,
	transportTypeAttributes,
} from '@osee/messaging/shared/types';
import { TransportTypeFormComponent } from '@osee/messaging/transports/forms';
import { BehaviorSubject } from 'rxjs';

@Component({
	selector: 'osee-edit-transport-type-dialog',
	imports: [MatDialogTitle, AsyncPipe, TransportTypeFormComponent],
	template: `@if (transportType | async; as _t) {
			<h1 mat-dialog-title>Editing Transport Type {{ _t.name }}</h1>
			<osee-transport-type-form
				[transportType]="_t"
				(completion)="
					receiveFormState($event)
				"></osee-transport-type-form>
		} @else {
			<h1 mat-dialog-title>Editing Transport Type</h1>
		} `,
})
export class EditTransportTypeDialogComponent {
	dialogRef =
		inject<MatDialogRef<EditTransportTypeDialogComponent>>(MatDialogRef);
	data = inject(MAT_DIALOG_DATA);

	protected transportType = new BehaviorSubject<TransportTypeForm>(
		new TransportType()
	);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		const data = this.data;

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
