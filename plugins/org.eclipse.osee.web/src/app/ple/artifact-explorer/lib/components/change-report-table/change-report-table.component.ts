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
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	of,
	shareReplay,
	switchMap,
} from 'rxjs';
import { HeaderService } from '@osee/shared/services';
import { changeReportRow } from '@osee/shared/types/change-report';
import { ChangeReportService } from './services/change-report.service';
import { changeReportHeaders } from './change-report-table-headers';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { MatTableModule } from '@angular/material/table';

@Component({
	selector: 'osee-change-report-table',
	templateUrl: './change-report-table.component.html',
	styles: [],
	standalone: true,
	imports: [NgIf, NgClass, NgFor, AsyncPipe, MatTableModule],
})
export class ChangeReportTableComponent implements OnChanges {
	@Input() branchId: string = '';

	constructor(
		private headerService: HeaderService,
		private crService: ChangeReportService
	) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes.branchId) {
			this.branchId$.next(this.branchId);
		}
	}

	headers: (keyof changeReportRow)[] = [
		'ids',
		'names',
		'itemType',
		'itemKind',
		'changeType',
		'isValue',
		'wasValue',
	];

	getHeaderByName(value: keyof changeReportRow) {
		return this.headerService.getHeaderByName(changeReportHeaders, value);
	}

	branchId$ = new BehaviorSubject<string>('');

	branch = this.branchId$.pipe(
		switchMap((branchId) => this.crService.getBranchInfo(branchId))
	);

	parentBranch = this.branch.pipe(
		switchMap((branch) =>
			this.crService.getBranchInfo(branch.parentBranch.id)
		),
		shareReplay(1)
	);

	action = this.branch.pipe(
		switchMap((branch) =>
			this.crService.getRelatedAction(branch.associatedArtifact)
		),
		shareReplay(1)
	);

	latestTxInfo = this.branch.pipe(
		switchMap((branch) => this.crService.getLatestTxInfo(branch.id)),
		shareReplay(1)
	);

	changes = combineLatest([this.branch, this.parentBranch]).pipe(
		switchMap(([branch, parentBranch]) =>
			this.crService.getBranchChanges(branch.id, parentBranch.id)
		),
		shareReplay(1)
	);

	viewData = combineLatest([
		this.branch,
		this.parentBranch,
		this.action,
		this.latestTxInfo,
	]).pipe(
		switchMap(([branch, parentBranch, action, txInfo]) =>
			of({ branch, parentBranch, action, txInfo })
		)
	);
}
