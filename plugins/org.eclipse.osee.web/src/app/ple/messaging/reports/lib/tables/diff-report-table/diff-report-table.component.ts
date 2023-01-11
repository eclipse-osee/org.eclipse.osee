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
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { HeaderService } from '../../../../shared/services/ui/header.service';
import {
	diffItem,
	diffItemKey,
} from '../../../../shared/types/DifferenceReport';

@Component({
	selector: 'osee-messaging-diff-report-table',
	templateUrl: './diff-report-table.component.html',
	styleUrls: ['./diff-report-table.component.sass'],
	standalone: true,
	imports: [NgIf, NgFor, AsyncPipe, NgClass, MatTableModule, MatIconModule],
})
export class DiffReportTableComponent {
	@Input() items: diffItem[] = [];
	@Input() title: string = '';
	@Input() headers: diffItemKey[] = [];
	@Input() headerKey: string = '';

	constructor(private headerService: HeaderService) {}

	getHeaderByName(value: diffItemKey) {
		return this.headerService.getHeaderByName(value, this.headerKey);
	}
}
