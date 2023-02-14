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
import { Component, Inject, OnInit } from '@angular/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import type {
	enumerationSet,
	enumsetDialogData,
} from '@osee/messaging/shared/types';
import {
	EnumerationUIService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-messaging-edit-enum-set-dialog',
	templateUrl: './edit-enum-set-dialog.component.html',
	styleUrls: ['./edit-enum-set-dialog.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		MatDialogModule,
		MatButtonModule,
		EditEnumSetFieldComponent,
	],
})
export class EditEnumSetDialogComponent implements OnInit {
	enumObs: Observable<enumerationSet> = this.enumSetService.getEnumSet(
		this.data.id
	);
	private _enumUpdate: Subject<enumerationSet> = new Subject();

	isOnEditablePage = this.data.isOnEditablePage;
	inEditMode = this.preferenceService.inEditMode;

	changedEnum!: Observable<unknown>;
	constructor(
		public dialogRef: MatDialogRef<EditEnumSetDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: enumsetDialogData,
		private enumSetService: EnumerationUIService,
		private preferenceService: PreferencesUIService
	) {}

	ngOnInit(): void {
		this.changedEnum = combineLatest([this.enumObs, this._enumUpdate]).pipe(
			switchMap(([previousEnum, currentEnum]) =>
				of([previousEnum, currentEnum]).pipe(
					map(([previousEnum, currentEnum]) => {
						//find changes in currentEnum, remove existing contents of previousEnum
						//this will end up being the old output

						return {
							id: currentEnum.id,
							name: currentEnum.name,
							applicability: currentEnum.applicability,
							applicabilityId: currentEnum.applicability.id,
							description: currentEnum.description,
							enumerations: currentEnum.enumerations,
						};
					})
				)
			)
		);
	}
	onNoClick(): void {
		this.dialogRef.close();
	}

	enumUpdate(value: enumerationSet | undefined) {
		if (value) {
			this._enumUpdate.next(value);
		}
	}
}
