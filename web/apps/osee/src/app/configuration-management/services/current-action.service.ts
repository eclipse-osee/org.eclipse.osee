/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import {
	actionImpl,
	teamWorkflowDetailsImpl,
} from '@osee/shared/types/configuration-management';
import {
	switchMap,
	iif,
	share,
	of,
	shareReplay,
	combineLatest,
	concatMap,
	filter,
	from,
	map,
	repeat,
} from 'rxjs';
import { UserDataAccountService } from '@osee/auth';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { ActionService } from './action.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentActionService {
	constructor(
		private currentBranchService: CurrentBranchInfoService,
		private actionService: ActionService,
		private accountService: UserDataAccountService,
		private uiService: UiService
	) {}
	private _user = this.accountService.user;
	private _branchState = this.currentBranchService.currentBranch;

	public get branchState() {
		return this._branchState;
	}

	private _branchAction = this.branchState.pipe(
		switchMap((val) =>
			iif(
				() =>
					val.associatedArtifact != '-1' &&
					typeof val !== 'undefined' &&
					val.associatedArtifact !== '',
				this.actionService.getAction(val.associatedArtifact).pipe(
					//repeatWhen((_) => this.uiService.update),
					share()
				),
				of([new actionImpl()])
			)
		),
		share(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get branchAction() {
		return this._branchAction;
	}

	private _branchWorkflowToken = this.branchState.pipe(
		filter(
			(state) =>
				state.associatedArtifact !== '' &&
				state.associatedArtifact !== '-1'
		),
		switchMap((state) =>
			this.actionService.searchTeamWorkflows({
				search: state.associatedArtifact,
				searchByArtId: true,
			})
		),
		filter((teamWfs) => teamWfs.length === 1),
		map((teamWfs) => teamWfs[0])
	);

	private _branchWorkflow = this.branchAction.pipe(
		switchMap((val) =>
			iif(
				() =>
					val.length > 0 &&
					val[0]?.TeamWfAtsId != '' &&
					typeof val[0]?.id !== 'undefined',
				this.actionService.getTeamWorkflowDetails(val[0]?.id).pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter(
									(updatedId) =>
										updatedId === val[0].id.toString()
								)
							),
					}),
					share()
				),
				of(new teamWorkflowDetailsImpl())
			)
		),
		share(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get branchWorkFlow() {
		return this._branchWorkflow;
	}

	public get branchWorkflowToken() {
		return this._branchWorkflowToken;
	}

	private _teamsLeads = this.branchWorkFlow.pipe(
		switchMap((workflow) =>
			iif(
				() => workflow['ats.Team Definition Reference'].length > 0,
				this.actionService.getTeamLeads(
					workflow['ats.Team Definition Reference']
				),
				of([])
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get teamsLeads() {
		return this._teamsLeads;
	}
	private get user() {
		return this._user;
	}

	private _isATeamLead = combineLatest([this.teamsLeads, this.user]).pipe(
		switchMap(([leads, user]) =>
			of([leads, user]).pipe(
				concatMap(([leadsResponse, currentUser]) =>
					from(leads).pipe(
						filter((val) => val.id === user.id),
						map((v) => true)
					)
				)
			)
		)
	);

	get isTeamLead() {
		return this._isATeamLead;
	}
}
