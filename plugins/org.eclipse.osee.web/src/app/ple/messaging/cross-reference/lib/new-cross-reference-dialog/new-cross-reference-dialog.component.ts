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
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CrossReference, CrossRefKeyValue } from '@osee/messaging/shared';

@Component({
	selector: 'osee-new-cross-reference-dialog',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		MatButtonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
	],
	templateUrl: './new-cross-reference-dialog.component.html',
	styleUrls: ['./new-cross-reference-dialog.component.scss'],
})
export class NewCrossReferenceDialogComponent {
	constructor(
		@Inject(MAT_DIALOG_DATA) public data: { crossRef: CrossReference },
		public dialogRef: MatDialogRef<NewCrossReferenceDialogComponent>
	) {
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
