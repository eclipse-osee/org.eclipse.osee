/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CdkTrapFocus } from '@angular/cdk/a11y';
import { AsyncPipe } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { attribute, mergeData } from '@osee/shared/types';
import { BehaviorSubject } from 'rxjs';
import { AttributesEditorComponent } from '../../../attributes-editor/attributes-editor.component';

@Component({
	selector: 'osee-merge-manager-editor-dialog',
	standalone: true,
	imports: [
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		CdkTrapFocus,
		MatDialogClose,
		AttributesEditorComponent,
	],
	templateUrl: './merge-manager-editor-dialog.component.html',
})
export class MergeManagerEditorDialogComponent implements OnInit {
	attributes = new BehaviorSubject<attribute[]>([]);

	constructor(
		public dialogRef: MatDialogRef<MergeManagerEditorDialogComponent>,
		@Inject(MAT_DIALOG_DATA)
		public data: mergeData
	) {}

	ngOnInit(): void {
		const attr: attribute = {
			id: '-1',
			multiplicityId: '-1',
			name: this.data.attrMergeData.attrTypeName,
			storeType: this.data.attrMergeData.storeType,
			typeId: this.data.attrMergeData.attrType,
			value: this.data.attrMergeData.mergeValue,
		};
		this.attributes.next([attr]);
	}

	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		if (updatedAttributes.length === 0) {
			return;
		}
		this.data.attrMergeData.mergeValue = updatedAttributes[0].value;
	}
}
