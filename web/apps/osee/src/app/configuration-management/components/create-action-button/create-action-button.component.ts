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
import { Component, inject, input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { filter, switchMap, take, tap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { CreateActionDialogComponent } from './create-action-dialog/create-action-dialog.component';
import { CreateAction } from '@osee/configuration-management/types';
import { CreateActionService } from '@osee/configuration-management/services';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { RouterLink } from '@angular/router';

@Component({
	selector: 'osee-create-action-button',
	imports: [MatButton, MatIcon, RouterLink],
	template: `
		<a
			[routerLink]="!opensDialog() ? '/actra/action/create' : null"
			target="_blank">
			<button
				mat-flat-button
				(click)="opensDialog() ? addAction() : null">
				<mat-icon>add</mat-icon>Create Action
			</button>
		</a>
	`,
})
export class CreateActionButtonComponent {
	category = input('0');
	workType = input('');
	/**
	 * Defines whether create action button opens a dialog or navigates to new browser tab
	 */
	opensDialog = input(true);

	createActionService = inject(CreateActionService);
	private uiService = inject(UiService);
	private branchedRouter = inject(BranchRoutedUIService);
	dialog = inject(MatDialog);

	addAction() {
		this.createActionService.user
			.pipe(
				switchMap((thisUser) =>
					this.dialog
						.open(CreateActionDialogComponent, {
							data: new CreateAction(
								thisUser,
								true,
								this.workType()
							),
							minWidth: '60vw',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter(
								(val): val is CreateAction => val !== undefined
							),
							switchMap((value) =>
								this.createActionService.createAction(
									value,
									this.category()
								)
							),
							tap((resp) => {
								this.uiService.updated = true;
								if (
									resp.results.success &&
									resp.workingBranchId !== null
								) {
									const _branchType =
										resp.workingBranchId.branchType === '2'
											? 'baseline'
											: 'working';
									this.branchedRouter.position = {
										type: _branchType,
										id: resp.workingBranchId.id,
									};
								}
							})
						)
				)
			)
			.subscribe();
	}
}
