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
import { Component } from '@angular/core';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { BatchDropdownComponent } from './batch-dropdown/batch-dropdown.component';
import { MatTableModule } from '@angular/material/table';
import { CiBatchService } from '../../services/ci-batch.service';
import { CommonModule } from '@angular/common';
import { ResultReference } from '../../types';
import { HeaderService } from '@osee/shared/services';
import { resultHeaderDetails } from '../../table-headers/result-headers';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormatMillisecondsPipe } from '@osee/shared/utils';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { BehaviorSubject, combineLatest, switchMap, take, tap } from 'rxjs';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CiDashboardUiService } from 'src/app/ci-dashboard/lib/services/ci-dashboard-ui.service';

@Component({
	selector: 'osee-results',
	standalone: true,
	imports: [
		CommonModule,
		CiDashboardControlsComponent,
		BatchDropdownComponent,
		MatTableModule,
		MatPaginatorModule,
		MatTooltipModule,
		FormatMillisecondsPipe,
		MatButtonModule,
		MatIconModule,
	],
	templateUrl: './results.component.html',
})
export default class ResultsComponent {
	constructor(
		private uiService: CiDashboardUiService,
		private batchService: CiBatchService,
		private headerService: HeaderService,
		private route: ActivatedRoute
	) {
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
}
