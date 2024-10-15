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
	MatTableDataSource,
} from '@angular/material/table';
import { CiSetsService } from '../../../services/ci-sets.service';
import { CISet } from '../../../types/tmo';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { applicabilitySentinel } from '@osee/applicability/types';
import { FormsModule } from '@angular/forms';
import { PersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import {
	MatButton,
	MatFabButton,
	MatIconButton,
	MatMiniFabButton,
} from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CreateCiSetDialogComponent } from './create-ci-set-dialog/create-ci-set-dialog.component';
import { filter, first, switchMap } from 'rxjs';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

@Component({
	selector: 'osee-ci-sets-table',
	standalone: true,
	imports: [
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
		PersistedStringAttributeInputComponent,
		PersistedBooleanAttributeToggleComponent,
	],
	templateUrl: './ci-sets-table.component.html',
})
export class CiSetsTableComponent {
	private ciSetService = inject(CiSetsService);
	private dialog = inject(MatDialog);

	datasource = new MatTableDataSource<CISet>([]);

	applic = applicabilitySentinel;
	headers = ['Name', 'Active', ' '];

	filter = signal('');
	private _filterEffect = effect(() => {
		this.datasource.filterPredicate = this.filterPredicate;
		this.datasource.filter = this.filter();
	});

	ciSets = toSignal(
		this.ciSetService.adminCiSets.pipe(takeUntilDestroyed()),
		{
			initialValue: [],
		}
	);
	private _ciSetsEffect = effect(
		() => (this.datasource.data = this.ciSets())
	);

	filterPredicate(data: CISet, filter: string) {
		const filterLower = filter.toLowerCase();
		return (
			data.name.value.toLowerCase().includes(filterLower) ||
			data.active.value.toString().toLowerCase().includes(filterLower)
		);
	}

	openNewCiSetDialog() {
		const dialogData: CISet = {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				gammaId: '-1',
				typeId: '1152921504606847088',
				value: '',
			},
			active: {
				id: '-1',
				gammaId: '-1',
				typeId: '1152921504606847065',
				value: false,
			},
		};
		const dialogRef = this.dialog.open(CreateCiSetDialogComponent, {
			data: dialogData,
			minWidth: '80vw',
		});
		dialogRef
			.afterClosed()
			.pipe(
				first(),
				filter((val) => val !== undefined),
				switchMap((val) => this.ciSetService.createCISet(val))
			)
			.subscribe();
	}

	deleteCISet(ciSet: CISet) {
		this.ciSetService.deleteCISet(ciSet);
	}
}
