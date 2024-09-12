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
import { Injectable, inject } from '@angular/core';
import { UserDataAccountService } from '@osee/auth';
import {
	CreateAction,
	CreateNewAction,
} from '@osee/configuration-management/types';
import {
	BranchInfoService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import {
	BehaviorSubject,
	Subject,
	iif,
	map,
	of,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { ActionService } from './action.service';

@Injectable({
	providedIn: 'root',
})
export class CreateActionService {
	private actionService = inject(ActionService);
	private branchService = inject(BranchInfoService);
	private accountService = inject(UserDataAccountService);
	private currentBranchService = inject(CurrentBranchInfoService);

	private _workType = new BehaviorSubject<string>('');

	private _createdTeamWorkflows = new Subject<`${number}`[]>();

	currentBranch = this.currentBranchService.currentBranch;

	workTypes = this.actionService
		.getWorkTypes()
		.pipe(shareReplay({ bufferSize: 1, refCount: true }));

	actionableItems = this.workType.pipe(
		switchMap((workType) =>
			this.actionService.getActionableItems(workType)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get user() {
		return this.accountService.user;
	}

	get workType() {
		return this._workType.asObservable();
	}

	set workTypeValue(workType: string) {
		this._workType.next(workType);
	}

	get createdTeamWorkflows() {
		return this._createdTeamWorkflows.asObservable();
	}

	set CreatedTeamWorkflows(value: `${number}`[]) {
		this._createdTeamWorkflows.next(value);
	}

	getVersions(actionableItem: string) {
		return this.actionService.getVersions(actionableItem);
	}

	getChangeTypes(actionableItem: string) {
		return this.actionService.getChangeTypes(actionableItem);
	}
	getPoints() {
		return this.actionService.getPoints();
	}
	getCreateActionFields(actionableItemId: string) {
		return this.actionService.getCreateActionFields(actionableItemId);
	}
	getTeamDef(actionableItemId: string) {
		return this.actionService.getTeamDef(actionableItemId);
	}
	getFeatureGroups(teamDefId: string) {
		return this.actionService.getFeatureGroups(teamDefId);
	}
	getSprints(teamDefId: string) {
		return this.actionService.getSprints(teamDefId);
	}

	public createAction(value: CreateAction, category: string) {
		if (typeof value?.description === 'undefined') {
			return of(); // @todo replace with a false response
		}
		if (!value.createBranchDefault) {
			return this.actionService
				.createAction(new CreateNewAction(value))
				.pipe(
					tap((res) => {
						if (res.teamWfs.length > 0) {
							this.CreatedTeamWorkflows = res.teamWfs.map(
								(wf) => wf.id
							);
						}
					})
				);
		}
		return this.actionService
			.createActionAndWorkingBranch(new CreateNewAction(value))
			.pipe(
				switchMap((branchResponse) =>
					iif(
						() => category !== '0',
						this.branchService.setBranchCategory(
							branchResponse.workingBranchId.id,
							category
						),
						of(branchResponse)
					).pipe(map(() => branchResponse))
				)
			);
	}
}
