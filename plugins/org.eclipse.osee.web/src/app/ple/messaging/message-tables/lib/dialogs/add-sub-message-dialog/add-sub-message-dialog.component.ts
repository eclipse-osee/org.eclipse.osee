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
import { Component, Inject, viewChild } from '@angular/core';
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
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import type { subMessage } from '@osee/messaging/shared/types';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
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

@Component({
	selector: 'osee-messaging-add-sub-message-dialog',
	templateUrl: './add-sub-message-dialog.component.html',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		ApplicabilitySelectorComponent,
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
				this.data.subMessage.id !== undefined &&
				this.data.subMessage.id !== '-1'
			) {
				stepper.next();
				this._firstStepFilled.next(false);
			}
		}),
		takeUntilDestroyed()
	);
	constructor(
		public dialogRef: MatDialogRef<AddSubMessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddSubMessageDialog,
		private messageService: CurrentMessagesService
	) {
		this._moveToNextStep.subscribe();
	}

	selectedSubmessage: subMessage | undefined;
	submessageSearch = new BehaviorSubject<string>('');
	paginationSize = 50;

	availableSubMessages = this.submessageSearch.pipe(
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

	availableSubMessagesCount = this.submessageSearch.pipe(
		debounceTime(250),
		switchMap((search) =>
			this.messageService.getSubmessagesByNameCount(search)
		)
	);

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	createNew() {
		this.data.subMessage.id = '-1';
		this.selectedSubmessage = undefined;
	}

	moveToReview(stepper: MatStepper) {
		if (this.selectedSubmessage) {
			this.data.subMessage = this.selectedSubmessage;
		}
		this.moveToStep(3, stepper);
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.submessageSearch.next(value);
	}

	selectExistingSubmessage(structure: subMessage) {
		this.selectedSubmessage = structure;
	}
}
