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
import { switchMap, shareReplay, take, tap, map } from 'rxjs/operators';
import {
	ActionService,
	CommitBranchService,
	CurrentActionService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
import { UserDataAccountService } from '@osee/auth';
import { BranchRoutedUIService } from '../../../internal/services/branch-routed-ui.service';
import {
	transitionAction,
	teamWorkflowState,
} from '@osee/shared/types/configuration-management';
import { MatDialog } from '@angular/material/dialog';

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
		private currentActionService: CurrentActionService,
		private commitBranchService: CommitBranchService
	) {}

	private _user = this.accountService.user;

	private _branchState = this.currentBranchService.currentBranch;
	private _branchAction = this.currentActionService.branchAction;
	private _branchWorkflow = this.currentActionService.branchWorkFlow;

	private _branchApproved = this.branchAction.pipe(
		switchMap((action) =>
			iif(
				() => action.length > 0 && action[0]?.TeamWfAtsId.length > 0,
				this.actionService
					.getBranchApproved(action[0]?.TeamWfAtsId)
					.pipe(
						shareReplay({ bufferSize: 1, refCount: true }),
						switchMap((approval) => of(`${approval}`)),
						shareReplay({ bufferSize: 1, refCount: true })
					),
				of('false')
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _teamsLeads = this.currentActionService.teamsLeads;

	private _isATeamLead = this.currentActionService.isTeamLead;
	private _branchTransitionable = this.branchWorkFlow.pipe(
		switchMap((workflow) =>
			iif(() => workflow.State === 'InWork', of('true'), of('false'))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _nextStates = this.branchWorkFlow.pipe(map((tw) => tw.toStates));

	private _previousStates = this.branchWorkFlow.pipe(
		map((tw) => tw.previousStates.slice(0, -1))
	);

	private _state = this.branchWorkFlow.pipe(map((tw) => tw.currentState));

	get isTeamLead() {
		return this._isATeamLead;
	}
	public get branchAction() {
		return this._branchAction;
	}
	public get branchWorkFlow() {
		return this._branchWorkflow;
	}
	private get user() {
		return this._user;
	}
	public get branchState() {
		return this._branchState;
	}
	public get branchApproved() {
		return this._branchApproved;
	}
	public get teamsLeads() {
		return this._teamsLeads;
	}
	public get branchTransitionable() {
		return this._branchTransitionable;
	}

	public get nextStates() {
		return this._nextStates;
	}

	public get previousStates() {
		return this._previousStates;
	}

	public get currentState() {
		return this._state;
	}

	public transitionValidate(state: teamWorkflowState) {
		return combineLatest([this.branchAction, this._user]).pipe(
			take(1),
			switchMap(([actions, user]) =>
				this.actionService.validateTransitionAction(
					new transitionAction(
						state.state,
						'Transition to ' + state.state,
						actions,
						user
					)
				)
			)
		);
	}

	performTransition(state: teamWorkflowState) {
		return combineLatest([this.branchAction, this._user]).pipe(
			take(1),
			switchMap(([actions, user]) =>
				this.actionService
					.transitionAction(
						new transitionAction(
							state.state,
							'Transition to ' + state.state,
							actions,
							user
						)
					)
					.pipe(
						tap((response) => {
							if (response.results.length > 0) {
								this.uiService.ErrorText = response.results[0];
							} else {
								this.uiService.updated = true;
							}
						})
					)
			)
		);
	}
	public transition(state: teamWorkflowState) {
		return this.transitionValidate(state).pipe(
			switchMap((validation) =>
				iif(
					() => validation.results.length === 0,
					this.performTransition(state),
					of()
				)
			)
		);
	}

	private _doApproveBranch = this.branchAction.pipe(
		take(1),
		switchMap((actions) =>
			iif(
				() => actions.length > 0,
				this.actionService.approveBranch(actions[0].TeamWfAtsId).pipe(
					tap((response) => {
						if (!response) {
							this.uiService.ErrorText = `Failed to approve branch ${actions[0].TeamWfAtsId}`;
						} else {
							this.uiService.updated = true;
						}
					})
				),
				of() // @todo replace with a false response
			)
		)
	);

	private _doTransition = combineLatest([this.branchAction, this._user]).pipe(
		take(1),
		switchMap(([actions, user]) =>
			this.actionService
				.validateTransitionAction(
					new transitionAction(
						'Review',
						'Transition to Review',
						actions,
						user
					)
				)
				.pipe(
					switchMap((validation) =>
						iif(
							() => validation.results.length === 0,
							this.actionService
								.transitionAction(
									new transitionAction(
										'Review',
										'Transition To Review',
										actions,
										user
									)
								)
								.pipe(
									tap((response) => {
										if (response.results.length > 0) {
											this.uiService.ErrorText =
												response.results[0];
										} else {
											this.uiService.updated = true;
										}
									})
								),
							of() // @todo replace with a false response
						)
					)
				)
		)
	);
	private _doCommitBranch = combineLatest([
		this.branchAction,
		this.user,
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

	get approvedState() {
		return this._approvableOrCommittable;
	}
	private _approvableOrCommittable = combineLatest([
		this.branchApproved,
		this.teamsLeads,
		this.branchWorkFlow,
		this._user,
	]).pipe(
		switchMap(([approved, leads, workflow, user]) =>
			iif(
				() => workflow.State === 'Review',
				iif(
					() =>
						leads.filter((lead) => lead.id === user.id).length >
							0 && approved === 'false',
					of('approvable'),
					iif(
						() => approved === 'true',
						of('committable'),
						of('false')
					)
				),
				of('false')
			)
		)
	);
	public commitBranch(body: { committer: string; archive: string }) {
		return this.currentBranchService.commitBranch(body);
	}

	public get doCommitBranch() {
		return this._doCommitBranch;
	}

	public get doTransition() {
		return this._doTransition;
	}

	public get doApproveBranch() {
		return this._doApproveBranch;
	}
}
