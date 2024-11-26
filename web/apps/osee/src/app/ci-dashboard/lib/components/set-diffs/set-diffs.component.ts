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
import { Component, effect, signal, viewChild, inject } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
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
import { MatTooltip } from '@angular/material/tooltip';
import { HeaderService } from '@osee/shared/services';
import { SplitStringPipe } from '@osee/shared/utils';
import { combineLatest, tap } from 'rxjs';
import { CiSetDiffService } from '../../services/ci-set-diff.service';
import { setDiffHeaderDetails } from '../../table-headers/set-diff.headers';
import { SetDiff } from '../../types';
import { CiDashboardControlsComponent } from '../ci-dashboard-controls/ci-dashboard-controls.component';
import { SetDropdownMultiComponent } from './set-dropdown-multi/set-dropdown-multi.component';

@Component({
	selector: 'osee-set-diffs',
	imports: [
		NgClass,
		AsyncPipe,
		CiDashboardControlsComponent,
		SetDropdownMultiComponent,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
		SplitStringPipe,
	],
	templateUrl: './set-diffs.component.html',
})
export default class SetDiffsComponent {
	private diffService = inject(CiSetDiffService);
	private headerService = inject(HeaderService);

	private paginator = viewChild.required(MatPaginator);

	dataSource = new MatTableDataSource<SetDiff>();

	private _updateDataSourcePaginator = effect(() => {
		this.dataSource.paginator = this.paginator();
	});

	defaultHeaders: ('name' | 'equal')[] = ['name', 'equal'];
	setDiffHeaders: ('passes' | 'fails' | 'abort')[] = [
		'passes',
		'fails',
		'abort',
	];
	groupHeaders = signal([' ']);
	headers = signal<
		(
			| 'name'
			| 'equal'
			| 'passes'
			| 'fails'
			| 'abort'
			| `name-${string}`
			| `equal-${string}`
			| `passes-${string}`
			| `fails-${string}`
			| `abort-${string}`
		)[]
	>(this.defaultHeaders);

	selectedSets = this.diffService.selectedSets;

	setDiffs = combineLatest([
		this.diffService.setDiffs,
		this.selectedSets,
	]).pipe(
		tap(([setDiffs, sets]) => {
			this.dataSource.data = setDiffs;
			this.groupHeaders.set([' ']);
			this.headers.set(this.defaultHeaders);
			for (const set of sets) {
				this.groupHeaders.update((headers) => [
					...headers,
					set.name.value,
				]);
				const mappedHeaders = this.setDiffHeaders.map(
					(h) => `${h}-${set.id}` as const
				);
				this.headers.update((headers) => [
					...headers,
					...mappedHeaders,
				]);
			}
		})
	);

	getTableHeaderByName(
		header:
			| `name`
			| `equal`
			| `passes`
			| `fails`
			| `abort`
			| `name-${string}`
			| `equal-${string}`
			| `passes-${string}`
			| `fails-${string}`
			| `abort-${string}`
	) {
		const formattedHeader: 'name' | 'equal' | 'passes' | 'fails' | 'abort' =
			header.split('-')[0] as
				| 'name'
				| 'equal'
				| 'passes'
				| 'fails'
				| 'abort';
		return this.headerService.getHeaderByName(
			setDiffHeaderDetails,
			formattedHeader
		);
	}
}
