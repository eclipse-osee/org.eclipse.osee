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
import { Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
	EditEnumSetDialogComponent,
	EditTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/enumerations';
import {
	PlatformType,
	editPlatformTypeDialogData,
} from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
} from '@osee/shared/types';
import { OperatorFunction, iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { EnumerationUIService } from './enumeration-ui.service';
import { TypesUIService } from './types-ui.service';
import { WarningDialogService } from './warning-dialog.service';

@Injectable({
	providedIn: 'root',
})
export class PlatformTypeActionsService {
	private dialog = inject(MatDialog);
	private warningDialogService = inject(WarningDialogService);
	private typesService = inject(TypesUIService);
	private enumSetService = inject(EnumerationUIService);

	openCopyEditDialog(
		mode: editPlatformTypeDialogDataMode,
		typeData: PlatformType
	) {
		const copy = structuredClone(typeData); //clone the object so that edits aren't reflected in the page
		const dialogData: editPlatformTypeDialogData = {
			mode: mode,
			type: copy,
		};
		const dialogRef = this.dialog.open(EditTypeDialogComponent, {
			data: dialogData,
			minWidth: '70%',
		});
		return dialogRef.afterClosed().pipe(
			filter((val) => val !== undefined),
			switchMap(({ manifest, mode }) =>
				this.warningDialogService
					.openPlatformTypeDialogWithManifest(manifest)
					.pipe(
						switchMap((v) =>
							iif(
								() =>
									mode ===
									editPlatformTypeDialogDataMode.copy,
								this.typesService.copyType(manifest),
								this.getEditObservable(manifest)
							)
						)
					)
			)
		);
	}
	private getEditObservable(manifest: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		return this.warningDialogService
			.openPlatformTypeDialogWithManifest(manifest)
			.pipe(
				switchMap((body) => this.typesService.partialUpdate(manifest))
			);
	}

	openEnumDialog(id: string, editMode: boolean) {
		/**
		 * If create artifacts does not contain the enum set key(should be last or 2nd last object in modifiedArtifacts),
		 * Display a warning for the following:
		 * Each modified enum
		 * The modified enum set
		 * The modified platform type(s)
		 */
		return this.dialog
			.open(EditEnumSetDialogComponent, {
				data: {
					id: id,
					isOnEditablePage: editMode,
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
						() => editMode,
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
						of()
					)
				)
			);
	}
}
