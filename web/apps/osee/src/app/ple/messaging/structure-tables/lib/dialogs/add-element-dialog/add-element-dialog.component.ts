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
import {
	Component,
	computed,
	effect,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
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
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { MatTooltip } from '@angular/material/tooltip';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { ElementDialog, element } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { writableSlice } from '@osee/shared/utils';
import { debounceTime, delay, map, switchMap, tap } from 'rxjs/operators';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';

@Component({
	selector: 'osee-messaging-add-element-dialog',
	templateUrl: './add-element-dialog.component.html',
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
		AsyncPipe,
		MatOptionLoadingComponent,
		ElementFormComponent,
		AttributeToValuePipe,
	],
	providers: [
		{
			provide: STRUCTURE_SERVICE_TOKEN,
			useExisting: CurrentStructureService,
		},
	],
})
export class AddElementDialogComponent {
	dialog = inject(MatDialog);
	private structures = inject(STRUCTURE_SERVICE_TOKEN);
	dialogRef = inject<MatDialogRef<AddElementDialogComponent>>(MatDialogRef);

	_internalStepper = viewChild.required(MatStepper);
	__internalStepper = toObservable(this._internalStepper);

	private _moveToNextStep = this.__internalStepper.pipe(
		debounceTime(1),
		delay(1),
		tap((v) => {
			if (
				this.elementId() !== '-1' &&
				this._isFullElement(this.data().element)
			) {
				this.elementId.set('-1');
				if (this.element()) {
					this.elementId.set('-1');
				}
				this.type.set(this.data().element.platformType);
				v.next();
			}
		})
	);

	protected data = signal(inject<ElementDialog>(MAT_DIALOG_DATA));

	protected name = computed(() => this.data().name);
	protected platformType = computed(
		() => this.data().element.platformType.name.value
	);
	protected element = writableSlice(this.data, 'element');
	protected type = writableSlice(this.data, 'type');
	protected elementId = writableSlice(this.element, 'id');
	protected elementNameAttr = writableSlice(this.element, 'name');
	protected elementName = writableSlice(this.elementNameAttr, 'value');

	paginationSize = 10;

	protected elementSearchString = signal('');

	private _updateElementSearchOnSelection = effect(
		() => {
			this.elementSearchString.set(this.elementName());
		},
		{ allowSignalWrites: true }
	);
	elementSearch = toObservable(this.elementSearchString);
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

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		this._moveToNextStep.subscribe();
	}

	private _isFullElement(
		value: Partial<element> | element | Required<element>
	): value is element {
		return value?.id !== undefined;
	}

	selectElement(event: MatAutocompleteSelectedEvent) {
		this.element.set(event.option.value);
	}

	createNew() {
		this.elementId.set('-1');
	}

	getElementOptionToolTip(element: element) {
		let tooltip = '';
		if (element.platformType.interfaceLogicalType) {
			tooltip += element.platformType.interfaceLogicalType;
		}
		if (
			element.interfaceElementIndexStart.value !==
			element.interfaceElementIndexEnd.value
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
		this.moveToStep(3, stepper);
	}

	resetId() {
		this.elementId.set('-1');
	}
}
