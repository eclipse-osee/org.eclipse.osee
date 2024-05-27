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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatToolbar } from '@angular/material/toolbar';
import { PlatformTypeActionsComponent } from '@osee/messaging/shared/main-content';
import { PlatformTypesFabComponent } from '../platform-types-fab/platform-types-fab.component';
import { PlMessagingTypesUIService } from '../services/pl-messaging-types-ui.service';
import { CurrentTypesService } from '../services/current-types.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { AttributeToValuePipe } from '@osee/attributes/pipes';

@Component({
	selector: 'osee-platform-types-toolbar',
	standalone: true,
	imports: [
		MatToolbar,
		MatPaginator,
		PlatformTypeActionsComponent,
		PlatformTypesFabComponent,
		AttributeToValuePipe,
	],
	template: `<mat-toolbar>
		@if (selection().id !== '-1' && selection().id !== '0') {
			{{ (selection().name | attributeToValue) + ' Selected ' }}
			<span class="tw-flex-auto"></span>
			<osee-platform-type-actions
				[typeData]="selection()"></osee-platform-type-actions>
		}
		<span class="tw-flex-auto"></span>
		<mat-paginator
			class="tw-w-1/3"
			[pageSizeOptions]="[
				10, 15, 20, 25, 50, 75, 100, 200, 250, 500, 1000, 1500, 2000,
				2500, 5000,
			]"
			[pageIndex]="currentPage()"
			(page)="setPage($event)"
			[length]="filteredDataSize()"
			[disabled]="false"></mat-paginator>
		<osee-platform-types-fab class="tw-pl-4"></osee-platform-types-fab>
	</mat-toolbar>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlatformTypesToolbarComponent {
	private uiService = inject(PlMessagingTypesUIService);
	private typesService = inject(CurrentTypesService);
	protected selection = this.uiService.selected;
	private filteredDataSizeObs = this.typesService.typeDataCount;
	protected filteredDataSize = toSignal(this.filteredDataSizeObs);
	private currentPageObs = this.typesService.currentPage;
	protected currentPage = toSignal(this.currentPageObs);
	setPage(event: PageEvent) {
		this.typesService.pageSize = event.pageSize;
		this.typesService.page = event.pageIndex;
	}
}
