/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
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
} from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { headerDetail } from '@osee/shared/types';
import { applic } from '@osee/shared/types/applicability';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';

@Component({
	selector: 'osee-import-table',
	standalone: true,
	imports: [
		AsyncPipe,
		HighlightFilteredTextDirective,
		NgClass,
		NgIf,
		NgFor,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
	templateUrl: './import-table.component.html',
})
export class ImportTableComponent<T extends { [key: string]: any }>
	implements OnChanges
{
	@Input() data: T[] = [];
	@Input() headers: string[] = [];
	@Input() headerDetails: headerDetail<T>[] = [];
	@Input() tableTitle: string = '';

	filteredData: T[] = [];
	filterText: string = '';
	showTableContents: boolean = false;

	constructor(private headerService: HeaderService) {}

	ngOnChanges(changes: SimpleChanges) {
		this.filteredData = this.data;
	}

	getTableHeaderByName(header: string) {
		return this.headerService.getHeaderByName(this.headerDetails, header);
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filterText = filterValue;
		if (filterValue === '') {
			this.filteredData = this.data;
			return;
		}
		this.filteredData = [];
		this.data.forEach((d) => {
			this.headers.forEach((header) => {
				if (header === 'applicability') {
					if (
						(d[header] as applic).name
							.toLowerCase()
							.includes(filterValue.toLowerCase())
					) {
						this.filteredData = [...this.filteredData, d];
					}
				} else {
					const val = '' + d[header];
					if (val.toLowerCase().includes(filterValue.toLowerCase())) {
						this.filteredData = [...this.filteredData, d];
					}
				}
			});
		});
	}

	toggleTableContents() {
		this.showTableContents = !this.showTableContents;
	}
}
