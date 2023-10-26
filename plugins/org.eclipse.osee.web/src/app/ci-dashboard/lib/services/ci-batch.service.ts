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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, combineLatest, filter, switchMap, take } from 'rxjs';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TmoHttpService } from './tmo-http.service';

@Injectable({
	providedIn: 'root',
})
export class CiBatchService {
	constructor(
		private uiService: CiDashboardUiService,
		private tmoHttp: TmoHttpService,
		private router: Router
	) {}

	private _selectedBatchId = new BehaviorSubject<string>('-1');

	getBatch(batchId: string) {
		return this.uiService.branchId.pipe(
			take(1),
			switchMap((branchId) => this.tmoHttp.getBatch(branchId, batchId))
		);
	}

	getBatches(pageNum: number | string, pageSize: number, filter: string) {
		return combineLatest([
			this.uiService.branchId,
			this.uiService.ciSetId,
		]).pipe(
			switchMap(([branchId, setId]) =>
				this.tmoHttp.getBatches(
					branchId,
					setId,
					filter,
					pageNum,
					pageSize
				)
			)
		);
	}

	getBatchesCount(filter: string) {
		return combineLatest([
			this.uiService.branchId,
			this.uiService.ciSetId,
		]).pipe(
			switchMap(([branchId, setId]) =>
				this.tmoHttp.getBatchesCount(branchId, setId, filter)
			)
		);
	}

	getBatchResults(pageNum: number, pageSize: number) {
		return combineLatest([
			this.uiService.branchId,
			this._selectedBatchId,
		]).pipe(
			filter(
				([branchId, batchId]) =>
					branchId !== '' &&
					branchId !== '-1' &&
					batchId !== '' &&
					batchId !== '-1'
			),
			switchMap(([branchId, batchId]) =>
				this.tmoHttp.getBatchResults(
					branchId,
					batchId,
					pageNum,
					pageSize
				)
			)
		);
	}

	getBatchResultsCount() {
		return combineLatest([
			this.uiService.branchId,
			this._selectedBatchId,
		]).pipe(
			filter(
				([branchId, batchId]) =>
					branchId !== '' &&
					branchId !== '-1' &&
					batchId !== '' &&
					batchId !== '-1'
			),
			switchMap(([branchId, batchId]) =>
				this.tmoHttp.getBatchResultsCount(branchId, batchId)
			)
		);
	}

	routeToBatch(id: string) {
		const formattedBatchId = this.selectedBatchId
			.getValue()
			.replace('-', '%2D');
		const formattedId = id.replace('-', '%2D');
		let url = this.router.url;
		if (url.endsWith(formattedBatchId)) {
			url = url.replace(formattedBatchId, formattedId);
		} else if (url.endsWith(this.uiService.ciSetId.getValue())) {
			url = url + '/' + formattedId;
		}
		this.router.navigateByUrl(url);
	}

	get selectedBatchId() {
		return this._selectedBatchId;
	}

	set SelectedBatchId(batchId: string) {
		this._selectedBatchId.next(batchId);
	}
}
