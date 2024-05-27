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
import { Component, inject } from '@angular/core';
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
import {
	EnumerationUIService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type {
	enumerationSet,
	enumsetDialogData,
} from '@osee/messaging/shared/types';
import {
	legacyCreateArtifact,
	legacyModifyArtifact,
	legacyModifyRelation,
} from '@osee/transactions/types';
import { Observable, Subject } from 'rxjs';

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
	data = inject<enumsetDialogData>(MAT_DIALOG_DATA);
	private enumSetService = inject(EnumerationUIService);
	private preferenceService = inject(PreferencesUIService);

	enumObs: Observable<enumerationSet> = this.enumSetService.getEnumSet(
		this.data.id
	);
	isOnEditablePage = this.data.isOnEditablePage;
	inEditMode = this.preferenceService.inEditMode;

	receivedTx = new Subject<{
		createArtifacts: legacyCreateArtifact[];
		modifyArtifacts: legacyModifyArtifact[];
		deleteRelations: legacyModifyRelation[];
	}>();

	onNoClick(): void {
		this.dialogRef.close();
	}

	receiveTx(value: {
		createArtifacts: legacyCreateArtifact[];
		modifyArtifacts: legacyModifyArtifact[];
		deleteRelations: legacyModifyRelation[];
	}) {
		this.receivedTx.next(value);
	}
}
