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
import { DiffReportService } from '@osee/messaging/shared';
import type { connectionDiffItem } from '@osee/messaging/shared';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { connectionDiffHeaderDetails } from '../../table-headers/connection-diff-table-headers';
import { DiffReportTableComponent } from '../../tables/diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-connection-diffs',
	templateUrl: './connection-diffs.component.html',
	styleUrls: ['./connection-diffs.component.sass'],
	standalone: true,
	imports: [NgIf, AsyncPipe, DiffReportTableComponent],
})
export class ConnectionDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headerDetails = connectionDiffHeaderDetails;
	headers: (keyof connectionDiffItem)[] = [
		'name',
		'description',
		'transportType',
		'applicability',
	];

	allConnections = this.diffReportService.connections;

	connectionsChanged = this.allConnections.pipe(
		switchMap((connections) =>
			from(connections).pipe(
				filter(
					(connection) =>
						!connection.diffInfo?.added &&
						!connection.diffInfo?.deleted
				),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as connectionDiffItem[]
				)
			)
		)
	);

	connectionsAdded = this.allConnections.pipe(
		switchMap((connections) =>
			from(connections).pipe(
				filter((connection) => connection.diffInfo?.added === true),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as connectionDiffItem[]
				)
			)
		)
	);

	connectionsDeleted = this.allConnections.pipe(
		switchMap((connections) =>
			from(connections).pipe(
				filter((connection) => connection.diffInfo?.deleted === true),
				reduce(
					(acc, curr) => [...acc, curr],
					[] as connectionDiffItem[]
				)
			)
		)
	);
}
