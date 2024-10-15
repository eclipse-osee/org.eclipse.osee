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
import { AsyncPipe } from '@angular/common';
import { Component, OnDestroy, viewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
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
import { TableEditTextFieldComponent } from '@osee/messaging/shared/forms';
import { CrossReferenceService } from '@osee/messaging/shared/services';
import { crossReferenceHeaderDetails } from '@osee/messaging/shared/table-headers';
import type { CrossReference } from '@osee/messaging/shared/types';
import { HeaderService, UiService } from '@osee/shared/services';
import { SplitStringPipe } from '@osee/shared/utils';
import { filter, switchMap, take, tap } from 'rxjs';
import { NewCrossReferenceDialogComponent } from '../new-cross-reference-dialog/new-cross-reference-dialog.component';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';

@Component({
	selector: 'osee-cross-reference-table',
	standalone: true,
	templateUrl: './cross-reference-table.component.html',
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatMenu,
		MatMenuContent,
		MatMenuItem,
		MatMenuTrigger,
		MatRow,
		MatRowDef,
		MatTooltip,
		NewCrossReferenceDialogComponent,
		TableEditTextFieldComponent,
		SplitStringPipe,
		ApplicabilityDropdownComponent,
	],
})
export class CrossReferenceTableComponent implements OnDestroy {
	private headerService = inject(HeaderService);
	private crossRefService = inject(CrossReferenceService);
	private ui = inject(UiService);
	dialog = inject(MatDialog);

	matMenuTrigger = viewChild.required(MatMenuTrigger);

	getTableHeaderByName(header: keyof CrossReference) {
		return this.headerService.getHeaderByName(
			crossReferenceHeaderDetails,
			header
		);
	}

	data = this.crossRefService.crossReferences;

	inEditMode = this.crossRefService.inEditMode;

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.FilterValue = filterValue;
	}

	updateCrossReference<U extends keyof CrossReference>(
		crossRef: CrossReference,
		header: keyof CrossReference,
		newValue: CrossReference[U]
	) {
		this.crossRefService.updateCrossReferenceAttribute(
			crossRef,
			header,
			newValue
		);
	}

	deleteCrossReference(crossRef: CrossReference) {
		const del = this.crossRefService
			.deleteCrossReference(crossRef)
			.pipe(tap((_) => (this.ui.updated = true)));
		del.subscribe();
	}

	menuPosition = {
		x: '0',
		y: '0',
	};

	openMenu(event: MouseEvent, crossRef: CrossReference) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger().menuData = {
			crossRef: crossRef,
		};
		this.matMenuTrigger().openMenu();
	}

	openEditDialog(crossRef: CrossReference) {
		this.dialog
			.open(NewCrossReferenceDialogComponent, {
				data: { crossRef: crossRef },
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is CrossReference => value !== undefined),
				switchMap((crossRef) =>
					this.crossRefService
						.updateCrossReference(crossRef)
						.pipe(tap((_) => (this.ui.updated = true)))
				)
			)
			.subscribe();
	}

	updateCrossRef = this.crossRefService.updateCrossReference;

	headers: (keyof CrossReference)[] = [
		'name',
		'crossReferenceValue',
		'crossReferenceAdditionalContent',
		'crossReferenceArrayValues',
		'applicability',
	];

	ngOnDestroy(): void {
		this.FilterValue = '';
	}

	get filterValue() {
		return this.crossRefService.filterValue;
	}

	set FilterValue(value: string) {
		this.crossRefService.FilterValue = value;
	}
}
