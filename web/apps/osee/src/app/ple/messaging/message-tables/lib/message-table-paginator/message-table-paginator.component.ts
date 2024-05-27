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
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MessageUiService } from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-message-table-paginator',
	standalone: true,
	imports: [MatPaginator],
	template: `<mat-paginator
		[pageSizeOptions]="[10, 15, 20, 25, 50, 75, 100, 200, 500]"
		[pageSize]="currentPageSize()"
		[pageIndex]="currentPage()"
		(page)="setPage($event)"
		[length]="messagesCount()"></mat-paginator>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessageTablePaginatorComponent {
	messagesCount = input.required<number>();
	private messageService = inject(MessageUiService);
	protected setPage(event: PageEvent) {
		this.messageService.currentPageSize.set(event.pageSize);
		this.messageService.currentPage.set(event.pageIndex);
	}
	currentPageSize = this.messageService.currentPageSize;
	currentPage = this.messageService.currentPage;
}
