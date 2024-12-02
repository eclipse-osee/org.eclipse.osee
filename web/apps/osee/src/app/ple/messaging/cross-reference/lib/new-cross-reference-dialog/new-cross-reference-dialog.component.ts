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
import { Component, inject } from '@angular/core';
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
	data = inject<{ crossRef: CrossReference }>(MAT_DIALOG_DATA);
	dialogRef =
		inject<MatDialogRef<NewCrossReferenceDialogComponent>>(MatDialogRef);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		if (this.data && this.data.crossRef) {
			this.crossReference.id = this.data.crossRef.id;
			this.crossReference.name = this.data.crossRef.name;
			this.crossReference.crossReferenceValue =
				this.data.crossRef.crossReferenceValue;
			this.crossReference.crossReferenceArrayValues =
				this.data.crossRef.crossReferenceArrayValues;

			this.data.crossRef.crossReferenceArrayValues
				.split(';')
				.forEach((a) => {
					this.arrayValues.push({
						key: a.split('=')[0],
						value: a.split('=')[1],
					});
				});
		}
	}

	crossReference: CrossReference = {
		name: '',
		crossReferenceValue: '',
		crossReferenceArrayValues: '',
		crossReferenceAdditionalContent: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	};

	arrayValues: CrossRefKeyValue[] = [];

	onNoClick(): void {
		this.dialogRef.close();
	}

	updateArrayString() {
		this.crossReference.crossReferenceArrayValues = this.arrayValues
			.map((v) => v.key + '=' + v.value)
			.join(';');
	}

	addArrayValue() {
		this.arrayValues.push({ key: '', value: '' });
	}
}
