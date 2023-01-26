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
import { Component, Input } from '@angular/core';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import {
	HeaderKeys,
	HeaderKeysEnum,
	HeaderService,
} from 'src/app/ple/messaging/shared/services/ui/header.service';
import { MatTableModule } from '@angular/material/table';

@Component({
	selector: 'osee-import-table',
	standalone: true,
	imports: [AsyncPipe, NgClass, NgIf, NgFor, MatTableModule],
	templateUrl: './import-table.component.html',
	styleUrls: ['./import-table.component.scss'],
})
export class ImportTableComponent<T> {
	@Input() data: T[] = [];
	@Input() headers: string[] = [];
	@Input() headerKey: HeaderKeys = HeaderKeysEnum.NONE;
	@Input() tableTitle: string = '';

	showTableContents: boolean = false;

	constructor(private headerService: HeaderService) {}

	getTableHeaderByName(header: string) {
		return this.headerService.getTableHeaderByName(header, this.headerKey);
	}

	toggleTableContents() {
		this.showTableContents = !this.showTableContents;
	}
}
