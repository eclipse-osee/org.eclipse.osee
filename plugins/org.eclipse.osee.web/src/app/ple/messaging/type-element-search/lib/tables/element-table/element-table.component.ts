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
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatAnchor, MatButton } from '@angular/material/button';
import {
	MatMenu,
	MatMenuContent,
	MatMenuTrigger,
} from '@angular/material/menu';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { HeaderService } from '@osee/messaging/shared/services';
import type {
	PlatformType,
	element,
	elementWithPathsAndButtons,
} from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { DisplayTruncatedStringWithFieldOverflowPipe } from '@osee/shared/utils';
import { map } from 'rxjs/operators';
import { ElementTableSearchComponent } from '../../forms/element-table-search/element-table-search.component';
import { CurrentElementSearchService } from '../../services/current-element-search.service';

@Component({
	selector: 'osee-typesearch-element-table',
	templateUrl: './element-table.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgFor,
		NgIf,
		AsyncPipe,
		NgClass,
		RouterLink,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatAnchor,
		MatButton,
		MatMenu,
		MatMenuContent,
		MatMenuTrigger,
		ElementTableSearchComponent,
		DisplayTruncatedStringWithFieldOverflowPipe,
	],
})
export class ElementTableComponent {
	dataSource = new MatTableDataSource<element>();
	headers: Extract<keyof elementWithPathsAndButtons, string>[] = [
		'name',
		'platformType',
		'paths',
		'interfaceElementAlterable',
		'description',
		'notes',
	];
	branchType = this.uiService.type;
	branchId = this.uiService.id;
	constructor(
		private elementService: CurrentElementSearchService,
		private headerService: HeaderService,
		private uiService: UiService
	) {
		this.elementService.elements.subscribe((val) => {
			this.dataSource.data = val;
		});
	}
	valueTracker(index: any, item: any) {
		return index;
	}

	getHumanReadable(value: string) {
		return this.headerService
			.getHeaderByName(value, 'element')
			.pipe(map((v) => v.humanReadable));
	}

	isPlatformType(value: unknown): value is PlatformType {
		return (
			value !== null &&
			value !== undefined &&
			typeof value === 'object' &&
			'id' in value &&
			'name' in value &&
			typeof value.id === 'string' &&
			typeof value.name === 'string'
		);
	}
}
