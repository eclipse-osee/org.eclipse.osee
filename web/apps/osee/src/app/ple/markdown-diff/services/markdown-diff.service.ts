/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { BranchInfoService } from '@osee/shared/services';
import { shareReplay } from 'rxjs';
import { MarkdownDiffHttpService } from './markdown-diff-http.service';

@Injectable({
	providedIn: 'root',
})
export class MarkdownDiffService {
	private httpService = inject(MarkdownDiffHttpService);
	private branchInfoService = inject(BranchInfoService);

	getBranchInfo(branchId: string) {
		return this.branchInfoService.getBranch(branchId).pipe(shareReplay(1));
	}

	getMarkdownChanges(
		branchId: string,
		parentBranchId: string,
		filter = '',
		pageNum = 0,
		pageSize = 10
	) {
		return this.httpService.getMarkdownChanges(
			branchId,
			parentBranchId,
			filter,
			pageNum,
			pageSize
		);
	}

	getMarkdownChangesCount(
		branchId: string,
		parentBranchId: string,
		filter = ''
	) {
		return this.httpService.getMarkdownChangesCount(
			branchId,
			parentBranchId,
			filter
		);
	}
}
