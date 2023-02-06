/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { subMessage, message } from '@osee/messaging/shared';

@Component({
	selector: 'osee-messaging-sub-message-table',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockSubMessageTableComponent {
	@Input() data!: subMessage[];
	@Input() dataSource!: MatTableDataSource<subMessage>;
	@Input() filter!: string;

	@Input() element!: message;
	@Input() editMode!: boolean;
	@Output() expandRow: EventEmitter<boolean> = new EventEmitter();
}
