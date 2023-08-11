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
import {
	MatOptionLoadingComponent,
	ApplicabilitySelectorComponent,
} from '@osee/shared/components';
import { AddStructureDialog } from './add-structure-dialog';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { BehaviorSubject, debounceTime, map, switchMap } from 'rxjs';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
	selector: 'osee-messaging-add-structure-dialog',
	templateUrl: './add-structure-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatStepperModule,
		MatDialogModule,
		MatButtonModule,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatInputModule,
		MatAutocompleteModule,
		MatSlideToggleModule,
		MatOptionLoadingComponent,
		MatTooltipModule,
		NgIf,
		NgFor,
		AsyncPipe,
		ApplicabilitySelectorComponent,
	],
})
export class AddStructureDialogComponent {
	categories = this.enumService.categories;
	paginationSize: number = 50;

	selectedStructure: structure | undefined;
	structureSearch = new BehaviorSubject<string>('');

	availableStructures = this.structureSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: number | string) =>
				this.structures.getPaginatedStructuresFilteredByName(
					search,
					this.paginationSize,
					pageNum
				)
		)
	);

	availableStructuresCount = this.structureSearch.pipe(
		debounceTime(250),
		switchMap((search) =>
			this.structures.getStructuresFilteredByNameCount(search)
		)
	);

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
		this.selectedStructure = undefined;
	}

	moveToReview(stepper: MatStepper) {
		if (this.selectedStructure) {
			this.data.structure = this.selectedStructure;
		}
		this.moveToStep(3, stepper);
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.structureSearch.next(value);
	}

	selectExistingStructure(structure: structure) {
		this.selectedStructure = structure;
	}
}
