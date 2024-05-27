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
import { TransactionHistoryService } from '@osee/transaction-history/services';
import { iif, of, shareReplay, switchMap } from 'rxjs';
import { BranchInfoService } from '@osee/shared/services';
import { ChangeReportHttpService } from './change-report-http.service';
import { ActionService } from '@osee/configuration-management/services';

@Injectable({
	providedIn: 'root',
})
export class ChangeReportService {
	private crHttpService = inject(ChangeReportHttpService);
	private branchInfoService = inject(BranchInfoService);
	private actionService = inject(ActionService);
	private txService = inject(TransactionHistoryService);

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
