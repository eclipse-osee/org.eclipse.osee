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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { MatTooltip } from '@angular/material/tooltip';
import {
	CurrentStructureService,
	EnumsService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { structure } from '@osee/messaging/shared/types';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import {
	BehaviorSubject,
	Subject,
	debounceTime,
	delay,
	distinct,
	map,
	switchMap,
	tap,
} from 'rxjs';
import { AddStructureDialog } from './add-structure-dialog';

@Component({
	selector: 'osee-messaging-add-structure-dialog',
	templateUrl: './add-structure-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatDialogContent,
		MatButton,
		MatStepperNext,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatTooltip,
		MatDialogActions,
		MatHint,
		CdkTextareaAutosize,
		MatSelect,
		MatStepperPrevious,
		MatDialogClose,
		MatOptionLoadingComponent,
		NgIf,
		NgFor,
		FormsModule,
		AsyncPipe,
		ApplicabilitySelectorComponent,
	],
})
export class AddStructureDialogComponent {
	@ViewChild(MatStepper) set _internalStepper(stepper: MatStepper) {
		this.__internalStepper.next(stepper);
	}
	__internalStepper = new Subject<MatStepper>();

	_firstStepFilled = new BehaviorSubject<boolean>(true);
	private _moveToNextStep = this.__internalStepper.pipe(
		debounceTime(1),
		delay(1),
		distinct(),
		tap((stepper) => {
			if (
				stepper &&
				this.data.structure.id !== undefined &&
				this.data.structure.id !== '-1'
			) {
				stepper.next();
				this._firstStepFilled.next(false);
			}
		})
	);
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
	) {
		this._moveToNextStep.subscribe();
	}

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
