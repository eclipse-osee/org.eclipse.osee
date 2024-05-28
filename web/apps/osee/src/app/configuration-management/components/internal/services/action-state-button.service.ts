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
import { Injectable } from '@angular/core';
import { iif, of, combineLatest } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import {
	BranchRoutedUIService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
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

@Injectable({
	providedIn: 'root',
})
export class ActionStateButtonService {
	constructor(
		public dialog: MatDialog,
		private uiService: UiService,
		private actionService: ActionService,
		private currentBranchService: CurrentBranchInfoService,
		private accountService: UserDataAccountService,
		private branchedRouter: BranchRoutedUIService,
		private currentActionService: CurrentActionService
	) {}

	private _user = this.accountService.user;
	private _branchAction = this.currentActionService.branchAction;

	performTransition(state: teamWorkflowState, action: action) {
		return combineLatest([
			this._user,
			this.currentActionService.branchAction,
		]).pipe(
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
		return this._user.pipe(
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
		return this._user.pipe(
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
		return this._branchAction.pipe(
			take(1),
			switchMap((branchActions) =>
				this.actionService.approveBranch(action.TeamWfAtsId).pipe(
					tap((res) => {
						if (!res) {
							this.uiService.ErrorText = `Failed to approve branch ${action.TeamWfAtsId}`;
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

	private _doCommitBranch = combineLatest([
		this._branchAction,
		this._user,
	]).pipe(
		take(1),
		switchMap(([actions, user]) =>
			iif(
				() => actions.length > 0 && user.name.length > 0,
				this.commitBranch({
					committer: user.id,
					archive: 'false',
				}).pipe(
					switchMap((commitObs) =>
						iif(
							() => commitObs.success,
							this.actionService
								.validateTransitionAction(
									new transitionAction(
										'Completed',
										'Transition to Completed',
										actions,
										user
									)
								)
								.pipe(
									switchMap((validateObs) =>
										iif(
											() =>
												validateObs.results.length ===
												0,
											this.actionService
												.transitionAction(
													new transitionAction(
														'Completed',
														'Transition To Completed',
														actions,
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
																	.length > 0
															) {
																this.uiService.ErrorText =
																	transitionResponse.results[0];
															} else {
																this.uiService.updated =
																	true;
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

	public commitBranch(body: { committer: string; archive: string }) {
		return this.currentBranchService.commitBranch(body);
	}

	public get doCommitBranch() {
		return this._doCommitBranch;
	}
}
