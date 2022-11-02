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
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { map } from 'rxjs/operators';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { elementWithPathsAndButtons } from '../../../shared/types/element';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { element } from '../../types/element';

@Component({
	selector: 'osee-typesearch-element-table',
	templateUrl: './element-table.component.html',
	styleUrls: ['./element-table.component.sass'],
})
export class ElementTableComponent {
	dataSource = new MatTableDataSource<element>();
	headers: Extract<keyof elementWithPathsAndButtons, string>[] = [
		'name',
		'platformTypeName2',
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
}
