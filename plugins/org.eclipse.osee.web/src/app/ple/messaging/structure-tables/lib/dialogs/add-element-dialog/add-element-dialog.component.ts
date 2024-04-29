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
import { AsyncPipe } from '@angular/common';
import { Component, Inject, viewChild } from '@angular/core';
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
	MatDialog,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { MatTooltip } from '@angular/material/tooltip';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { ElementDialog, element } from '@osee/messaging/shared/types';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import { BehaviorSubject, Subject } from 'rxjs';
import { debounceTime, delay, map, switchMap, tap } from 'rxjs/operators';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';
import { PlatformTypeQueryComponent } from '../platform-type-query/platform-type-query.component';

@Component({
	selector: 'osee-messaging-add-element-dialog',
	templateUrl: './add-element-dialog.component.html',
	standalone: true,
	imports: [
		FormsModule,
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
		MatStepperPrevious,
		MatDialogClose,
		MatProgressSpinner,
		AsyncPipe,
		PlatformTypeQueryComponent,
		NewTypeFormComponent,
		MatOptionLoadingComponent,
		ApplicabilitySelectorComponent,
		ElementFormComponent,
	],
	providers: [
		{
			provide: STRUCTURE_SERVICE_TOKEN,
			useExisting: CurrentStructureService,
		},
	],
})
export class AddElementDialogComponent {
	_internalStepper = viewChild.required(MatStepper);
	__internalStepper = toObservable(this._internalStepper);

	private _moveToNextStep = this.__internalStepper.pipe(
		debounceTime(1),
		delay(1),
		tap((v) => {
			if (
				this.data.element.id !== '' &&
				this.data.element.id !== '-1' &&
				this._isFullElement(this.data.element)
			) {
				this.data.element.id = '-1';
				if (this.selectedElement) {
					this.selectedElement.id = '-1';
					this._cleanElement(this.selectedElement);
				}
				this._cleanElement(this.data.element);
				this.data.type = this.data.element.platformType;
				v.next();
			}
		})
	);
	protected resetElementForm = new Subject<number>();
	paginationSize = 10;
	elementSearch = new BehaviorSubject<string>('');
	selectedElement: element | undefined = undefined;

	availableElements = this.elementSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: number | string) =>
				this.structures.getPaginatedElementsByName(
					search,
					this.paginationSize,
					pageNum
				)
		)
	);

	availableElementsCount = this.elementSearch.pipe(
		debounceTime(250),
		switchMap((search) => this.structures.getElementsByNameCount(search))
	);

	constructor(
		public dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structures: CurrentStructureService,
		public dialogRef: MatDialogRef<AddElementDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: ElementDialog
	) {
		if (
			this.data.element.id !== '' &&
			this.data.element.id !== '-1' &&
			this._isFullElement(this.data.element)
		) {
			this.selectExistingElement(this.data.element);
		}
		this._moveToNextStep.subscribe();
	}

	private _isFullElement(
		value: Partial<element> | element | Required<element>
	): value is element {
		return value?.id !== undefined;
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.elementSearch.next(value);
	}

	createNew() {
		this.data.element.id = '-1';
		this.selectedElement = undefined;
	}
	selectExistingElement(element: element) {
		this.selectedElement = element;
	}

	getElementOptionToolTip(element: element) {
		let tooltip = '';
		if (element.logicalType) {
			tooltip += element.logicalType;
		}
		if (
			element.interfaceElementIndexStart !==
			element.interfaceElementIndexEnd
		) {
			tooltip +=
				' [' +
				element.interfaceElementIndexStart +
				'...' +
				element.interfaceElementIndexEnd +
				']';
		}
		if (tooltip !== '') {
			tooltip += '\n\n';
		}
		tooltip += element.description;
		return tooltip;
	}

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}
	moveToReview(stepper: MatStepper) {
		if (this.selectedElement) {
			this.data.element = this.selectedElement;
		}
		this.moveToStep(3, stepper);
	}
	resetDialog() {
		this.resetElementForm.next(Math.random());
	}

	private _cleanElement(_element: element) {
		delete _element.beginByte;
		delete _element.beginWord;
		delete _element.endByte;
		delete _element.endWord;
		delete _element.autogenerated;
		delete _element.logicalType;
		delete _element.interfacePlatformTypeDescription;
		delete _element.units;
		delete _element.interfacePlatformTypeMaxval;
		delete _element.interfacePlatformTypeMinval;
		delete _element.elementSizeInBits;
		delete _element.elementSizeInBytes;
	}
}
