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
import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { HeaderService } from 'src/app/ple-services/ui/header.service';
import { headerDetail } from '@osee/shared/types';

@Component({
	selector: 'osee-messaging-diff-report-table',
	templateUrl: './diff-report-table.component.html',
	styleUrls: ['./diff-report-table.component.scss'],
	standalone: true,
	imports: [NgIf, NgFor, AsyncPipe, NgClass, MatTableModule, MatIconModule],
})
export class DiffReportTableComponent<T extends { [key: string]: any }> {
	@Input() items: T[] = [];
	@Input() title: string = '';
	@Input() headerDetails: headerDetail<T>[] = [];
	@Input() headers: string[] = [];

	constructor(private headerService: HeaderService) {}

	getHeaderByName(header: string) {
		return this.headerService.getHeaderByName(this.headerDetails, header);
	}
}
