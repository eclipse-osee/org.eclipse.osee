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
import { iif, of, combineLatest, BehaviorSubject } from 'rxjs';
import {
	filter,
	switchMap,
	repeatWhen,
	share,
	shareReplay,
	take,
	tap,
	map,
	distinctUntilChanged,
} from 'rxjs/operators';
import {
	ActionService,
	BranchInfoService,
	CurrentActionService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
import { BranchCategoryService } from '../../../internal/services/branch-category.service';
import { UserDataAccountService } from '@osee/auth';
import { BranchRoutedUIService } from '../../../internal/services/branch-routed-ui.service';
import {
	transitionAction,
	CreateAction,
	CreateNewAction,
} from '@osee/shared/types/configuration-management';

@Injectable({
	providedIn: 'root',
})
export class ActionStateButtonService {
	private _workType = new BehaviorSubject<string>('');
	constructor(
		private uiService: UiService,
		private actionService: ActionService,
		private branchService: BranchInfoService,
		private currentBranchService: CurrentBranchInfoService,
		private accountService: UserDataAccountService,
		private branchedRouter: BranchRoutedUIService,
		private branchCategoryService: BranchCategoryService,
		private currentActionService: CurrentActionService
	) {}

	set category(category: string) {
		this.branchCategoryService.category = category;
	}

	set workTypeValue(workType: string) {
		this._workType.next(workType);
	}

	get workType() {
		return this._workType.asObservable();
	}

	private _user = this.accountService.user;

	private _actionableItems = this.workType.pipe(
		filter((val) => val !== ''),
		distinctUntilChanged(),
		switchMap((workType) =>
			this.actionService.getActionableItems(workType).pipe(
				repeatWhen((_) => this.uiService.update),
				share()
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get actionableItems() {
		return this._actionableItems;
	}
	getVersions(actionableItem: string) {
		return this.actionService.getVersions(actionableItem);
	}

	getChangeTypes(actionableItem: string) {
		return this.actionService.getChangeTypes(actionableItem);
	}
	private _branchState = this.currentBranchService.currentBranchDetail;
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
						switchMap((approval) =>
							iif(
								() => approval.errorCount > 0,
								of('false'),
								of('true')
							)
						),
						shareReplay({ bufferSize: 1, refCount: true })
					),
				of('false')
			)
		)
	);
	private _teamsLeads = this.currentActionService.teamsLeads;

	private _isATeamLead = this.currentActionService.isTeamLead;
	private _branchTransitionable = this.branchWorkFlow.pipe(
		switchMap((workflow) =>
			iif(() => workflow.State === 'InWork', of('true'), of('false'))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

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

	private _addActionInitialStep = this._user.pipe(take(1));
	public get addActionInitialStep() {
		return this._addActionInitialStep;
	}

	private _doApproveBranch = this.branchAction.pipe(
		take(1),
		switchMap((actions) =>
			iif(
				() => actions.length > 0,
				this.actionService.approveBranch(actions[0].TeamWfAtsId).pipe(
					tap((response) => {
						if (response.results.length > 0) {
							this.uiService.error = response.results[0];
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
											this.uiService.error =
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
																this.uiService.error =
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

	public doAddAction(value: CreateAction, category: string) {
		return this._user.pipe(
			take(1),
			switchMap((user) =>
				iif(
					() => typeof value?.description != 'undefined',
					this.actionService
						.createBranch(new CreateNewAction(value))
						.pipe(
							switchMap((branchResponse) =>
								iif(
									() => category !== '0',
									this.branchService.setBranchCategory(
										branchResponse.workingBranchId.id,
										category
									),
									of(branchResponse)
								).pipe(
									map(() => branchResponse),
									tap((resp) => {
										this.uiService.updated = true;
										if (resp.results.success) {
											let _branchType = '';
											if (
												resp.workingBranchId
													.branchType === '2'
											) {
												_branchType = 'baseline';
											} else {
												_branchType = 'working';
											}
											this.branchedRouter.position = {
												type: _branchType,
												id: resp.workingBranchId.id,
											};
										}
									})
								)
							)
						),
					of() // @todo replace with a false response
				)
			)
		);
	}
}
