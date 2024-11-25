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
import {
	Component,
	computed,
	effect,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
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
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { writableSlice } from '@osee/shared/utils';
import {
	BehaviorSubject,
	debounceTime,
	delay,
	distinct,
	map,
	switchMap,
	tap,
} from 'rxjs';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector: 'osee-messaging-add-sub-message-dialog',
	templateUrl: './add-sub-message-dialog.component.html',
	imports: [
		AsyncPipe,
		FormsModule,
		ApplicabilityDropdownComponent,
		MatOptionLoadingComponent,
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatButton,
		MatStepperNext,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatDialogActions,
		CdkTextareaAutosize,
		MatStepperPrevious,
		MatDialogClose,
		MatTooltip,
	],
})
export class AddSubMessageDialogComponent {
	dialogRef =
		inject<MatDialogRef<AddSubMessageDialogComponent>>(MatDialogRef);
	private messageService = inject(CurrentMessagesService);

	_internalStepper = viewChild.required(MatStepper);
	__internalStepper = toObservable(this._internalStepper);

	_firstStepFilled = new BehaviorSubject<boolean>(true);
	protected data = signal(inject<AddSubMessageDialog>(MAT_DIALOG_DATA));
	protected messageName = computed(() => this.data().name);
	protected subMessage = writableSlice(this.data, 'subMessage');
	protected subMessageId = writableSlice(this.subMessage, 'id');
	protected subMessageNameAttr = writableSlice(this.subMessage, 'name');
	protected subMessageName = writableSlice(this.subMessageNameAttr, 'value');
	protected subMessageDescriptionAttr = writableSlice(
		this.subMessage,
		'description'
	);
	protected subMessageDescription = writableSlice(
		this.subMessageDescriptionAttr,
		'value'
	);
	protected subMessageNumberAttr = writableSlice(
		this.subMessage,
		'interfaceSubMessageNumber'
	);
	protected subMessageNumber = writableSlice(
		this.subMessageNumberAttr,
		'value'
	);
	protected subMessageApplicability = writableSlice(
		this.subMessage,
		'applicability'
	);
	protected subMessageFilter = signal('');
	private _updateFilterBasedOnSubMessageSelection = effect(
		() => {
			if (this.subMessageId() !== '-1') {
				this.subMessageFilter.set(this.subMessageName());
			}
		},
		{ allowSignalWrites: true }
	);
	private _moveToNextStep = this.__internalStepper.pipe(
		debounceTime(1),
		delay(1),
		distinct(),
		tap((stepper) => {
			if (
				stepper &&
				this.subMessageId() !== undefined &&
				this.subMessageId() !== '-1'
			) {
				stepper.next();
				this._firstStepFilled.next(false);
			}
		}),
		takeUntilDestroyed()
	);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		this._moveToNextStep.subscribe();
	}

	private _submessageSearch = toObservable(this.subMessageFilter);
	paginationSize = 50;

	availableSubMessages = this._submessageSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: string | number) =>
				this.messageService.getPaginatedSubmessagesByName(
					search,
					this.paginationSize,
					pageNum
				)
		)
	);

	availableSubMessagesCount = this._submessageSearch.pipe(
		debounceTime(250),
		switchMap((search) =>
			this.messageService.getSubmessagesByNameCount(search)
		)
	);

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	createNew() {
		this.subMessageId.set('-1');
		this.subMessage.set({
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceSubMessageNumber: {
				id: '-1',
				typeId: '2455059983007225769',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
		});
	}

	moveToReview(stepper: MatStepper) {
		this.moveToStep(3, stepper);
	}
}
