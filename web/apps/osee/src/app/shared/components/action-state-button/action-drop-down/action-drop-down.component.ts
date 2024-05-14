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
import { AsyncPipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { Component } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { BranchInfoService, CommitBranchService } from '@osee/shared/services';
import { teamWorkflowState } from '@osee/shared/types/configuration-management';
import { iif, of } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { MergeManagerDialogComponent } from '../../merge-manager-dialog/merge-manager-dialog.component';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';

/**
 * Allows users to create and manage the state of a branch from within a page.
 */
@Component({
	selector: 'osee-action-dropdown',
	templateUrl: './action-drop-down.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		MatButton,
		MatIcon,
		MatIconButton,
		MatMenuTrigger,
		MatMenu,
		MatMenuItem,
		NgClass,
		NgTemplateOutlet,
	],
})
export class ActionDropDownComponent {
	branchTransitionable = this.actionService.branchTransitionable;

	nextStates = this.actionService.nextStates;

	previousStates = this.actionService.previousStates;

	currentState = this.actionService.currentState;
	currentStateName = this.currentState.pipe(
		map((state) => state.state),
		map((name) => name.replace(/([a-z])([A-Z])/g, '$1 $2')) //yucky regex to convert camelcase to spaced out
	);

	isTeamLead = this.actionService.isTeamLead;

	isApproved = this.actionService.branchApproved.pipe(takeUntilDestroyed());

	doApproveBranch = this.actionService.doApproveBranch;

	doCommitBranch = this.actionService.branchState.pipe(
		take(1),
		switchMap((currentBranch) =>
			this.branchService.getBranch(currentBranch.parentBranch.id).pipe(
				switchMap((parentBranch) =>
					this.commitBranchService.validateCommit(currentBranch).pipe(
						switchMap((validateResults) => {
							if (validateResults.conflictCount > 0) {
								return this.dialog
									.open(MergeManagerDialogComponent, {
										data: {
											sourceBranch: currentBranch,
											parentBranch: parentBranch,
											validateResults: validateResults,
										},
										minWidth: '60%',
									})
									.afterClosed()
									.pipe(take(1));
							}
							return of(true);
						}),
						switchMap((commit) =>
							iif(
								() => commit === true,
								this.actionService.doCommitBranch,
								of()
							)
						)
					)
				)
			)
		)
	);

	constructor(
		private actionService: ActionStateButtonService,
		public dialog: MatDialog,
		private commitBranchService: CommitBranchService,
		private branchService: BranchInfoService
	) {}
	transition(state: teamWorkflowState) {
		this.actionService.transition(state).subscribe();
	}
	approveBranch(): void {
		this.doApproveBranch.subscribe();
	}
	commitBranch(): void {
		this.doCommitBranch.subscribe();
	}
}
