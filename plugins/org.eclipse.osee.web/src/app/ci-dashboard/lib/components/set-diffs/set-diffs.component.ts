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
import { AfterViewInit, Component, ViewChild, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { SetDropdownMultiComponent } from './set-dropdown-multi/set-dropdown-multi.component';
import { CiSetDiffService } from '../../services/ci-set-diff.service';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { setDiffHeaderDetails } from '../../table-headers/set-diff.headers';
import { MatTooltipModule } from '@angular/material/tooltip';
import { combineLatest, tap } from 'rxjs';
import { SetDiff } from '../../types';
import { SplitStringPipe } from '@osee/shared/utils';

@Component({
	selector: 'osee-set-diffs',
	standalone: true,
	imports: [
		CommonModule,
		CiDashboardControlsComponent,
		SetDropdownMultiComponent,
		MatPaginatorModule,
		MatTableModule,
		MatTooltipModule,
		SplitStringPipe,
	],
	templateUrl: './set-diffs.component.html',
})
export default class SetDiffsComponent implements AfterViewInit {
	@ViewChild(MatPaginator) paginator!: MatPaginator;

	dataSource = new MatTableDataSource<SetDiff>();

	defaultHeaders = ['name', 'equal'];
	setDiffHeaders = ['passes', 'fails', 'abort'];
	groupHeaders = signal([' ']);
	headers = signal(this.defaultHeaders);

	constructor(
		private diffService: CiSetDiffService,
		private headerService: HeaderService
	) {}

	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
	}

	selectedSets = this.diffService.selectedSets;

	setDiffs = combineLatest([
		this.diffService.setDiffs,
		this.selectedSets,
	]).pipe(
		tap(([setDiffs, sets]) => {
			this.dataSource.data = setDiffs;
			this.groupHeaders.set([' ']);
			this.headers.set(this.defaultHeaders);
			for (let set of sets) {
				this.groupHeaders.update((headers) => [...headers, set.name]);
				const mappedHeaders = this.setDiffHeaders.map(
					(h) => h + '-' + set.id
				);
				this.headers.update((headers) => [
					...headers,
					...mappedHeaders,
				]);
			}
		})
	);

	getTableHeaderByName(header: string) {
		const formattedHeader = header.split('-')[0];
		return this.headerService.getHeaderByName(
			setDiffHeaderDetails,
			formattedHeader
		);
	}
}
