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
import { NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import type { transportType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-transport-type-dialog',
	templateUrl: './new-transport-type-dialog.component.html',
	styleUrls: ['./new-transport-type-dialog.component.sass'],
	standalone: true,
	imports: [
		MatDialogModule,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		MatSlideToggleModule,
		MatSelectModule,
		MatOptionModule,
		NgFor,
		MatButtonModule,
	],
})
export class NewTransportTypeDialogComponent {
	transportType: transportType = {
		name: '',
		byteAlignValidation: false,
		messageGeneration: false,
		byteAlignValidationSize: 0,
		messageGenerationType: '',
		messageGenerationPosition: '',
		minimumPublisherMultiplicity: 0,
		maximumPublisherMultiplicity: 0,
		minimumSubscriberMultiplicity: 0,
		maximumSubscriberMultiplicity: 0,
	};
	generationTypes = ['None', 'Dynamic', 'Relational', 'Static'];
	validation = this.transportType.byteAlignValidation
		? this.transportType.byteAlignValidationSize !== 0
		: true;

	constructor(
		public dialogRef: MatDialogRef<NewTransportTypeDialogComponent>
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}
}
