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
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Input,
	Output,
	effect,
	input,
	viewChild,
	inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
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
import type { NodeTraceReportItem } from '@osee/messaging/shared/types';
import { HeaderService } from '@osee/shared/services';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { BehaviorSubject, debounceTime, distinct, skip } from 'rxjs';
import { nodeTraceReportHeaderDetails } from './trace-report-table-headers';

@Component({
	selector: 'osee-trace-report-table',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		NgTemplateOutlet,
		AsyncPipe,
		FormsModule,
		HighlightFilteredTextDirective,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
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
	templateUrl: './trace-report-table.component.html',
})
export class TraceReportTableComponent {
	private headerService = inject(HeaderService);

	data = input.required<NodeTraceReportItem[]>();

	sort = viewChild.required(MatSort);
	paginator = viewChild.required(MatPaginator);

	private _setSort = effect(() => {
		this.dataSource.sort = this.sort();
	});

	private _setPaginator = effect(() => {
		this.dataSource.paginator = this.paginator();
	});

	private _setDataSourceData = effect(() => {
		this.dataSource.data = this.data();
	});

	dataSource: MatTableDataSource<NodeTraceReportItem> =
		new MatTableDataSource<NodeTraceReportItem>([]);

	@Input() set pageSize(value: number) {
		if (value) {
			this.pageSize$.next(value);
		}
	}
	_pageSize = 200;
	pageSize$ = new BehaviorSubject<number>(this._pageSize);

	@Output() paginationSize = this.pageSize$.pipe(
		skip(1),
		debounceTime(50),
		distinct()
	);

	@Input() set total(value: number) {
		if (value) {
			this.total$.next(value);
		}
	}

	total$ = new BehaviorSubject<number>(0);

	@Input() set currentPage(value: number) {
		if (value) {
			this.currentPage$.next(value);
		}
	}

	currentPage$ = new BehaviorSubject<number>(0);

	@Output() currentPageChange = this.currentPage$.pipe(
		skip(1),
		debounceTime(50),
		distinct()
	);

	filterPredicate(data: NodeTraceReportItem, filter: string) {
		const filterLower = filter.toLowerCase();
		if (
			data.name.toLowerCase().includes(filterLower) ||
			data.artifactType.toLowerCase().includes(filterLower)
		) {
			return true;
		}
		for (const rel of data.relatedItems) {
			if (
				rel.name.toLowerCase().includes(filterLower) ||
				rel.artifactType.toLowerCase().includes(filterLower)
			) {
				return true;
			}
		}
		return false;
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filterPredicate = this.filterPredicate;
		this.dataSource.filter = filterValue;
	}

	getTableHeaderByName(header: keyof NodeTraceReportItem) {
		return this.headerService.getHeaderByName(
			nodeTraceReportHeaderDetails,
			header
		);
	}
	trackRows(_index: number, item: NodeTraceReportItem) {
		return item.id;
	}

	headers: (keyof NodeTraceReportItem)[] = [
		'name',
		'artifactType',
		'relatedItems',
	];
	updatePage(ev: PageEvent) {
		if (this.currentPage$.getValue() !== ev.pageIndex) {
			this.currentPage$.next(ev.pageIndex);
		}
		if (this.pageSize$.getValue() !== ev.pageSize) {
			this.pageSize = ev.pageSize;
		}
	}
}
