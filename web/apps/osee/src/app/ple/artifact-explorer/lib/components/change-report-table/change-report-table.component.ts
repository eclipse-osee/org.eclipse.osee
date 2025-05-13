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
import { AsyncPipe } from '@angular/common';
import {
	Component,
	input,
	OnChanges,
	SimpleChanges,
	inject,
	effect,
} from '@angular/core';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { changeReportRow } from '@osee/shared/types/change-report';
import {
	BehaviorSubject,
	combineLatest,
	of,
	shareReplay,
	switchMap,
} from 'rxjs';
import { changeReportHeaders } from './change-report-table-headers';
import { ChangeReportService } from './services/change-report.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-change-report-table',
	templateUrl: './change-report-table.component.html',
	styles: [],
	imports: [
		AsyncPipe,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
})
export class ChangeReportTableComponent {
	private headerService = inject(HeaderService);
	private crService = inject(ChangeReportService);

	branchId = input.required<string>();
	branchId$ = toObservable(this.branchId);

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
