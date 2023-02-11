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
import {
	Observable,
	switchMap,
	take,
	repeatWhen,
	tap,
	shareReplay,
	of,
	map,
} from 'rxjs';
import { UiService } from '../../../../ple-services/ui/ui.service';
import { GCBranchIdService } from '../../fetch-data-services/branch/gc-branch-id.service';
import {
	executedCommandHistory,
	userHistory,
} from '../../../types/grid-commander-types/userHistory';
import { GetUserHistoryService } from '../../fetch-data-services/user-history/get-user-history.service';
import { HistoryService } from './history.service';
import { UserDataAccountService } from '@osee/auth';
import { user } from '@osee/shared/types/auth';

@Injectable({
	providedIn: 'root',
})
export class UserHistoryService {
	private _user: Observable<user> = this.accountService.user;
	private _defaultBranchId = this.branchIdService.branchId;
	private _userHistory$: Observable<userHistory> = this.userHistoryResponse;
	newCmdHistory: Partial<executedCommandHistory> = {
		name: 'UsersCommandHistory',
	};

	constructor(
		private getUserHistoryService: GetUserHistoryService,
		private historyService: HistoryService,
		private accountService: UserDataAccountService,
		private uiService: UiService,
		private branchIdService: GCBranchIdService
	) {}

	get userHistoryResponse() {
		return this._user.pipe(
			take(1),
			switchMap((user) =>
				this.getUserHistoryService
					.getUserHistory(this._defaultBranchId)
					.pipe(
						repeatWhen(() => this.uiService.update),
						switchMap((val) =>
							val.executedCommandHistory ===
								'No history available' ||
							val.commandHistoryId === ''
								? this.createInitialHistory(user).pipe(
										map((initialHx) => val)
								  )
								: of(val)
						)
					)
			),
			shareReplay({ bufferSize: 1, refCount: true })
		);
	}

	private createInitialHistory(user: user) {
		return of(user).pipe(
			take(1),
			switchMap((user) =>
				this.historyService
					.createExecutedCommandHistoryArtifact(
						this._defaultBranchId,
						this.newCmdHistory
					)
					.pipe(
						take(1),
						switchMap((relation) =>
							this.historyService.performMutation(relation).pipe(
								take(1),
								switchMap((transactionResult) =>
									this.historyService.createUserHistoryRelation(
										user.id,
										transactionResult.results.ids[0]
									)
								),
								take(1),
								switchMap((relation) =>
									this.historyService.createUserToHistoryRelationship(
										this._defaultBranchId,
										relation
									)
								),
								take(1),
								switchMap((transaction) =>
									this.historyService
										.performMutation(transaction)
										.pipe(
											tap(
												() =>
													(this.uiService.updated =
														true)
											)
										)
								)
							)
						)
					)
			)
		);
	}

	public get userHistory$() {
		return this._userHistory$;
	}
}
