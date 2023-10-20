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
import { Component, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { scriptDefHeaderDetails } from '../../../table-headers/script-def-headers';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UiService, HeaderService } from '@osee/shared/services';
import { SplitStringPipe } from '@osee/shared/utils';
import { TmoService } from '../../../services/tmo.service';
import type { SetReference } from '../../../types/tmo';
import type { DefReference } from '../../../types/tmo';

@Component({
	selector: 'osee-script-table',
	standalone: true,
	templateUrl: './script-table.component.html',
	imports: [
		CommonModule,
		FormsModule,
		MatButtonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatMenuModule,
		MatTableModule,
		MatTooltipModule,
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
