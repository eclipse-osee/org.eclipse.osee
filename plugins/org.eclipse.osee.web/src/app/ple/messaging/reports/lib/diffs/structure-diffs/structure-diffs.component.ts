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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { from } from 'rxjs';
import { filter, reduce, switchMap } from 'rxjs/operators';
import { DiffReportService } from '../../../../shared/services/ui/diff-report.service';
import {
	DiffHeaderType,
	elementDiffItem,
	structureDiffItem,
} from '../../../../shared/types/DifferenceReport.d';
import { DiffReportTableComponent } from '../../tables/diff-report-table/diff-report-table.component';

@Component({
	selector: 'osee-messaging-structure-diffs',
	templateUrl: './structure-diffs.component.html',
	styleUrls: ['./structure-diffs.component.sass'],
	standalone: true,
	imports: [NgIf, AsyncPipe, NgFor, DiffReportTableComponent],
})
export class StructureDiffsComponent {
	constructor(private diffReportService: DiffReportService) {}

	headers: (keyof structureDiffItem)[] = [
		'name',
		'description',
		'interfaceMinSimultaneity',
		'interfaceMaxSimultaneity',
		'interfaceTaskFileType',
		'interfaceStructureCategory',
	];

	elementHeaders: (keyof elementDiffItem)[] = [
		'name',
		'description',
		'logicalType',
		'interfaceElementIndexStart',
		'interfaceElementIndexEnd',
		'elementSizeInBits',
		'interfacePlatformTypeMinval',
		'interfacePlatformTypeMaxval',
		'interfaceDefaultValue',
		'units',
		'enumeration',
		'interfaceElementAlterable',
		'enumLiteral',
		'notes',
		'applicability',
	];

	headerType = DiffHeaderType.STRUCTURE;
	elementHeaderType = DiffHeaderType.ELEMENT;

	allStructures = this.diffReportService.structuresWithElements;

	structuresChanged = this.allStructures.pipe(
		switchMap((structures) =>
			from(structures).pipe(
				filter(
					(structure) =>
						!structure.diffInfo?.added &&
						!structure.diffInfo?.deleted
				),
				reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
			)
		)
	);

	structuresAdded = this.allStructures.pipe(
		switchMap((structures) =>
			from(structures).pipe(
				filter((structure) => structure.diffInfo?.added === true),
				reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
			)
		)
	);

	structuresDeleted = this.allStructures.pipe(
		switchMap((structures) =>
			from(structures).pipe(
				filter((structure) => structure.diffInfo?.deleted === true),
				reduce((acc, curr) => [...acc, curr], [] as structureDiffItem[])
			)
		)
	);

	getArrayLength(arr: structureDiffItem[]) {
		return [...Array(arr.length).keys()];
	}
}
