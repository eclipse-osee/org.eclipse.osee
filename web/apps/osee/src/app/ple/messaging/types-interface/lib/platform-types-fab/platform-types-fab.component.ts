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
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatMiniFabButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { PlatformType } from '@osee/messaging/shared/types';
import { OperatorFunction, filter, switchMap } from 'rxjs';
import { NewTypeDialogComponent } from '../new-type-dialog/new-type-dialog.component';
import { CurrentTypesService } from '../services/current-types.service';

@Component({
	selector: 'osee-platform-types-fab',
	imports: [MatMiniFabButton, MatIcon, AsyncPipe],
	template: `@if ((inEditMode | async) === true) {
		<button
			mat-mini-fab
			class="tertiary-fab"
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
					PlatformType | undefined,
					PlatformType
				>,
				switchMap((platformType) =>
					this.typesService.createType(platformType)
				)
			)
			.subscribe();
	}
}
