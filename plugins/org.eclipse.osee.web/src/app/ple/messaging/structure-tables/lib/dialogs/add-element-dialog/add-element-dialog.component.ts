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
import { Component, Inject, ViewChild } from '@angular/core';
import {
	MatDialog,
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { BehaviorSubject, Subject } from 'rxjs';
import { debounceTime, delay, map, switchMap, tap } from 'rxjs/operators';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PlatformTypeQueryComponent } from '../platform-type-query/platform-type-query.component';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MimQuery } from '@osee/messaging/shared/query';
import type {
	element,
	PlatformType,
	ElementDialog,
} from '@osee/messaging/shared/types';
import {
	MatOptionLoadingComponent,
	ApplicabilitySelectorComponent,
} from '@osee/shared/components';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';

@Component({
	selector: 'osee-messaging-add-element-dialog',
	templateUrl: './add-element-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogModule,
		MatStepperModule,
		MatButtonModule,
		FormsModule,
		MatFormFieldModule,
		MatOptionLoadingComponent,
		MatInputModule,
		MatSlideToggleModule,
		MatIconModule,
		MatDividerModule,
		MatProgressSpinnerModule,
		PlatformTypeQueryComponent,
		NewTypeFormComponent,
		MatSelectModule,
		MatOptionModule,
		MatTooltipModule,
		MatAutocompleteModule,
		AsyncPipe,
		NgIf,
		NgFor,
		ApplicabilitySelectorComponent,
		ElementFormComponent,
	],
})
export class AddElementDialogComponent {
	@ViewChild(MatStepper) set _internalStepper(stepper: MatStepper) {
		this.__internalStepper.next(stepper);
	}
	__internalStepper = new Subject<MatStepper>();

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
			tooltip += element.logicalType + '\n\n';
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
