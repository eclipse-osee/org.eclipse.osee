/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, effect, inject, signal } from '@angular/core';
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
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { applicabilitySentinel } from '@osee/applicability/types';
import { FormsModule } from '@angular/forms';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import {
	MatButton,
	MatFabButton,
	MatIconButton,
	MatMiniFabButton,
} from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { CurrentUnitsService } from '@osee/messaging/units/services';
import { unit } from '../../types/units';
import { AsyncPipe } from '@angular/common';
import { MatToolbar } from '@angular/material/toolbar';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { CreateUnitDialogComponent } from './create-unit-dialog/create-unit-dialog.component';
import { filter, first, switchMap } from 'rxjs';

@Component({
	selector: 'osee-units-table',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatCell,
		MatCellDef,
		MatRow,
		MatRowDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatTooltip,
		MatIcon,
		MatButton,
		MatIconButton,
		MatFabButton,
		MatMiniFabButton,
		MatFormField,
		MatInput,
		MatToolbar,
		MatPaginator,
		PersistedStringAttributeInputComponent,
	],
	templateUrl: './units-table.component.html',
})
export class UnitsTableComponent {
	private _currentUnitsService = inject(CurrentUnitsService);
	private dialog = inject(MatDialog);

	units = this._currentUnitsService.current;
	unitsCount = this._currentUnitsService.count;
	unitsPageSize = this._currentUnitsService.currentPageSize;
	unitsPageIndex = this._currentUnitsService.currentPage;

	headers = ['Name', 'Measurement', ' '];

	filter = signal('');
	private _filterEffect = effect(
		() => (this._currentUnitsService.filter = this.filter())
	);

	setPage(event: PageEvent) {
		this._currentUnitsService.pageSize = event.pageSize;
		this._currentUnitsService.page = event.pageIndex;
	}

	deleteUnit(unit: unit) {
		this._currentUnitsService.deleteUnit(unit);
	}

	openNewUnitDialog() {
		const dialogData: unit = {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				gammaId: '-1',
				typeId: '1152921504606847088',
				value: '',
			},
			measurement: {
				id: '-1',
				gammaId: '-1',
				typeId: '2478822847543373494',
				value: '',
			},
			applicability: applicabilitySentinel,
		};
		const dialogRef = this.dialog.open(CreateUnitDialogComponent, {
			data: dialogData,
			minWidth: '80vw',
		});
		dialogRef
			.afterClosed()
			.pipe(
				first(),
				filter((val) => val !== undefined),
				switchMap((val) => this._currentUnitsService.createUnit(val))
			)
			.subscribe();
	}
}
