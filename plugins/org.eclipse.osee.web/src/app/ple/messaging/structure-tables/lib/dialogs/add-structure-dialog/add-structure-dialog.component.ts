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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	CurrentStructureService,
	EnumsService,
} from '@osee/messaging/shared/services';
import type { structure } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { AddStructureDialog } from './add-structure-dialog';

@Component({
	selector: 'osee-messaging-add-structure-dialog',
	templateUrl: './add-structure-dialog.component.html',
	styleUrls: ['./add-structure-dialog.component.sass'],
	standalone: true,
	imports: [
		MatStepperModule,
		MatDialogModule,
		MatButtonModule,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatInputModule,
		MatSlideToggleModule,
		MatOptionLoadingComponent,
		NgIf,
		NgFor,
		AsyncPipe,
	],
})
export class AddStructureDialogComponent {
	availableStructures = (pageNum: number | string) =>
		this.structures.getPaginatedStructures(pageNum);
	categories = this.enumService.categories;
	storedId: string = '-1';
	constructor(
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structures: CurrentStructureService,
		public dialogRef: MatDialogRef<AddStructureDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddStructureDialog,
		private enumService: EnumsService
	) {}
	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	createNew() {
		this.data.structure.id = '-1';
	}

	storeId(value: structure) {
		this.storedId = value.id || '-1';
	}

	moveToReview(stepper: MatStepper) {
		this.data.structure.id = this.storedId;
		this.moveToStep(3, stepper);
	}
}
