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
import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { DiffReportService } from '@osee/messaging/shared/services';
import type { messageDiffItem } from '@osee/messaging/shared/types';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { messageDiffHeaderDetails } from '../../table-headers/message-diff-table-headers';
import { DiffReportTableComponent } from '../../tables/diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-message-diffs',
	templateUrl: './message-diffs.component.html',
	styleUrls: ['./message-diffs.component.sass'],
	standalone: true,
	imports: [NgIf, AsyncPipe, DiffReportTableComponent],
})
export class MessageDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headerDetails = messageDiffHeaderDetails;
	headers: (keyof messageDiffItem)[] = [
		'name',
		'description',
		'interfaceMessageNumber',
		'interfaceMessagePeriodicity',
		'interfaceMessageRate',
		'interfaceMessageWriteAccess',
		'interfaceMessageType',
		'applicability',
	];

	allMessages = this.diffReportService.messages;

	messagesChanged = this.allMessages.pipe(
		switchMap((messages) =>
			from(messages).pipe(
				filter(
					(message) =>
						!message.diffInfo?.added && !message.diffInfo?.deleted
				),
				reduce((acc, curr) => [...acc, curr], [] as messageDiffItem[])
			)
		)
	);

	messagesAdded = this.allMessages.pipe(
		switchMap((messages) =>
			from(messages).pipe(
				filter((message) => message.diffInfo?.added === true),
				reduce((acc, curr) => [...acc, curr], [] as messageDiffItem[])
			)
		)
	);

	messagesDeleted = this.allMessages.pipe(
		switchMap((messages) =>
			from(messages).pipe(
				filter((message) => message.diffInfo?.deleted === true),
				reduce((acc, curr) => [...acc, curr], [] as messageDiffItem[])
			)
		)
	);
}
