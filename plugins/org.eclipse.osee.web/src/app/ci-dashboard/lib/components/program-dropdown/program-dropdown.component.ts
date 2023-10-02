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
import { scriptDefHeaderDetails } from '../../table-headers/script-def-headers';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import {
	BehaviorSubject,
	filter,
	of,
	switchMap,
	take,
	tap,
	combineLatest,
} from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UiService, HeaderService } from '@osee/shared/services';
import { SplitStringPipe } from '@osee/shared/utils';
import { TmoService } from '../../services/tmo.service';
import type { ProgramReference } from '../../types/tmo';

@Component({
	selector: 'osee-program-dropdown',
	standalone: true,
	templateUrl: './program-dropdown.component.html',
	imports: [
		CommonModule,
		FormsModule,
		MatAutocompleteModule,
		MatButtonModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatMenuModule,
		MatTableModule,
		MatTooltipModule,
	],
})
export class ProgramDropdownComponent {
	constructor(
		private tmoService: TmoService,
		private headerService: HeaderService,
		private ui: UiService,
		public dialog: MatDialog
	) {}

	programs = this.tmoService.programs;

	filterText = new BehaviorSubject<string>('');

	noneOption = { name: 'None' } as ProgramReference;

	selectedProgram = combineLatest([
		this.programs,
		this.tmoService.programId,
	]).pipe(
		switchMap(([programs, programId]) => {
			const program = programs.find((v) => v.name === programId);
			return program ? of(program) : of(this.noneOption);
		})
	);

	selectProgram(program: ProgramReference) {
		this.tmoService.ProgramId = program.name;
	}

	applyFilter(text: Event) {
		const value = (text.target as HTMLInputElement).value;
		this.filterText.next(value);
	}
}
