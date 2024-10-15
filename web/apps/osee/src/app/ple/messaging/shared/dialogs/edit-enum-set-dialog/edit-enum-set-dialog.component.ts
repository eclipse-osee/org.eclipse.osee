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
import { Component, computed, inject, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import type { enumsetDialogData } from '@osee/messaging/shared/types';
import { writableSlice } from '@osee/shared/utils';

@Component({
	selector: 'osee-messaging-edit-enum-set-dialog',
	templateUrl: './edit-enum-set-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		EditEnumSetFieldComponent,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
})
export class EditEnumSetDialogComponent {
	dialogRef = inject<MatDialogRef<EditEnumSetDialogComponent>>(MatDialogRef);
	data = signal(inject<enumsetDialogData>(MAT_DIALOG_DATA));
	private preferenceService = inject(PreferencesUIService);

	protected platformType = writableSlice(this.data, 'platformType');

	protected bitSize = computed(
		() => this.platformType().interfacePlatformTypeBitSize
	);
	protected enumSet = writableSlice(this.platformType, 'enumSet');
	protected isOnEditablePage = writableSlice(this.data, 'isOnEditablePage');

	protected inEditMode = this.preferenceService.inEditMode;

	onNoClick(): void {
		this.dialogRef.close();
	}
}
