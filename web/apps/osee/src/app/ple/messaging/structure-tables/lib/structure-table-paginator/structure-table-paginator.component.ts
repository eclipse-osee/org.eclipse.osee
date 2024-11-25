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
	ChangeDetectionStrategy,
	Component,
	inject,
	input,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';

@Component({
	selector: 'osee-structure-table-paginator',
	imports: [MatPaginator],
	template: `<mat-paginator
		[pageSizeOptions]="[10, 15, 20, 25, 50, 75, 100, 200, 500]"
		[pageSize]="currentPageSize()"
		[pageIndex]="currentPage()"
		(page)="setPage($event)"
		[length]="structuresCount()"></mat-paginator>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StructureTablePaginatorComponent {
	structuresCount = input.required<number>();
	private structureService = inject(STRUCTURE_SERVICE_TOKEN);
	protected setPage(event: PageEvent) {
		this.structureService.pageSize = event.pageSize;
		this.structureService.page = event.pageIndex;
	}
	currentPageSize = toSignal(this.structureService.currentPageSize, {
		initialValue: 0,
	});
	currentPage = toSignal(this.structureService.currentPage, {
		initialValue: 0,
	});
}
