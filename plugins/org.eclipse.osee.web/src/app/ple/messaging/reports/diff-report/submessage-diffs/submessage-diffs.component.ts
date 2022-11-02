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
import { Component, OnInit } from '@angular/core';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { DiffReportService } from '../../../shared/services/ui/diff-report.service';
import {
	DiffHeaderType,
	submessageDiffItem,
} from '../../../shared/types/DifferenceReport.d';

@Component({
	selector: 'osee-messaging-submessage-diffs',
	templateUrl: './submessage-diffs.component.html',
	styleUrls: ['./submessage-diffs.component.sass'],
})
export class SubmessageDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headers: (keyof submessageDiffItem)[] = [
		'name',
		'description',
		'interfaceSubMessageNumber',
		'applicability',
	];

	headerType = DiffHeaderType.SUBMESSAGE;

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
