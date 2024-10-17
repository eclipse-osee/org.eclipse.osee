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
	enumerationSet,
} from '@osee/messaging/shared/types';
import { OperatorFunction, of } from 'rxjs';
import { filter, map, switchMap, take } from 'rxjs/operators';
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
		const copiedPlatformType = structuredClone(typeData); //clone the object so that edits aren't reflected in the page
		if (mode === editPlatformTypeDialogDataMode.copy) {
			//set the copy to -1 everywhere
			copiedPlatformType.id = '-1';
			copiedPlatformType.gammaId = '-1';
			copiedPlatformType.description.id = '-1';
			copiedPlatformType.interfaceDefaultValue.id = '-1';
			copiedPlatformType.interfaceLogicalType.id = '-1';
			copiedPlatformType.interfacePlatformType2sComplement.id = '-1';
			copiedPlatformType.interfacePlatformTypeAnalogAccuracy.id = '-1';
			copiedPlatformType.interfacePlatformTypeBitSize.id = '-1';
			copiedPlatformType.interfacePlatformTypeBitsResolution.id = '-1';
			copiedPlatformType.interfacePlatformTypeCompRate.id = '-1';
			copiedPlatformType.interfacePlatformTypeMaxval.id = '-1';
			copiedPlatformType.interfacePlatformTypeMinval.id = '-1';
			copiedPlatformType.interfacePlatformTypeMsbValue.id = '-1';
			copiedPlatformType.interfacePlatformTypeUnits.id = '-1';
			copiedPlatformType.interfacePlatformTypeValidRangeDescription.id =
				'-1';
			copiedPlatformType.name.id = '-1';
			copiedPlatformType.enumSet.id = '-1';
			copiedPlatformType.enumSet.gammaId = '-1';
			copiedPlatformType.enumSet.name.id = '-1';
			copiedPlatformType.enumSet.description.id = '-1';
			copiedPlatformType.enumSet.enumerations.forEach((e) => {
				e.id = '-1';
				e.name.id = '-1';
				e.ordinal.id = '-1';
				e.ordinalType.id = '-1';
			});
		}
		const dialogData: editPlatformTypeDialogData = {
			mode: mode,
			type: copiedPlatformType,
		};
		const dialogRef = this.dialog.open(EditTypeDialogComponent, {
			data: dialogData,
			minWidth: '70vw',
		});
		return dialogRef.afterClosed().pipe(
			filter((val) => val !== undefined),
			switchMap((newType) =>
				mode === editPlatformTypeDialogDataMode.copy
					? this.typesService.copyType(newType)
					: this.getEditObservable(newType, typeData)
			)
		);
	}
	private getEditObservable(current: PlatformType, previous: PlatformType) {
		return this.warningDialogService
			.openPlatformTypeDialog(previous)
			.pipe(
				switchMap((_) =>
					this.typesService.partialUpdate(current, previous)
				)
			);
	}

	openEnumDialog(platformType: PlatformType, editMode: boolean) {
		/**
		 * Display a warning for the following:
		 * Each modified enum
		 * The modified enum set
		 * The modified platform type(s)
		 */
		return this.dialog
			.open(EditEnumSetDialogComponent, {
				data: {
					platformType: structuredClone(platformType),
					isOnEditablePage: editMode,
				},
				minWidth: '70vw',
				minHeight: '80vh',
			})
			.afterClosed()
			.pipe(
				filter((x) => x !== undefined) as OperatorFunction<
					enumerationSet | undefined,
					enumerationSet
				>,
				take(1),
				switchMap((enumSet) =>
					editMode
						? this.warningDialogService
								.openEnumsDialogs(
									enumSet.enumerations.map((v) => v.id),
									[enumSet.id],
									[platformType.id]
								)
								.pipe(
									map((_) => enumSet),
									switchMap((enumSet) =>
										this.enumSetService.changeEnumSet(
											enumSet,
											platformType.enumSet
										)
									)
								)
						: of()
				)
			);
	}
}
