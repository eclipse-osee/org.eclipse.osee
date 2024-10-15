/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import type { MimChangeSummaryItem } from '@osee/messaging/shared/types';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { DiffReportTableComponent } from '../diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-diffs',
	templateUrl: './messaging-diffs.component.html',
	standalone: true,
	imports: [AsyncPipe, DiffReportTableComponent],
})
export class MessagingDiffsComponent {
	@Input({ required: true }) set items(value: MimChangeSummaryItem[]) {
		this.allItems.next(value);
	}
	@Input({ required: true }) title = '';
	@Input() showChildren = false;

	allItems = new BehaviorSubject<MimChangeSummaryItem[]>([]);

	itemsChanged = this.allItems.pipe(
		map((items) => items.filter((item) => !item.added && !item.deleted))
	);

	itemsAdded = this.allItems.pipe(
		map((items) => items.filter((item) => item.added))
	);

	itemsDeleted = this.allItems.pipe(
		map((items) => items.filter((item) => item.deleted))
	);
}
