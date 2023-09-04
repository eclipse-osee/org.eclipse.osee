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
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { filter, switchMap, take } from 'rxjs/operators';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import { CreateAction } from '@osee/shared/types/configuration-management';
import { CreateActionDialogComponent } from '../create-action-dialog/create-action-dialog.component';
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';

/**
 * Allows users to create and manage the state of a branch from within a page.
 */
@Component({
	selector: 'osee-action-dropdown',
	templateUrl: './action-drop-down.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
		CreateActionDialogComponent,
		MatTooltipModule,
		MatFormFieldModule,
	],
})
export class ActionDropDownComponent implements OnChanges {
	@Input() category: string = '0';
	@Input() workType: string = '';
	branchInfo = this.actionService.branchState;

	branchTransitionable = this.actionService.branchTransitionable;

	branchApprovableOrCommittable = this.actionService.approvedState;
	doAddAction = this.actionService.addActionInitialStep.pipe(
		switchMap((thisUser) =>
			this.dialog
				.open(CreateActionDialogComponent, {
					data: new CreateAction(thisUser),
					minWidth: '60%',
				})
				.afterClosed()
				.pipe(
					take(1),
					filter((val): val is CreateAction => val !== undefined),
					switchMap((value) =>
						this.actionService.doAddAction(value, this.category)
					)
				)
		)
	);

	doApproveBranch = this.actionService.doApproveBranch;

	doTransition = this.actionService.doTransition;
	doCommitBranch = this.actionService.doCommitBranch;

	constructor(
		private actionService: ActionStateButtonService,
		public dialog: MatDialog
	) {}
	ngOnChanges(changes: SimpleChanges): void {
		this.actionService.category = this.category;
		this.actionService.workTypeValue = this.workType;
	}
	addAction(): void {
		this.doAddAction.subscribe();
	}
	transitionToReview(): void {
		this.doTransition.subscribe();
	}
	approveBranch(): void {
		this.doApproveBranch.subscribe();
	}
	commitBranch(): void {
		this.doCommitBranch.subscribe();
	}
}
