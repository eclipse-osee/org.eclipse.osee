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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddStructureDialog } from '../../types/AddStructureDialog';
import { structure } from '../../../shared/types/structure';

@Component({
	selector: 'osee-messaging-add-structure-dialog',
	templateUrl: './add-structure-dialog.component.html',
	styleUrls: ['./add-structure-dialog.component.sass'],
})
export class AddStructureDialogComponent {
	availableStructures = (pageNum: number | string) =>
		this.structures.getPaginatedStructures(pageNum);
	categories = this.enumService.categories;
	storedId: string = '-1';
	constructor(
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
