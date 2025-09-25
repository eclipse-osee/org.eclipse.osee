/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import {
	AfterViewInit,
	Component,
	computed,
	effect,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSort, MatSortHeader } from '@angular/material/sort';
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
import { ActivatedRoute, Router } from '@angular/router';
import { map, shareReplay, switchMap, tap } from 'rxjs';
import { WorldHttpService } from './services/actra-world-http.service';
import { worldRow } from './actra-world';

@Component({
	selector: 'osee-world',
	/** should this be stand-alone? error if not! these should be included in app and not have to be re-imported? */
	standalone: true,
	imports: [
		MatFormField,
		MatInput,
		MatTable,
		MatSort,
		MatColumnDef,
		MatSortHeader,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
	templateUrl: './actra-world.component.html',
})
export class ActraWorldComponent implements AfterViewInit {
	private router = inject(Router);
	private routeUrl = inject(ActivatedRoute);
	private worldService = inject(WorldHttpService);
	dataSource = new MatTableDataSource<worldRow>([]);

	params = this.routeUrl.queryParamMap.pipe(
		map((value) => {
			return {
				op: value.get('op') || '',
				collId: value.get('collId') || '',
				custId: value.get('custId') || '',
				diff: value.get('diff') || '',
			};
		})
	);
	paramsSignal = toSignal(this.params);
	private __worldData = this.params.pipe(
		switchMap((value) => {
			if (this.paramsSignal.length === 0 || value.op === 'my') {
				return this.worldService.getWorldDataMy();
			}
			return this.worldService.getWorldData(value.collId, value.custId);
			// .pipe(
			// 	tap(v=>console.log(v))
			// );
		}),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _worldData = toSignal(this.__worldData, {
		initialValue: {
			orderedHeaders: [],
			rows: [],
			title: '',
			atsId: '',
		},
	});

	title = computed(() => this._worldData().title);

	worldDataLoaded = computed(
		() =>
			this._worldData().orderedHeaders.length > 0 &&
			this._worldData().rows.length > 0 &&
			this._worldData().title.length > 0
	);

	tableData = computed(() => {
		return this._worldData();
	});

	filter = signal('');
	headers = computed(() => this.tableData()?.orderedHeaders || []);
	rows = computed(() => this.tableData()?.rows || []);
	protected sort = viewChild.required(MatSort);

	private _updateDataSourceSort = effect(() => {
		this.dataSource.sort = this.sort();
	});
	private _updateDataSourceData = effect(() => {
		this.dataSource.data = this.rows();
	});

	ngAfterViewInit(): void {
		this.dataSource.sortingDataAccessor = (item, property) => {
			return item[property];
		};
		this.dataSource.filterPredicate = (row: worldRow, filter: string) => {
			const filterLower = filter.toLowerCase();
			for (const key of Object.keys(row)) {
				if (row[key].toLowerCase().includes(filterLower)) {
					return true;
				}
			}
			return false;
		};
	}

	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}

	openLink(url: string) {
		window.open(url, '_blank');
	}

	openWorkflow(wfId: string) {
		this.openLink('/actra/workflow?id=' + wfId);
	}
}
export default ActraWorldComponent;
