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
	teamWorkflowImpl,
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
} from 'rxjs';
import { CurrentBranchInfoService } from './current-branch-info.service';
import { ActionService } from '../http/action.service';
import { UserDataAccountService } from '@osee/auth';

@Injectable({
	providedIn: 'root',
})
export class CurrentActionService {
	constructor(
		private currentBranchService: CurrentBranchInfoService,
		private actionService: ActionService,
		private accountService: UserDataAccountService
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

	private _branchWorkflow = this.branchAction.pipe(
		switchMap((val) =>
			iif(
				() =>
					val.length > 0 &&
					val[0]?.TeamWfAtsId != '' &&
					typeof val[0]?.id !== 'undefined',
				this.actionService.getWorkFlow(val[0]?.id).pipe(
					//repeatWhen((_) => this.uiService.update),
					share()
				),
				of(new teamWorkflowImpl())
			)
		),
		share(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get branchWorkFlow() {
		return this._branchWorkflow;
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
