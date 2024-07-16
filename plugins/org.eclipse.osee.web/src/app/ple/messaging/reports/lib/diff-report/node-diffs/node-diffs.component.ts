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
import type { nodeDiffItem } from '@osee/messaging/shared/types';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { nodeDiffHeaderDetails } from './node-diff-table-headers';
import { DiffReportTableComponent } from '../diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-node-diffs',
	templateUrl: './node-diffs.component.html',
	standalone: true,
	imports: [NgIf, AsyncPipe, DiffReportTableComponent],
})
export class NodeDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headerDetails = nodeDiffHeaderDetails;
	headers: (keyof nodeDiffItem)[] = [
		'name',
		'description',
		'address',
		'color',
		'applicability',
	];

	allNodes = this.diffReportService.nodes;

	nodesChanged = this.allNodes.pipe(
		switchMap((nodes) =>
			from(nodes).pipe(
				filter(
					(node) => !node.diffInfo?.added && !node.diffInfo?.deleted
				),
				reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
			)
		)
	);

	nodesAdded = this.allNodes.pipe(
		switchMap((nodes) =>
			from(nodes).pipe(
				filter((node) => node.diffInfo?.added === true),
				reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
			)
		)
	);

	nodesDeleted = this.allNodes.pipe(
		switchMap((nodes) =>
			from(nodes).pipe(
				filter((node) => node.diffInfo?.deleted === true),
				reduce((acc, curr) => [...acc, curr], [] as nodeDiffItem[])
			)
		)
	);
}
