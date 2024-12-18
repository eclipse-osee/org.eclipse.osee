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
import { Injectable, inject } from '@angular/core';
import { iif, of, combineLatest } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { UserDataAccountService } from '@osee/auth';
import {
	transitionAction,
	teamWorkflowState,
	action,
	actionImpl,
	teamWorkflowDetails,
} from '@osee/shared/types/configuration-management';
import { MatDialog } from '@angular/material/dialog';
import {
	ActionService,
	CurrentActionService,
} from '@osee/configuration-management/services';
import { CommitBranchService } from '@osee/commit/services';
import { branch } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ActionStateButtonService {
	dialog = inject(MatDialog);
	private uiService = inject(UiService);
	private actionService = inject(ActionService);
	private accountService = inject(UserDataAccountService);
	private branchedRouter = inject(BranchRoutedUIService);
	private currentActionService = inject(CurrentActionService);
	private commitBranchService = inject(CommitBranchService);

	private _user$ = this.accountService.user;
	private _currentAction$ = this.currentActionService.branchAction;

	performTransition(state: teamWorkflowState, action: action) {
		return combineLatest([this._user$, this._currentAction$]).pipe(
			take(1),
			switchMap(([user, branchActions]) =>
				this.actionService
					.transitionAction(
						new transitionAction(
							state.state,
							'Transition to ' + state.state,
							[action],
							user
						)
					)
					.pipe(
						tap((response) => {
							if (response.results.length > 0) {
								this.uiService.ErrorText = response.results[0];
							} else if (
								branchActions.length > 0 &&
								branchActions[0].id === action.id
							) {
								this.uiService.updated = true;
								this.uiService.updatedArtifact = `${action.id}`;
							}
						})
					)
			)
		);
	}

	public isTransitionApproved(action: action) {
		return this.actionService.getBranchApproved(action.TeamWfAtsId);
	}

	public getAction(actionId: string | number) {
		return this.actionService
			.getAction(actionId)
			.pipe(
				switchMap((actions) =>
					iif(
						() => actions.length > 0 && actions[0].id !== -1,
						of(actions[0]),
						of(new actionImpl())
					)
				)
			);
	}

	public getWorkflow(artifactId: `${number}`) {
		return this.actionService.getWorkFlow(artifactId);
	}

	public transition(state: teamWorkflowState, action: action) {
		return this.transitionValidate(state, action).pipe(
			switchMap((validation) =>
				iif(
					() => validation.results.length === 0,
					this.performTransition(state, action),
					of()
				)
			)
		);
	}

	private transitionValidate(state: teamWorkflowState, action: action) {
		return this._user$.pipe(
			take(1),
			switchMap((user) =>
				this.actionService.validateTransitionAction(
					new transitionAction(
						state.state,
						'Transition to ' + state.state,
						[action],
						user
					)
				)
			)
		);
	}

	isTeamLead(teamWorkflow: teamWorkflowDetails) {
		return this._user$.pipe(
			map((user) => {
				let isLead = false;
				teamWorkflow.leads.forEach((lead) => {
					if (lead.id === user.id) {
						isLead = true;
						return;
					}
				});
				return isLead;
			})
		);
	}

	approveBranch(action: action) {
		return this._currentAction$.pipe(
			take(1),
			switchMap((currentActions) =>
				this.actionService.approveBranch(action.TeamWfAtsId).pipe(
					tap((res) => {
						if (!res) {
							this.uiService.ErrorText = `Failed to approve branch ${action.TeamWfAtsId}`;
						} else if (
							currentActions.length > 0 &&
							currentActions[0].id === action.id
						) {
							this.uiService.updated = true;
							this.uiService.updatedArtifact = `${action.id}`;
						}
					})
				)
			)
		);
	}

	public commitBranch(action: action, branch: branch, destBranch: branch) {
		return combineLatest([this._user$, this._currentAction$]).pipe(
			take(1),
			switchMap(([user, currentAction]) =>
				iif(
					() =>
						action.id !== 0 &&
						action.id !== -1 &&
						user.name.length > 0,
					this.commitBranchService
						.commitBranch(branch.id, destBranch.id)
						.pipe(
							switchMap((commitObs) =>
								iif(
									() => commitObs.success,
									this.actionService
										.validateTransitionAction(
											new transitionAction(
												'Completed',
												'Transition to Completed',
												[action],
												user
											)
										)
										.pipe(
											switchMap((validateObs) =>
												iif(
													() =>
														validateObs.results
															.length === 0,
													this.actionService
														.transitionAction(
															new transitionAction(
																'Completed',
																'Transition To Completed',
																[action],
																user
															)
														)
														.pipe(
															tap(
																(
																	transitionResponse
																) => {
																	if (
																		transitionResponse
																			.results
																			.length >
																		0
																	) {
																		this.uiService.ErrorText =
																			transitionResponse.results[0];
																	} else {
																		this.uiService.updated =
																			true;
																		if (
																			currentAction.length >
																				0 &&
																			currentAction[0]
																				.id ===
																				action.id
																		)
																			this.branchedRouter.position =
																				{
																					type: 'baseline',
																					id: commitObs
																						.tx
																						.branchId,
																				};
																	}
																}
															)
														),
													of() // @todo replace with a false response
												)
											)
										),
									of() // @todo replace with a false response
								)
							)
						),
					of() // @todo replace with a false response
				)
			)
		);
	}
}
