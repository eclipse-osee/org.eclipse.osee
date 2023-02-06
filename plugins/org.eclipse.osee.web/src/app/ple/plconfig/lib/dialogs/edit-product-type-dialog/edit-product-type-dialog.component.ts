/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { productType } from '../../types/pl-config-product-types';

@Component({
	selector: 'osee-edit-product-type-dialog',
	templateUrl: './edit-product-type-dialog.component.html',
	styleUrls: ['./edit-product-type-dialog.component.sass'],
})
export class EditProductTypeDialogComponent {
	parentMatcher = new ParentErrorStateMatcher();
	constructor(
		public dialogRef: MatDialogRef<EditProductTypeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: Required<productType>
	) {}
	onNoClick(): void {
		this.dialogRef.close();
	}
}
