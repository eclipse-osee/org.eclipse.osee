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
	ViewChild,
	computed,
	effect,
	signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldHttpService } from './services/world-http.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { worldRow } from './world';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute } from '@angular/router';
import { debounceTime, map, switchMap, tap } from 'rxjs';

@Component({
	selector: 'osee-world',
	standalone: true,
	imports: [
		CommonModule,
		MatTableModule,
		MatSortModule,
		MatInputModule,
		MatFormFieldModule,
		MatButtonModule,
	],
	templateUrl: './world.component.html',
})
class WorldComponent implements AfterViewInit {
	dataSource = new MatTableDataSource<worldRow>([]);
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
		switchMap((value) =>
			this.worldService.getWorldData(value.collId, value.custId)
		)
	);
	tabledata = toSignal(this.worldData);
	filter = signal('');
	headers = computed(() => this.tabledata()?.orderedHeaders || []);
	rows = computed(() => this.tabledata()?.rows || []);

	constructor(
		private worldService: WorldHttpService,
		private routeUrl: ActivatedRoute
	) {
		effect(() => {
			this.dataSource.data = this.rows();
		});
	}

	@ViewChild(MatSort) sort!: MatSort;
	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	updateFilter(event: KeyboardEvent) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
		this.dataSource.filter = filterValue.trim().toLowerCase();
	}
}
export default WorldComponent;
