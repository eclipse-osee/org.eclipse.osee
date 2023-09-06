/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Inject, Injectable } from '@angular/core';
import {
	CurrentStructureService,
	EnumerationUIService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { RemoveElementDialogData } from '../dialogs/remove-element-dialog/remove-element-dialog';
import { RemoveElementDialogComponent } from '../dialogs/remove-element-dialog/remove-element-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import {
	EditViewFreeTextDialog,
	ElementDialog,
	element,
	elementWithChanges,
	structure,
} from '@osee/messaging/shared/types';
import {
	OperatorFunction,
	combineLatest,
	filter,
	iif,
	map,
	of,
	switchMap,
	take,
} from 'rxjs';
import { EditEnumSetDialogComponent } from '@osee/messaging/shared/dialogs';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
} from '@osee/shared/types';
import { EditViewFreeTextFieldDialogComponent } from '@osee/messaging/shared/dialogs/free-text';
import { difference } from '@osee/shared/types/change-report';
import { applic } from '@osee/shared/types/applicability';
import { DefaultAddElementDialog } from '../dialogs/add-element-dialog/add-element-dialog.default';
import { AddElementDialogComponent } from '../dialogs/add-element-dialog/add-element-dialog.component';
import { UiService } from '@osee/shared/services';
import { Router } from '@angular/router';
import { EditElementDialogComponent } from '../dialogs/edit-element-dialog/edit-element-dialog.component';

@Injectable({
	providedIn: 'any',
})
export class ElementTableDropdownService {
	constructor(
		private _ui: UiService,
		private router: Router,
		private dialog: MatDialog,
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private warningDialogService: WarningDialogService,
		private enumSetService: EnumerationUIService
	) {}

	openAddElementDialog(
		parent: structure | element,
		isArray: boolean,
		allowArray: boolean,
		afterElement?: string,
		copyElement?: element
	) {
		const dialogData = new DefaultAddElementDialog(
			parent.id || '',
			parent.name || '',
			structuredClone(copyElement), //make a copy
			'add',
			allowArray
		);
		let dialogRef = this.dialog.open(AddElementDialogComponent, {
			data: dialogData,
		});
		let createElement = dialogRef.afterClosed().pipe(
			take(1),
			filter(
				(data) =>
					data !== undefined &&
					data !== null &&
					data?.element !== undefined
			),
			switchMap((data: ElementDialog) =>
				iif(
					() =>
						data.element.id !== undefined &&
						data.element.id !== '-1' &&
						data.element.id.length > 0,
					iif(
						() => isArray,
						this.structureService.relateArrayElement(
							parent.id,
							data.element.id !== undefined
								? data.element.id
								: '-1',
							afterElement || 'end'
						),
						this.structureService.relateElement(
							parent.id,
							data.element.id !== undefined
								? data.element.id
								: '-1',
							afterElement || 'end'
						)
					),
					iif(
						() => isArray,
						this.structureService.createNewArrayElement(
							data.element,
							parent.id,
							data.type.id as string,
							afterElement || 'end'
						),
						this.structureService.createNewElement(
							data.element,
							parent.id,
							data.type.id as string,
							afterElement || 'end'
						)
					)
				)
			)
		);
		createElement.subscribe();
	}
	openDeleteElementDialog(
		element: element,
		removeType: 'Structure' | 'Array'
	) {
		//open dialog, yes/no if yes -> this.structures.deleteElement()
		const dialogData: RemoveElementDialogData = {
			removeType: removeType,
			elementName: element.name,
		};
		this.dialog
			.open(RemoveElementDialogComponent, {
				data: dialogData,
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.structureService.deleteElement(element),
						of()
					)
				)
			)
			.subscribe();
	}

	openEditElementDialog(element: element) {
		const dialogData: ElementDialog = {
			id: '',
			name: '',
			element: structuredClone(element),
			type: element.platformType,
			mode: 'edit',
			allowArray: false,
		};
		let dialogRef = this.dialog.open(EditElementDialogComponent, {
			data: dialogData,
		});
		dialogRef
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(val) =>
						val !== undefined &&
						val !== null &&
						val.element !== undefined &&
						val.type !== undefined
				),
				switchMap((val) =>
					this.structureService.changeElementFromDialog(val)
				)
			)
			.subscribe();
	}

	openEnumDialog(id: string, editMode: boolean) {
		/**
		 * If create artifacts does not contain the enum set key(should be last or 2nd last object in modifiedArtifacts),
		 * Display a warning for the following:
		 * Each modified enum
		 * The modified enum set
		 * The modified platform type(s)
		 */
		this.dialog
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
			)
			.subscribe();
	}

	openDescriptionDialog(description: string, elementId: string) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(description)) as string,
					type: 'Description',
					return: description,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((response: EditViewFreeTextDialog | string) =>
					iif(
						() =>
							response === 'ok' ||
							response === 'cancel' ||
							response === undefined,
						//do nothing
						of(),
						//change description
						this.structureService.partialUpdateElement({
							id: elementId,
							description: (response as EditViewFreeTextDialog)
								.return,
						})
					)
				)
			)
			.subscribe();
	}

	/**
	 * Need to verify if type is required
	 */
	openEnumLiteralDialog(enumLiteral: string, elementId: string) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(enumLiteral)) as string,
					type: 'Enum Literal',
					return: enumLiteral,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((response: EditViewFreeTextDialog | string) =>
					iif(
						() =>
							response === 'ok' ||
							response === 'cancel' ||
							response === undefined,
						//do nothing
						of(),
						//change description
						this.structureService.partialUpdateElement({
							id: elementId,
							enumLiteral: (response as EditViewFreeTextDialog)
								.return,
						})
					)
				)
			)
			.subscribe();
	}

	openNotesDialog(notes: string, elementId: string) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(notes)) as string,
					type: 'Notes',
					return: notes,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((response: EditViewFreeTextDialog | string) =>
					iif(
						() =>
							response === 'ok' ||
							response === 'cancel' ||
							response === undefined,
						//do nothing
						of(),
						//change notes
						this.structureService.partialUpdateElement({
							id: elementId,
							notes: (response as EditViewFreeTextDialog).return,
						})
					)
				)
			)
			.subscribe();
	}

	viewDiff<T>(value: difference<T> | undefined, header: string) {
		if (value !== undefined) {
			this.structureService.sideNav = {
				opened: true,
				field: header,
				currentValue: value.currentValue as string | number | applic,
				previousValue: value.previousValue as
					| string
					| number
					| applic
					| undefined,
				transaction: value.transactionToken,
			};
		}
	}

	hasChanges(v: element | elementWithChanges): v is elementWithChanges {
		return (
			(v as any).changes !== undefined ||
			(v as any).added !== undefined ||
			(v as any).deleted !== undefined
		);
	}
}
