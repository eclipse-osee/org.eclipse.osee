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
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
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
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
} from '@osee/shared/types';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	templateUrl: './platform-type-card.component.html',
	styles: [],
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
		private enumSetService: EnumerationUIService,
		private warningDialogService: WarningDialogService
	) {}

	/**
	 * Opens a Dialog for either editing or creating a new platform type based on the current platform type
	 * @param value Whether the dialog should be in edit or copy mode (see @enum {editPlatformTypeDialogDataMode})
	 */
	openDialog(value: editPlatformTypeDialogDataMode) {
		const copy = { ...this.typeData }; //clone the object so that edits aren't reflected in the page
		const dialogData: editPlatformTypeDialogData = {
			mode: value,
			type: copy,
		};
		const dialogRef = this.dialog.open(EditTypeDialogComponent, {
			data: dialogData,
			minWidth: '70%',
		});
		dialogRef
			.afterClosed()
			.pipe(
				filter((val) => val !== undefined),
				switchMap(({ manifest, mode }) =>
					this.warningDialogService
						.openPlatformTypeDialog(manifest)
						.pipe(
							switchMap((v) =>
								iif(
									() => mode === this.copy,
									this.typesService.copyType(manifest),
									this.getEditObservable(manifest)
								)
							)
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
	getEditObservable(manifest: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		return this.warningDialogService
			.openPlatformTypeDialog(manifest)
			.pipe(
				switchMap((body) => this.typesService.partialUpdate(manifest))
			);
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
					| {
							createArtifacts: createArtifact[];
							modifyArtifacts: modifyArtifact[];
							deleteRelations: modifyRelation[];
					  }
					| undefined,
					{
						createArtifacts: createArtifact[];
						modifyArtifacts: modifyArtifact[];
						deleteRelations: modifyRelation[];
					}
				>,
				take(1),
				switchMap((tx) =>
					iif(
						() => makeChanges,
						this.warningDialogService
							.openEnumsDialogs(
								tx.modifyArtifacts
									.slice(0, -1)
									.map((v) => v.id),
								[
									...tx.createArtifacts
										.flatMap((v) => v.relations)
										.filter(
											(v): v is relation =>
												v !== undefined
										)
										.map((v) => v.sideA)
										.filter(
											(v): v is string | string[] =>
												v !== undefined
										)
										.flatMap((v) => v),
									...tx.deleteRelations
										.flatMap((v) => v.aArtId)
										.filter(
											(v): v is string => v !== undefined
										),
								]
							)
							.pipe(
								switchMap((_) =>
									this.enumSetService.changeEnumSet(tx)
								)
							),
						of() // @todo replace with a false response
					)
				)
			)
			.subscribe();
	}
}
