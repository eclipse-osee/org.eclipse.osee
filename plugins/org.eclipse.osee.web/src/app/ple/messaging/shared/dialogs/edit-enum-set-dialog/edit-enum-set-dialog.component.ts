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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
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
	createArtifact,
	modifyArtifact,
	modifyRelation,
} from '@osee/shared/types';
import { Observable, Subject } from 'rxjs';

@Component({
	selector: 'osee-messaging-edit-enum-set-dialog',
	templateUrl: './edit-enum-set-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
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
	enumObs: Observable<enumerationSet> = this.enumSetService.getEnumSet(
		this.data.id
	);
	isOnEditablePage = this.data.isOnEditablePage;
	inEditMode = this.preferenceService.inEditMode;

	receivedTx = new Subject<{
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}>();
	constructor(
		public dialogRef: MatDialogRef<EditEnumSetDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: enumsetDialogData,
		private enumSetService: EnumerationUIService,
		private preferenceService: PreferencesUIService
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}

	receiveTx(value: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		this.receivedTx.next(value);
	}
}
