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
import { BehaviorSubject, map, switchMap } from 'rxjs';
import { ServerHealthHttpService } from '../../../shared/services/server-health-http.service';
import { tablespace } from '../../../shared/types/server-health-types';

@Component({
	selector: 'osee-server-health-tablespace',
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
	],
	templateUrl: './server-health-tablespace.component.html',
})
export class ServerHealthTablespaceComponent {
	private serverHealthHttpService = inject(ServerHealthHttpService);

	private sort = viewChild(MatSort);

	private _updateDataSourceSort = effect(() => {
		const sort = this.sort();
		if (sort) {
			this.dataSource.sort = sort;
		}
	});

	orderBy$ = new BehaviorSubject<orderByPair>({
		orderByName: '',
		orderByDirection: '',
	});

	displayedColumns = [
		'tablespaceName',
		'maxTsPctUsed',
		'autoExtend',
		'tsPctUsed',
		'tsPctFree',
		'usedTsSize',
		'freeTsSize',
		'currTsSize',
		'maxTxSize',
	];

	getMatPctUsedColor(value: string, index: number) {
		const num: number = +value;
		if (value === '') {
			if (index % 2 === 0) {
				return 'tw-bg-warning-300';
			} else {
				return 'tw-bg-warning-400';
			}
		} else if (num >= 90 && num < 97) {
			if (index % 2 === 0) {
				return 'tw-bg-accent-700';
			} else {
				return 'tw-bg-accent-800';
			}
		} else if (num < 90) {
			if (index % 2 === 0) {
				return 'tw-bg-success-300';
			} else {
				return 'tw-bg-success-400';
			}
		} else {
			if (index % 2 === 0) {
				return 'tw-bg-warning-300';
			} else {
				return 'tw-bg-warning-400';
			}
		}
	}

	updateOrderBy(sort: Sort) {
		if (sort.active && sort.direction !== '') {
			switch (sort.active) {
				case 'tablespaceName':
					this.orderBy$.next({
						orderByName: 'tablespaceName',
						orderByDirection: sort.direction,
					});
					break;
				case 'maxTsPctUsed':
					this.orderBy$.next({
						orderByName: 'maxTsPctUsed',
						orderByDirection: sort.direction,
					});
					break;
				case 'autoExtend':
					this.orderBy$.next({
						orderByName: 'autoExtend',
						orderByDirection: sort.direction,
					});
					break;
				case 'tsPctUsed':
					this.orderBy$.next({
						orderByName: 'tsPctUsed',
						orderByDirection: sort.direction,
					});
					break;
				case 'tsPctFree':
					this.orderBy$.next({
						orderByName: 'tsPctFree',
						orderByDirection: sort.direction,
					});
					break;
				case 'usedTsSize':
					this.orderBy$.next({
						orderByName: 'usedTsSize',
						orderByDirection: sort.direction,
					});
					break;
				case 'freeTsSize':
					this.orderBy$.next({
						orderByName: 'freeTsSize',
						orderByDirection: sort.direction,
					});
					break;
				case 'currTsSize':
					this.orderBy$.next({
						orderByName: 'currTsSize',
						orderByDirection: sort.direction,
					});
					break;
				case 'maxTxSize':
					this.orderBy$.next({
						orderByName: 'maxTxSize',
						orderByDirection: sort.direction,
					});
					break;
				default:
					this.orderBy$.next({
						orderByName: 'maxTsPctUsed',
						orderByDirection: sort.direction,
					});
					break;
			}
		}
	}

	private dataSource = new MatTableDataSource<tablespace>();
	tablespaceDatasource = this.orderBy$.pipe(
		switchMap((orderBy) =>
			this.serverHealthHttpService
				.getTablespace(orderBy.orderByName, orderBy.orderByDirection)
				.pipe(
					map((data) => {
						const dataSource = this.dataSource;
						dataSource.data = data.tablespaces;
						return dataSource;
					})
				)
		)
	);
}

type orderByPair = {
	orderByName: string;
	orderByDirection: string;
};
