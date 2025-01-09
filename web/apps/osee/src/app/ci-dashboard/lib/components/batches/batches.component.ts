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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
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
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderService } from '@osee/shared/services';
import { FormatMillisecondsPipe } from '@osee/shared/utils';
import { BehaviorSubject, combineLatest, switchMap, take, tap } from 'rxjs';
import { CiBatchService } from '../../services/ci-batch.service';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { resultHeaderDetails } from '../../table-headers/result-headers';
import { ResultReference } from '../../types';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { BatchDropdownComponent } from './batch-dropdown/batch-dropdown.component';
import { CiDetailsService } from '../../services/ci-details.service';

@Component({
	selector: 'osee-batches',
	imports: [
		AsyncPipe,
		CiDashboardControlsComponent,
		BatchDropdownComponent,
		FormatMillisecondsPipe,
		MatButton,
		MatIcon,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
	],
	templateUrl: './batches.component.html',
})
export default class BatchesComponent {
	private uiService = inject(CiDashboardUiService);
	private batchService = inject(CiBatchService);
	private headerService = inject(HeaderService);
	private route = inject(ActivatedRoute);

	router = inject(Router);
	detailsService = inject(CiDetailsService);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		this.batchService.selectedBatchId
			.pipe(
				tap((_) => this.currentPage.next(0)),
				takeUntilDestroyed()
			)
			.subscribe();

		this.route.paramMap
			.pipe(takeUntilDestroyed())
			.subscribe(
				(params) =>
					(this.batchService.SelectedBatchId =
						params.get('batchId') || '-1')
			);
	}

	branchId = toSignal(this.uiService.branchId);
	setId = toSignal(this.uiService.ciSetId);
	batchId = toSignal(this.batchService.selectedBatchId);

	currentPage = new BehaviorSubject<number>(0);
	pageSize = new BehaviorSubject<number>(250);

	results = combineLatest([this.currentPage, this.pageSize]).pipe(
		switchMap(([pageNum, pageSize]) =>
			this.batchService.getBatchResults(pageNum + 1, pageSize)
		)
	);

	resultsCount = this.batchService.getBatchResultsCount();

	headers: (keyof ResultReference)[] = [
		'name',
		'totalTestPoints',
		'passedCount',
		'failedCount',
		'scriptAborted',
		'elapsedTime',
		'machineName',
	];

	getTableHeaderByName(header: keyof ResultReference) {
		return this.headerService.getHeaderByName(resultHeaderDetails, header);
	}

	setPage(event: PageEvent) {
		this.currentPage.next(event.pageIndex);
		this.pageSize.next(event.pageSize);
	}

	downloadBatch() {
		this.batchService.downloadBatch().pipe(take(1)).subscribe();
	}

	navigateToResults(result: ResultReference) {
		let url = this.router.url;
		url = url.replace('batches', 'details');
		url = url.split('/').slice(0, -1).join('/'); // Remove batch id from url
		this.detailsService.CiDefId = result.definitionId;
		this.router.navigateByUrl(url);
	}
}
