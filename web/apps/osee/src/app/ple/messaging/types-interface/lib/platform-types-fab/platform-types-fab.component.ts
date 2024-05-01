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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatFabButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CurrentTypesService } from '../services/current-types.service';
import { MatDialog } from '@angular/material/dialog';
import { NewTypeDialogComponent } from '../new-type-dialog/new-type-dialog.component';
import {
	PlatformType,
	enumeration,
	newPlatformTypeDialogReturnData,
} from '@osee/messaging/shared/types';
import { applic } from '@osee/shared/types/applicability';
import { filter, OperatorFunction, switchMap } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-platform-types-fab',
	standalone: true,
	imports: [MatFabButton, MatIcon, AsyncPipe],
	template: `@if ((inEditMode | async) === true) {
		<button
			mat-fab
			class="tw-bg-success-200"
			(click)="openNewTypeDialog()"
			data-cy="add-type-bottom-button">
			<mat-icon>add</mat-icon>
		</button>
	} `,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlatformTypesFabComponent {
	private typesService = inject(CurrentTypesService);
	private dialog = inject(MatDialog);
	protected inEditMode = this.typesService.inEditMode;
	openNewTypeDialog() {
		this.dialog
			.open(NewTypeDialogComponent, {
				id: 'new-type-dialog',
				minHeight: '70vh',
				minWidth: '80vw',
			})
			.afterClosed()
			.pipe(
				filter((x) => x !== undefined) as OperatorFunction<
					newPlatformTypeDialogReturnData | undefined,
					newPlatformTypeDialogReturnData
				>,
				switchMap(
					({ platformType, createEnum, enumSet, ...enumData }) =>
						this.mapTo(platformType, createEnum, enumData).pipe()
				)
			)
			.subscribe();
	}
	/**
	 *
	 * @TODO replace enumData with actual enum
	 */
	mapTo(
		results: Partial<PlatformType>,
		newEnum: boolean,
		enumData: {
			enumSetId: string;
			enumSetName: string;
			enumSetDescription: string;
			enumSetApplicability: applic;
			enums: enumeration[];
		}
	) {
		results.name =
			(results?.name?.charAt(0)?.toLowerCase() || '') +
			results?.name?.slice(1);
		return this.typesService.createType(results, newEnum, enumData);
	}
}
