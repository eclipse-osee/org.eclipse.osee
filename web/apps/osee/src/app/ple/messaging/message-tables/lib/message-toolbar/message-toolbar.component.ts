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
import { Component, input } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MessageTablePaginatorComponent } from '../message-table-paginator/message-table-paginator.component';
import { MessageTableToolbarAddActionsComponent } from '../message-table-toolbar-add-actions/message-table-toolbar-add-actions.component';

@Component({
	selector: 'osee-message-toolbar',
	standalone: true,
	imports: [
		MatToolbar,
		MessageTablePaginatorComponent,
		MessageTableToolbarAddActionsComponent,
	],
	template: ` <mat-toolbar
		><span class="tw-flex-auto"></span
		><osee-message-table-paginator
			[messagesCount]="
				messagesCount()
			" /><osee-message-table-toolbar-add-actions
	/></mat-toolbar>`,
})
export class MessageToolbarComponent {
	messagesCount = input<number>(0);
}
