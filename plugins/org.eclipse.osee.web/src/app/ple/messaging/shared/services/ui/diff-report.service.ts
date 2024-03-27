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
import { switchMap, reduce, shareReplay, map } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import {
	ActionService,
	BranchInfoService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import type {
	branchSummary,
	diffReportSummaryItem,
} from '@osee/messaging/shared/types';
import { DiffReportHttpService } from '../http/diff-report-http.service';

@Injectable({
	providedIn: 'root',
})
export class DiffReportService {
	constructor(
		private currentBranchService: CurrentBranchInfoService,
		private diffService: DiffReportHttpService,
		private branchInfoService: BranchInfoService,
		private actionService: ActionService
	) {}

	private _branchInfo = this.currentBranchService.currentBranch;

	private _parentBranchInfo = this._branchInfo.pipe(
		switchMap((branch) =>
			this.branchInfoService.getBranch(branch.parentBranch.id)
		)
	);

	private _branchSummary = combineLatest([
		this._branchInfo,
		this._parentBranchInfo,
	]).pipe(
		switchMap(([branch, parentBranch]) =>
			this.actionService.getAction(branch.associatedArtifact).pipe(
				map((actions) => {
					return {
						pcrNo: actions.length > 0 ? actions[0].AtsId : '',
						description: actions.length > 0 ? actions[0].Name : '',
						compareBranch: parentBranch.name,
					};
				}),
				reduce((acc, curr) => [...acc, curr], [] as branchSummary[])
			)
		)
	);

	private _diffReport = this.currentBranchService.currentBranch.pipe(
		switchMap((branchId) =>
			this.currentBranchService.parentBranch.pipe(
				switchMap((parentBranchId) =>
					this.diffService.getDifferenceReport(
						branchId.id,
						parentBranchId
					)
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	// The report summary only shows differences in structures
	private _diffReportSummary = this._diffReport.pipe(
		map((summary) => {
			const summaryItems: diffReportSummaryItem[] = [];
			for (let structure of Object.values(summary.structures)) {
				let summaryItem: diffReportSummaryItem = {
					id: structure.artId,
					changeType: 'Structure',
					action: structure.added
						? 'Added'
						: structure.deleted
						  ? 'Deleted'
						  : 'Edited',
					name: structure.name,
					details: [],
				};
				if (structure.attributeChanges.length > 0) {
					summaryItem.details.push('Attribute changes');
				}
				if (structure.applicabilityChanged) {
					summaryItem.details.push('Applicability changed');
				}
				if (structure.children.length > 0) {
					summaryItem.details.push('Element changes');
				}
				summaryItems.push(summaryItem);
			}
			return summaryItems;
		})
	);

	// GETTERS
	get branchInfo() {
		return this._branchInfo;
	}

	get parentBranchInfo() {
		return this._parentBranchInfo;
	}

	get branchSummary() {
		return this._branchSummary;
	}

	get diffReportSummary() {
		return this._diffReportSummary;
	}

	get diffReport() {
		return this._diffReport;
	}
}
