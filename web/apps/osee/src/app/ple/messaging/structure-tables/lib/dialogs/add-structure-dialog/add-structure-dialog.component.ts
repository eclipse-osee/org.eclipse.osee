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
import { AsyncPipe } from '@angular/common';
import { Component, inject, viewChild } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
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
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { MatTooltip } from '@angular/material/tooltip';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { applicabilitySentinel } from '@osee/applicability/types';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { structure } from '@osee/messaging/shared/types';
import { StructureCategoryDropdownComponent } from '@osee/messaging/structure-category/structure-category-dropdown';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	BehaviorSubject,
	debounceTime,
	delay,
	distinct,
	map,
	switchMap,
	tap,
} from 'rxjs';

@Component({
	selector: 'osee-messaging-add-structure-dialog',
	templateUrl: './add-structure-dialog.component.html',
	styles: [],
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
		MatStepperPrevious,
		MatDialogClose,
		MatOptionLoadingComponent,
		FormsModule,
		AsyncPipe,
		ApplicabilityDropdownComponent,
		StructureCategoryDropdownComponent,
	],
	providers: [
		{
			provide: STRUCTURE_SERVICE_TOKEN,
			useExisting: CurrentStructureService,
		},
	],
})
export class AddStructureDialogComponent {
	private structures = inject(STRUCTURE_SERVICE_TOKEN);
	dialogRef = inject<MatDialogRef<AddStructureDialogComponent>>(MatDialogRef);
	data = inject(MAT_DIALOG_DATA);

	_internalStepper = viewChild.required(MatStepper);
	__internalStepper = toObservable(this._internalStepper);

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
	paginationSize = 50;

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

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		this._moveToNextStep.subscribe();
	}

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	createNew() {
		this.data.structure.id = '-1';
		this.selectedStructure = undefined;
		this.data.structure = {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			nameAbbrev: {
				id: '-1',
				typeId: '8355308043647703563',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMaxSimultaneity: {
				id: '-1',
				typeId: '2455059983007225756',
				gammaId: '-1',
				value: '',
			},
			interfaceMinSimultaneity: {
				id: '-1',
				typeId: '2455059983007225755',
				gammaId: '-1',
				value: '',
			},
			interfaceTaskFileType: {
				id: '-1',
				typeId: '2455059983007225760',
				gammaId: '-1',
				value: 0,
			},
			interfaceStructureCategory: {
				id: '-1',
				typeId: '2455059983007225764',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
			elements: [],
		};
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
