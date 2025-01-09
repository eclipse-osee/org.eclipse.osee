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
import { Injectable, effect, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	of,
	switchMap,
	take,
} from 'rxjs';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TmoHttpService } from './tmo-http.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class CiBatchService {
	private uiService = inject(CiDashboardUiService);
	private tmoHttp = inject(TmoHttpService);
	private router = inject(Router);
	private route = inject(ActivatedRoute);

	private _selectedBatchId = new BehaviorSubject<string>('-1');

	private _selectedBatch = combineLatest([
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
			this.tmoHttp.getBatch(branchId, batchId)
		)
	);

	private _queryParamMap = toSignal(this.route.queryParamMap);
	private _queryParamEffect = effect(() => {
		const params = this._queryParamMap();
		if (!params) {
			return;
		}
		const batchId = params.get('batch');
		if (batchId !== null) {
			this.SelectedBatchId = batchId;
		} else {
			this.SelectedBatchId = '-1';
		}
	});

	getBatches(pageNum: number | string, pageSize: number, filterText: string) {
		return combineLatest([
			this.uiService.branchId,
			this.uiService.ciSetId,
		]).pipe(
			filter(
				([branchId, setId]) =>
					branchId !== '' &&
					branchId !== '-1' &&
					setId !== '' &&
					setId !== '-1'
			),
			switchMap(([branchId, setId]) =>
				this.tmoHttp.getBatches(
					branchId,
					setId,
					filterText,
					pageNum,
					pageSize
				)
			)
		);
	}

	getBatchesCount(filterText: string) {
		return combineLatest([
			this.uiService.branchId,
			this.uiService.ciSetId,
		]).pipe(
			filter(
				([branchId, setId]) =>
					branchId !== '' &&
					branchId !== '-1' &&
					setId !== '' &&
					setId !== '-1'
			),
			switchMap(([branchId, setId]) =>
				this.tmoHttp.getBatchesCount(branchId, setId, filterText)
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
		this.SelectedBatchId = id;
		const tree = this.router.parseUrl(this.router.url);
		const queryParams = tree.queryParams;
		if (!id || id === '' || id === '-1') {
			delete queryParams['batch'];
		} else {
			queryParams['batch'] = id;
		}
		this.router.navigate([], { queryParams: queryParams });
	}

	downloadBatch() {
		return combineLatest([
			this.uiService.branchId,
			this.selectedBatch,
		]).pipe(
			switchMap((params) =>
				of(params).pipe(
					take(1),
					switchMap(([branchId, batch]) =>
						this.tmoHttp.downloadBatch(branchId, batch.id)
					),
					map((res) => {
						if (res.size !== 0) {
							const blob = new Blob([res], {
								type: 'application/zip',
							});
							const url = URL.createObjectURL(blob);
							const link = document.createElement('a');
							link.href = url;
							link.setAttribute(
								'download',
								params[1].testEnvBatchId + '.zip'
							);
							document.body.appendChild(link);
							link.click();
							link.remove();
						}
					})
				)
			)
		);
	}

	get selectedBatchId() {
		return this._selectedBatchId.asObservable();
	}

	set SelectedBatchId(batchId: string) {
		this._selectedBatchId.next(batchId);
	}

	get selectedBatch() {
		return this._selectedBatch;
	}
}
