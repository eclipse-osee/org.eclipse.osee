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
import { crossReferenceHeaderDetails } from '@osee/messaging/shared/table-headers';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { filter, switchMap, take, tap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UiService, HeaderService } from '@osee/shared/services';
import { NewCrossReferenceDialogComponent } from '../new-cross-reference-dialog/new-cross-reference-dialog.component';
import { SplitStringPipe } from '@osee/shared/utils';
import type { CrossReference } from '@osee/messaging/shared/types';
import { TableEditTextFieldComponent } from '@osee/messaging/shared/forms';
import { CrossReferenceService } from '@osee/messaging/shared/services';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-cross-reference-table',
	standalone: true,
	templateUrl: './cross-reference-table.component.html',
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
		NewCrossReferenceDialogComponent,
		TableEditTextFieldComponent,
		SplitStringPipe,
		ApplicabilitySelectorComponent,
	],
})
export class CrossReferenceTableComponent implements OnDestroy {
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;

	constructor(
		private headerService: HeaderService,
		private crossRefService: CrossReferenceService,
		private ui: UiService,
		public dialog: MatDialog
	) {}

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
			.pipe(tap((res) => (this.ui.updated = true)));
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
		this.matMenuTrigger.menuData = {
			crossRef: crossRef,
		};
		this.matMenuTrigger.openMenu();
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
