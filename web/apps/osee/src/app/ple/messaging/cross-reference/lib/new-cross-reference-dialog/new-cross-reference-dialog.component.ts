/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import {
	Component,
	computed,
	inject,
	linkedSignal,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton, MatMiniFabButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import type {
	CrossRefKeyValue,
	CrossReference,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-cross-reference-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatLabel,
		MatInput,
		MatButton,
		MatMiniFabButton,
		MatIcon,
		ApplicabilityDropdownComponent,
	],
	templateUrl: './new-cross-reference-dialog.component.html',
})
export class NewCrossReferenceDialogComponent {
	data = inject<{ crossRef?: CrossReference } | undefined>(MAT_DIALOG_DATA);
	dialogRef =
		inject<MatDialogRef<NewCrossReferenceDialogComponent>>(MatDialogRef);
	inputCrossRef = signal(
		this.data?.crossRef ||
			({
				name: '',
				crossReferenceValue: '',
				crossReferenceArrayValues: '',
				crossReferenceAdditionalContent: '',
				applicability: {
					id: '1',
					name: 'Base',
				},
			} as CrossReference)
	);
	crossRefName = linkedSignal(() => this.inputCrossRef().name);
	crossRefValue = linkedSignal(
		() => this.inputCrossRef().crossReferenceValue
	);
	crossRefAdditionalContent = linkedSignal(
		() => this.inputCrossRef().crossReferenceAdditionalContent
	);
	crossRefApplicability = linkedSignal(
		() => this.inputCrossRef().applicability
	);
	crossRefArray = linkedSignal(() =>
		this.inputCrossRef()
			.crossReferenceArrayValues.split(';')
			.map((x) => {
				return {
					key: x.split('=')[0],
					value: x.split('=')[1],
				} as CrossRefKeyValue;
			})
	);

	onNoClick(): void {
		this.dialogRef.close();
	}
	crossRefResults = computed<CrossReference>(() => {
		return {
			name: this.crossRefName(),
			crossReferenceValue: this.crossRefValue(),
			crossReferenceAdditionalContent: this.crossRefAdditionalContent(),
			applicability: this.crossRefApplicability(),
			crossReferenceArrayValues: this.crossRefArray()
				.map((v) => v.key + '=' + v.value)
				.join(';'),
		};
	});
	updateKey(key: string, index: number) {
		const array = this.crossRefArray();
		if (index < 0 || index > array.length) {
			return;
		}
		array[index].key = key;
		this.crossRefArray.set(array);
	}
	updateValue(value: string, index: number) {
		const array = this.crossRefArray();
		if (index < 0 || index > array.length) {
			return;
		}
		array[index].value = value;
		this.crossRefArray.set(array);
	}

	addArrayValue() {
		const array = this.crossRefArray();
		array.push({ key: '', value: '' });
		this.crossRefArray.set(array);
	}
}
