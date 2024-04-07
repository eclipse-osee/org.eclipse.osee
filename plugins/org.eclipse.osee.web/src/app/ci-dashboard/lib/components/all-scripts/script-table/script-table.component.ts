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
import { CommonModule } from '@angular/common';
import { Component, OnDestroy, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
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
import { MatTooltip } from '@angular/material/tooltip';
import { HeaderService, UiService } from '@osee/shared/services';
import { SplitStringPipe } from '@osee/shared/utils';
import { TmoService } from '../../../services/tmo.service';
import { scriptDefHeaderDetails } from '../../../table-headers/script-def-headers';
import type { DefReference, SetReference } from '../../../types/tmo';

@Component({
	selector: 'osee-script-table',
	standalone: true,
	templateUrl: './script-table.component.html',
	imports: [
		CommonModule,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		SplitStringPipe,
	],
})
export class ScriptTableComponent implements OnDestroy {
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;

	noneOption = { name: 'None' } as SetReference;

	scriptDefs = this.tmoService.scriptDefs;

	constructor(
		private tmoService: TmoService,
		private headerService: HeaderService,
		private ui: UiService,
		public dialog: MatDialog
	) {}

	getTableHeaderByName(header: keyof DefReference) {
		return this.headerService.getHeaderByName(
			scriptDefHeaderDetails,
			header
		);
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.FilterValue = filterValue;
	}

	menuPosition = {
		x: '0',
		y: '0',
	};

	openMenu(event: MouseEvent, defRef: DefReference) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			defRef: defRef,
		};
		this.matMenuTrigger.openMenu();
	}

	headers: (keyof DefReference)[] = [
		'name',
		'team',
		'subsystem',
		'safety',
		'notes',
		'statusBy',
		'statusDate',
		'latestResult',
		'latestScriptHealth',
		'latestPassedCount',
		'latestFailedCount',
		'latestScriptAborted',
		'machineName',
		'latestMachineName',
		'latestElapsedTime',
		'scheduledMachine',
		'scheduledTime',
		'scheduled',
		'fullScriptName',
	];

	ngOnDestroy(): void {
		this.FilterValue = '';
	}

	get filterValue() {
		return this.tmoService.filterValue;
	}

	set FilterValue(value: string) {
		this.tmoService.FilterValue = value;
	}
}
