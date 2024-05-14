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
import { Injectable } from '@angular/core';
import { UserDataAccountService } from '@osee/auth';
import {
	ActionService,
	BranchInfoService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
import {
	CreateAction,
	CreateNewAction,
} from '@osee/shared/types/configuration-management';
import {
	BehaviorSubject,
	iif,
	map,
	of,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { BranchRoutedUIService } from '../../../internal/services/branch-routed-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CreateActionService {
	private _workType = new BehaviorSubject<string>('');

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

	constructor(
		private actionService: ActionService,
		private branchService: BranchInfoService,
		private uiService: UiService,
		private branchedRouter: BranchRoutedUIService,
		private accountService: UserDataAccountService,
		private currentBranchService: CurrentBranchInfoService
	) {}

	get user() {
		return this.accountService.user;
	}

	get workType() {
		return this._workType.asObservable();
	}

	set workTypeValue(workType: string) {
		this._workType.next(workType);
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
			return this.actionService.createAction(new CreateNewAction(value));
		}
		return this.actionService.createBranch(new CreateNewAction(value)).pipe(
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
							const _branchType =
								resp.workingBranchId.branchType === '2'
									? 'baseline'
									: 'working';
							this.branchedRouter.position = {
								type: _branchType,
								id: resp.workingBranchId.id,
							};
						}
					})
				)
			)
		);
	}
}
