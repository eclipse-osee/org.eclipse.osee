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
import { Component, Input } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { iif, of, OperatorFunction } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import type {
	PlatformType,
	editPlatformTypeDialogData,
	enumerationSet,
} from '@osee/messaging/shared/types';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/enumerations';
import {
	EditEnumSetDialogComponent,
	EditTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import {
	TypesUIService,
	PreferencesUIService,
	EnumerationUIService,
} from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	templateUrl: './platform-type-card.component.html',
	styleUrls: ['./platform-type-card.component.sass'],
	standalone: true,
	imports: [
		MatCardModule,
		NgIf,
		AsyncPipe,
		MatDialogModule,
		MatIconModule,
		MatButtonModule,
	],
})
export class PlatformTypeCardComponent {
	@Input() typeData!: PlatformType;
	edit: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.edit;
	copy: editPlatformTypeDialogDataMode = editPlatformTypeDialogDataMode.copy;
	inEditMode = this.preferenceService.inEditMode;
	constructor(
		public dialog: MatDialog,
		private typesService: TypesUIService,
		private preferenceService: PreferencesUIService,
		private enumSetService: EnumerationUIService
	) {}

	/**
	 * Opens a Dialog for either editing or creating a new platform type based on the current platform type
	 * @param value Whether the dialog should be in edit or copy mode (see @enum {editPlatformTypeDialogDataMode})
	 */
	openDialog(value: editPlatformTypeDialogDataMode) {
		let dialogData: editPlatformTypeDialogData = {
			mode: value,
			type: this.typeData,
		};
		const copy = JSON.parse(JSON.stringify(this.typeData)); //can't remember what this line does
		const dialogRef = this.dialog.open(EditTypeDialogComponent, {
			data: dialogData,
			minWidth: '70%',
		});
		dialogRef
			.afterClosed()
			.pipe(
				filter((val) => val !== undefined),
				switchMap(({ mode, type }) =>
					iif(
						() => mode === this.copy,
						this.typesService.copyType(type),
						this.getEditObservable(copy, { mode, type })
					)
				)
			)
			.subscribe();
	}

	/**
	 * already shared
	 * Gets an observable for updating the attributes of a platform type
	 * @param copy Initial values of the platform type PRIOR to the dialog being opened
	 * @param result Changed values of the platform type + mode AFTER the dialog is closed
	 * @returns @type {Observable<OSEEWriteApiResponse>} observable containing results (see @type {OSEEWriteApiResponse} and @type {Observable})
	 */
	getEditObservable(copy: PlatformType, result: editPlatformTypeDialogData) {
		let newType: any = new Object();
		Object.keys(copy).forEach((value) => {
			if (
				copy[value as keyof PlatformType] !==
				result.type[value as keyof PlatformType]
			) {
				newType[value as keyof PlatformType] =
					result.type[value as keyof PlatformType];
			}
		});
		newType['id'] = copy['id'];

		return this.typesService.partialUpdate(newType);
	}

	/**
	 * already shared
	 * @param makeChanges
	 */
	openEnumDialog(makeChanges: boolean) {
		this.dialog
			.open(EditEnumSetDialogComponent, {
				data: {
					id: this.typeData.id || '',
					isOnEditablePage: makeChanges,
				},
			})
			.afterClosed()
			.pipe(
				filter((x) => x !== undefined) as OperatorFunction<
					enumerationSet | undefined,
					enumerationSet
				>,
				take(1),
				switchMap(({ enumerations, ...changes }) =>
					iif(
						() => makeChanges,
						this.enumSetService.changeEnumSet(
							changes,
							enumerations
						),
						of() // @todo replace with a false response
					)
				)
			)
			.subscribe();
	}
}
