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
import type { submessageDiffItem } from '@osee/messaging/shared/types';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { submessageDiffHeaderDetails } from '../../table-headers/submessage-diff-table-headers';
import { DiffReportTableComponent } from '../../tables/diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-submessage-diffs',
	templateUrl: './submessage-diffs.component.html',
	standalone: true,
	imports: [NgIf, AsyncPipe, DiffReportTableComponent],
})
export class SubmessageDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headerDetails = submessageDiffHeaderDetails;
	headers: (keyof submessageDiffItem)[] = [
		'name',
		'description',
		'interfaceSubMessageNumber',
		'applicability',
	];

	allSubMessages = this.diffReportService.submessages;

	subMessagesChanged = this.allSubMessages.pipe(
		switchMap((submessages) =>
			from(submessages).pipe(
				filter(
					(submessage) =>
						!submessage.diffInfo?.added &&
						!submessage.diffInfo?.deleted
				),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as submessageDiffItem[]
				)
			)
		)
	);

	subMessagesAdded = this.allSubMessages.pipe(
		switchMap((submessages) =>
			from(submessages).pipe(
				filter((submessage) => submessage.diffInfo?.added === true),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as submessageDiffItem[]
				)
			)
		)
	);

	subMessagesDeleted = this.allSubMessages.pipe(
		switchMap((submessages) =>
			from(submessages).pipe(
				filter((submessage) => submessage.diffInfo?.deleted === true),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as submessageDiffItem[]
				)
			)
		)
	);
}
