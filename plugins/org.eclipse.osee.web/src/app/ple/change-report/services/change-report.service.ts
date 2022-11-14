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
import { iif, of, shareReplay, switchMap, take } from 'rxjs';
import { ActionService } from 'src/app/ple-services/http/action.service';
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import { TransactionService } from 'src/app/transactions/transaction.service';
import { ChangeReportHttpService } from './change-report-http.service';

@Injectable({
	providedIn: 'root',
})
export class ChangeReportService {
	constructor(
		private crHttpService: ChangeReportHttpService,
		private branchInfoService: BranchInfoService,
		private actionService: ActionService,
		private txService: TransactionService
	) {}

	getBranchChanges(branch1Id: string, branch2Id: string) {
		return this.crHttpService.getBranchChangeReport(branch1Id, branch2Id);
	}

	getTxChanges(branchId: string, tx1: string, tx2: string) {
		return this.crHttpService.getTxChangeReport(branchId, tx1, tx2);
	}

	getBranchInfo(branchId: string) {
		return this.branchInfoService.getBranch(branchId).pipe(shareReplay(1));
	}

	getRelatedAction(actionId: string) {
		return this.actionService
			.getAction(actionId)
			.pipe(
				switchMap((actions) =>
					iif(() => actions.length > 0, of(actions[0]), of())
				)
			);
	}

	getLatestTxInfo(branchId: string) {
		return this.txService.getLatestBranchTransaction(branchId);
	}
}
