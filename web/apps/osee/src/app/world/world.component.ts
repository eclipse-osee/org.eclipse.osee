/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { MatButton } from '@angular/material/button';
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
import { filter, map, shareReplay, switchMap, take, tap } from 'rxjs';
import { WorldHttpService } from './services/world-http.service';
import { worldRow, worldRowWithDiffs, worldWithDiffs } from './world';
import { NgClass, Location } from '@angular/common';

@Component({
	selector: 'osee-world',
	imports: [
		NgClass,
		MatFormField,
		MatInput,
		MatButton,
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
	templateUrl: './world.component.html',
})
class WorldComponent implements AfterViewInit {
	private router = inject(Router);
	private routeUrl = inject(ActivatedRoute);
	private worldService = inject(WorldHttpService);
	dataSource = new MatTableDataSource<worldRowWithDiffs>([]);

	params = this.routeUrl.queryParamMap.pipe(
		map((value) => {
			return {
				collId: value.get('collId') || '',
				custId: value.get('custId') || '',
				diff: value.get('diff') || '',
			};
		})
	);
	paramsSignal = toSignal(this.params);
	showDiffs = computed(() => this.paramsSignal()?.diff);
	displayPublish = computed(() => this.paramsSignal()?.custId !== '');

	private _worldData = toSignal(
		this.params.pipe(
			switchMap((value) => {
				if (value.custId === '') {
					return this.worldService.getWorldDataStored(value.collId);
				}
				return this.worldService.getWorldData(
					value.collId,
					value.custId
				);
			}),
			takeUntilDestroyed(),
			shareReplay({ bufferSize: 1, refCount: true })
		),
		{
			initialValue: {
				orderedHeaders: [],
				rows: [],
				collectorArt: { name: '' },
				atsId: '',
			},
		}
	);

	collectorName = computed(() => this._worldData().collectorArt.name);
	collectorId = computed(() => this._worldData().atsId);

	worldDataLoaded = computed(
		() =>
			this._worldData().orderedHeaders.length > 0 &&
			this._worldData().rows.length > 0 &&
			this._worldData().collectorArt.name.length > 0
	);

	private _worldDataStored = toSignal(
		this.params.pipe(
			filter((params) => params.diff === 'true'),
			switchMap((params) =>
				this.worldService.getWorldDataStored(params.collId)
			),
			takeUntilDestroyed(),
			shareReplay({ bufferSize: 1, refCount: true })
		)
	);

	tableData = computed(() => {
		const dataWithDiffs: worldWithDiffs = {
			orderedHeaders: this._worldData().orderedHeaders,
			rows: [],
			collectorArt: { name: '' },
			atsId: '',
		};

		// Convert current rows to rows with diffs, with no changes initially.
		dataWithDiffs.rows = this._worldData().rows.map((row) => {
			const newRow: worldRowWithDiffs = {};
			Object.keys(row).forEach((key) => {
				newRow[key] = {
					value: row[key],
					added: false,
					deleted: false,
					changed: false,
				};
			});
			return newRow;
		});

		if (
			this.showDiffs() &&
			this.paramsSignal()?.custId !== '' &&
			this._worldDataStored()
		) {
			// Create and populate maps of ATS IDs to rows for easy lookup.
			// This assumes ATS Id will always be available in the customization
			// and should probably be changed to something that will definitely
			// always be available (add something to each row on backend)
			const worldDataMap = new Map<string, worldRow>();
			const storedDataMap = new Map<string, worldRow>();
			this._worldData().rows.forEach((row) =>
				worldDataMap.set(row['ATS Id'], row)
			);
			this._worldDataStored()!.rows.forEach((row) =>
				storedDataMap.set(row['ATS Id'], row)
			);

			// Check if current rows have an existing entry in the stored data.
			// If not, mark the current row as added. If so, check for changed values.
			dataWithDiffs.rows.forEach((row) => {
				const storedRow = storedDataMap.get(row['ATS Id'].value);
				if (!storedRow) {
					row['ATS Id'].added = true;
				} else {
					Object.keys(row).forEach((key) => {
						if (row[key].value !== storedRow[key]) {
							row[key].changed = true;
						}
					});
				}
			});

			// Go back through the stored data and see if there is anything in there
			// that is not in the current data. If so, add it and mark deleted.
			storedDataMap.forEach((row, key) => {
				if (!worldDataMap.has(key)) {
					const newRow: worldRowWithDiffs = {};
					Object.keys(row).forEach((rowKey) => {
						newRow[rowKey] = {
							value: row[rowKey],
							added: false,
							deleted: true,
							changed: false,
						};
					});
					dataWithDiffs.rows.push(newRow);
				}
			});
		}

		return dataWithDiffs;
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
			return item[property].value;
		};
		this.dataSource.filterPredicate = (
			row: worldRowWithDiffs,
			filter: string
		) => {
			const filterLower = filter.toLowerCase();
			for (const key of Object.keys(row)) {
				if (row[key].value.toLowerCase().includes(filterLower)) {
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

	toggleDiff() {
		const tree = this.router.parseUrl(this.router.url);
		const queryParams = tree.queryParams;
		if (queryParams['diff']) {
			delete queryParams['diff'];
		} else {
			queryParams['diff'] = 'true';
		}
		this.router.navigate([], { queryParams: queryParams });
	}

	publish() {
		this.params
			.pipe(
				take(1),
				switchMap((value) => {
					return this.worldService.publishWorldData(
						value.collId,
						value.custId,
						this._worldData()
					);
				})
			)
			.subscribe();
	}

	exportAsJson() {
		if (!this.worldDataLoaded()) {
			return;
		}
		const json = JSON.stringify(this._worldData());
		const link = document.createElement('a');
		link.setAttribute(
			'href',
			'data:text/json;charset=UTF-8,' + encodeURIComponent(json)
		);
		link.setAttribute('download', 'world.json');
		document.body.appendChild(link);
		link.click();
		link.remove();
	}

	exportAsHtml() {
		this.params
			.pipe(
				take(1),
				tap((params) =>
					this.openLink(
						'/ats/world/coll/' + params.collId + '/export'
					)
				)
			)
			.subscribe();
	}

	location = inject(Location);
	openSaved() {
		this.params
			.pipe(
				take(1),
				tap((params) => {
					const collId = params.collId;
					const urlTree = this.router.createUrlTree(['/world'], {
						queryParams: { collId: collId },
					});
					const relativeUrl = this.router.serializeUrl(urlTree);
					const externalUrl =
						this.location.prepareExternalUrl(relativeUrl);
					window.open(externalUrl, '_blank', 'noopener');
				})
			)
			.subscribe();
	}

	openLink(url: string) {
		window.open(url, '_blank');
	}
}
export default WorldComponent;
