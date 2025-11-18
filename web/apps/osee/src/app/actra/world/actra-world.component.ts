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
	Component,
	computed,
	effect,
	inject,
	OnInit,
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
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, shareReplay, switchMap } from 'rxjs';
import { ActraWorldHttpService } from '../services/actra-world-http.service';
import { MatButton } from '@angular/material/button';
import { ActraPageTitleComponent } from '../actra-page-title/actra-page-title.component';
import { worldRow, worldDataEmpty } from '../types/actra-types';
import { UiService } from '@osee/shared/services';
import { CreateActionButtonComponent } from '../../configuration-management/components/create-action-button/create-action-button.component';

@Component({
	selector: 'osee-actra-world',
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
		MatButton,
		ActraPageTitleComponent,
		RouterLink,
		CreateActionButtonComponent,
	],
	templateUrl: './actra-world.component.html',
})
export class ActraWorldComponent implements OnInit {
	private routeUrl = inject(ActivatedRoute);
	private worldService = inject(ActraWorldHttpService);
	dataSource = new MatTableDataSource<worldRow>([]);
	uiService = inject(UiService);

	ngOnInit(): void {
		this.uiService.idValue = '570';
	}

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
		}),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _worldData = toSignal(this.__worldData, {
		initialValue: worldDataEmpty,
	});

	title = computed(() => this._worldData().title);

	titleContainsMyWorld = computed(() => this.title().includes('My World'));

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
		if (this.sort()) {
			this.dataSource.sort = this.sort();
		}
	});

	private _updateDataSourceData = effect(() => {
		this.dataSource.data = this.rows();
	});

	private _initializeSortAndFilter = effect(() => {
		if (this.sort()) {
			this.dataSource.sortingDataAccessor = (item, property) => {
				return item[property];
			};
			this.dataSource.filterPredicate = (
				row: worldRow,
				filter: string
			) => {
				const filterLower = filter.toLowerCase();
				for (const key of Object.keys(row)) {
					if (row[key].toLowerCase().includes(filterLower)) {
						return true;
					}
				}
				return false;
			};
		}
	});

	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}

	// @todo: replace with loaded default customizations
	widths: Record<string, string> = {
		Name: 'tw-max-w-full tw-font-bold',
		Description: 'tw-max-w-80',
	};

	defaultWidth = 'tw-max-w-80';

	getWidthClass(header: string): string {
		return this.widths[header] ?? this.defaultWidth;
	}
}
export default ActraWorldComponent;
