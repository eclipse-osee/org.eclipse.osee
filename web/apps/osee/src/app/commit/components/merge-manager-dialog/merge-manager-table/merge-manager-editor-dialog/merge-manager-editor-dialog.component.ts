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
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { mergeData } from '@osee/commit/types';
import { AttributesEditorComponent } from '@osee/shared/components';
import { attribute } from '@osee/shared/types';
import { BehaviorSubject } from 'rxjs';

@Component({
	selector: 'osee-merge-manager-editor-dialog',
	imports: [
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		AttributesEditorComponent,
	],
	templateUrl: './merge-manager-editor-dialog.component.html',
})
export class MergeManagerEditorDialogComponent implements OnInit {
	dialogRef =
		inject<MatDialogRef<MergeManagerEditorDialogComponent>>(MatDialogRef);
	data = inject<mergeData>(MAT_DIALOG_DATA);

	attributes = new BehaviorSubject<attribute[]>([]);

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
