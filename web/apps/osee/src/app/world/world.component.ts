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
	Component,
	computed,
	effect,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { SafeResourceUrl } from '@angular/platform-browser';
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
import { ActivatedRoute } from '@angular/router';
import { debounceTime, map, switchMap } from 'rxjs';
import { WorldHttpService } from './services/world-http.service';
import { worldRow } from './world';

@Component({
	selector: 'osee-world',
	standalone: true,
	imports: [
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
class WorldComponent {
	private routeUrl = inject(ActivatedRoute);
	private worldService = inject(WorldHttpService);
	dataSource = new MatTableDataSource<worldRow>([]);
	displayPublish = true;
	collId = '';
	params = this.routeUrl.queryParamMap.pipe(
		map((value) => {
			return {
				collId: value.get('collId') || '',
				custId: value.get('custId') || '',
			};
		})
	);
	worldData = this.params.pipe(
		debounceTime(500),
		switchMap((value) => {
			this.collId = value.collId;
			if (value.custId === '') {
				this.displayPublish = false;
				return this.worldService.getWorldDataStored(value.collId);
			}
			return this.worldService.getWorldData(value.collId, value.custId);
		})
	);
	tabledata = toSignal(this.worldData, {
		initialValue: { orderedHeaders: [], rows: [] },
	});
	filter = signal('');
	headers = computed(() => this.tabledata()?.orderedHeaders || []);
	rows = computed(() => this.tabledata()?.rows || []);
	protected sort = viewChild.required(MatSort);

	private _updateDataSourceSort = effect(() => {
		this.dataSource.sort = this.sort();
	});
	private _updateDataSourceData = effect(() => {
		this.dataSource.data = this.rows();
	});
	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}
	publish() {
		this.params
			.pipe(
				switchMap((value) => {
					return this.worldService.publishWorldData(
						value.collId,
						value.custId,
						this.tabledata()
					);
				})
			)
			.subscribe();
	}
	openExport(collId: string) {
		this.openLink('/ats/world/coll/' + collId + '/export');
	}
	openLink(url: string) {
		window.open(url, '_blank');
	}
}
export default WorldComponent;
