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
import { Component } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { map } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { ElementTableSearchComponent } from '../../forms/element-table-search/element-table-search.component';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import type {
	element,
	elementWithPathsAndButtons,
	PlatformType,
} from '@osee/messaging/shared/types';
import { HeaderService } from '@osee/messaging/shared/services';
import { DisplayTruncatedStringWithFieldOverflowPipe } from '@osee/shared/utils';

@Component({
	selector: 'osee-typesearch-element-table',
	templateUrl: './element-table.component.html',
	styleUrls: ['./element-table.component.sass'],
	standalone: true,
	imports: [
		ElementTableSearchComponent,
		MatTableModule,
		NgFor,
		NgIf,
		AsyncPipe,
		NgClass,
		DisplayTruncatedStringWithFieldOverflowPipe,
		RouterLink,
		MatMenuModule,
		MatButtonModule,
	],
})
export class ElementTableComponent {
	dataSource = new MatTableDataSource<element>();
	headers: Extract<keyof elementWithPathsAndButtons, string>[] = [
		'name',
		'platformType',
		'path',
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
