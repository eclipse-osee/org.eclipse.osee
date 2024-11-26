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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, effect, viewChild, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
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
	MatTableDataSource,
} from '@angular/material/table';
import {
	BehaviorSubject,
	combineLatest,
	map,
	shareReplay,
	switchMap,
} from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';
import { sql } from '../../../shared/types/server-health-types';

@Component({
	selector: 'osee-server-health-sql',
	imports: [
		NgClass,
		AsyncPipe,
		MatTable,
		MatSort,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatSortHeader,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
	],
	templateUrl: './server-health-sql.component.html',
})
export class ServerHealthSqlComponent {
	private serverHealthHttpService = inject(ServerHealthHttpService);

	private paginator = viewChild(MatPaginator);
	private sort = viewChild(MatSort);
	private _updateSort = effect(() => {
		const sort = this.sort();
		if (sort) {
			this.dataSource.sort = sort;
		}
	});

	private _updatePaginator = effect(() => {
		const paginator = this.paginator();
		if (paginator) {
			this.dataSource.paginator = paginator;
		}
	});

	displayedColumns = [
		'sqlText',
		'percent',
		'elapsedTime',
		'executions',
		'elapsedTimeAverage',
	];

	pageSize$ = new BehaviorSubject<number>(10);
	pageNum$ = new BehaviorSubject<number>(0);
	orderBy$ = new BehaviorSubject<orderByPair>({
		orderByName: '',
		orderByDirection: '',
	});

	updateOrderBy(sort: Sort) {
		if (sort.active && sort.direction !== '') {
			switch (sort.active) {
				case 'percent':
					this.orderBy$.next({
						orderByName: 'percent',
						orderByDirection: sort.direction,
					});
					break;
				case 'sqlText':
					this.orderBy$.next({
						orderByName: 'sqlText',
						orderByDirection: sort.direction,
					});
					break;
				case 'elapsedTime':
					this.orderBy$.next({
						orderByName: 'elapsedTime',
						orderByDirection: sort.direction,
					});
					break;
				case 'executions':
					this.orderBy$.next({
						orderByName: 'executions',
						orderByDirection: sort.direction,
					});
					break;
				case 'elapsedTimeAverage':
					this.orderBy$.next({
						orderByName: 'elapsedTimeAverage',
						orderByDirection: sort.direction,
					});
					break;
				default:
					this.orderBy$.next({
						orderByName: 'elapsedTime',
						orderByDirection: sort.direction,
					});
					break;
			}
		}
	}

	updatePage(ev: PageEvent) {
		if (this.pageNum$.getValue() !== ev.pageIndex) {
			this.pageNum$.next(ev.pageIndex);
		}
		if (this.pageSize$.getValue() !== ev.pageSize) {
			this.pageSize$.next(ev.pageSize);
		}
	}

	private dataSource = new MatTableDataSource<sql>();
	sqlDataSource = combineLatest([
		this.pageNum$,
		this.pageSize$,
		this.orderBy$,
	]).pipe(
		switchMap(([pageNum, pageSize, orderBy]) =>
			this.serverHealthHttpService
				.getSql(
					pageNum,
					pageSize,
					orderBy.orderByName,
					orderBy.orderByDirection
				)
				.pipe(
					map((data) => {
						const dataSource = this.dataSource;
						dataSource.data = data.sqls;
						return dataSource;
					})
				)
		)
	);

	sqlSize = this.serverHealthHttpService.SqlSize.pipe(
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
}

type orderByPair = {
	orderByName: string;
	orderByDirection: string;
};
