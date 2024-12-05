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
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, computed, input, output, inject } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { BranchInfoService } from '@osee/shared/services';
import {
	teamWorkflowDetails,
	teamWorkflowState,
} from '@osee/shared/types/configuration-management';
import { Subject, combineLatest, iif, of } from 'rxjs';
import { filter, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import { branch, branchSentinel } from '@osee/shared/types';
import { CommitBranchService } from '@osee/commit/services';
import { MergeManagerDialogComponent } from '@osee/commit/components';

/**
 * Allows users to create and manage the state of a branch from within a page.
 */
@Component({
	selector: 'osee-action-dropdown',
	templateUrl: './action-drop-down.component.html',
	styles: [],
	imports: [
		MatButton,
		MatIcon,
		MatMenuTrigger,
		MatMenu,
		MatMenuItem,
		NgClass,
		NgTemplateOutlet,
	],
})
export class ActionDropDownComponent {
	private actionService = inject(ActionStateButtonService);
	dialog = inject(MatDialog);
	private commitBranchService = inject(CommitBranchService);
	private branchService = inject(BranchInfoService);

	teamWorkflow = input.required<teamWorkflowDetails>();
	branch = input<branch>(branchSentinel);
	commitAllowed = input(true);
	update = output();

	private _updateWorkflow = new Subject<boolean>();

	teamWorkflow$ = toObservable(this.teamWorkflow);
	branch$ = toObservable(this.branch);

	nextStates = computed(() => this.teamWorkflow().toStates);
	previousStates = computed(() =>
		this.teamWorkflow().previousStates.slice(0, -1)
	);

	currentState = computed(() => this.teamWorkflow().currentState);
	currentStateName = computed(() =>
		this.currentState().state.replace(/([a-z])([A-Z])/g, '$1 $2')
	);

	isTeamLead = toSignal(
		this.teamWorkflow$.pipe(
			switchMap((wf) => this.actionService.isTeamLead(wf)),
			shareReplay({ bufferSize: 1, refCount: true })
		)
	);

	transitionApproved = toSignal(
		this.teamWorkflow$.pipe(
			filter(
				(action) =>
					action.TeamWfAtsId !== undefined &&
					action.TeamWfAtsId !== '' &&
					action.TeamWfAtsId != '-1'
			),
			switchMap((action) =>
				this.actionService.isTransitionApproved(action)
			),
			shareReplay({ bufferSize: 1, refCount: true })
		)
	);

	transition(state: teamWorkflowState) {
		this.teamWorkflow$
			.pipe(
				take(1),
				switchMap((action) =>
					this.actionService.transition(state, action)
				),
				tap((res) => {
					if (res.results.length === 0) {
						this._updateWorkflow.next(true);
						this.update.emit();
					}
				})
			)
			.subscribe();
	}

	approveBranch() {
		this.teamWorkflow$
			.pipe(
				take(1),
				switchMap((action) => this.actionService.approveBranch(action)),
				tap((res) => {
					if (res) {
						this._updateWorkflow.next(true);
						this.update.emit();
					}
				})
			)
			.subscribe();
	}

	commitBranch(): void {
		combineLatest([this.branch$, this.teamWorkflow$])
			.pipe(
				take(1),
				filter(
					([branch, tw]) =>
						branch.id !== '-1' && tw.id !== 0 && tw.id !== -1
				),
				switchMap(([branch, teamWorkflow]) =>
					this.branchService.getBranch(branch.parentBranch.id).pipe(
						filter((parentBranch) => parentBranch.id !== '-1'),
						switchMap((parentBranch) =>
							this.commitBranchService
								.validateCommit(branch.id, parentBranch.id)
								.pipe(
									switchMap((validateResults) => {
										if (validateResults.conflictCount > 0) {
											return this.dialog
												.open(
													MergeManagerDialogComponent,
													{
														data: {
															sourceBranch:
																branch,
															destBranch:
																parentBranch,
															validateResults:
																validateResults,
														},
														minWidth: '60%',
													}
												)
												.afterClosed()
												.pipe(take(1));
										}
										return of(true);
									}),
									switchMap((commit) =>
										iif(
											() => commit === true,
											this.actionService.commitBranch(
												teamWorkflow,
												branch,
												parentBranch
											),
											of()
										)
									)
								)
						)
					)
				)
			)
			.subscribe();
	}
}
