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
import { Component, Input, ViewChild } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { attrMergeData, mergeData } from '@osee/shared/types';
import { CommitBranchService, HeaderService } from '@osee/shared/services';
import { mergeManagerHeaderDetails } from './merge-manager-table-headers';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AsyncPipe, DatePipe, NgClass } from '@angular/common';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MergeManagerEditorDialogComponent } from './merge-manager-editor-dialog/merge-manager-editor-dialog.component';
import { of, switchMap, take } from 'rxjs';

@Component({
	selector: 'osee-merge-manager-table',
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		DatePipe,
		MatTableModule,
		MatTooltipModule,
		MatMenuModule,
		MatIconModule,
	],
	templateUrl: './merge-manager-table.component.html',
})
export class MergeManagerTableComponent {
	@Input({ required: true }) mergeData!: mergeData[];
	@Input({ required: true }) mergeBranchId!: string;
	@Input({ required: true }) branchId!: string;
	@Input({ required: true }) parentBranchId!: string;

	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;

	headers: (keyof mergeData | keyof attrMergeData)[] = [
		'name',
		'conflictType',
		'attrTypeName',
		'sourceValue',
		'mergeValue',
		'destValue',
	];

	menuPosition = {
		x: '0',
		y: '0',
	};

	constructor(
		public dialog: MatDialog,
		private headerService: HeaderService,
		private commitBranchService: CommitBranchService
	) {}

	selectSourceValue(data: mergeData) {
		data.attrMergeData.mergeValue = data.attrMergeData.sourceValue;
		data.conflictStatus = 'RESOLVED';
		this.commitBranchService
			.updateMergeConflicts(
				data,
				this.mergeBranchId,
				this.branchId,
				this.parentBranchId
			)
			.subscribe();
	}

	selectDestValue(data: mergeData) {
		data.attrMergeData.mergeValue = data.attrMergeData.destValue;
		data.conflictStatus = 'RESOLVED';
		this.commitBranchService
			.updateMergeConflicts(
				data,
				this.mergeBranchId,
				this.branchId,
				this.parentBranchId
			)
			.subscribe();
	}

	resetMergeValue(data: mergeData) {
		data.attrMergeData.mergeValue = data.attrMergeData.sourceValue;
		data.conflictStatus = 'UNTOUCHED';
		this.commitBranchService
			.updateMergeConflicts(
				data,
				this.mergeBranchId,
				this.branchId,
				this.parentBranchId
			)
			.subscribe();
	}

	openEditDialog(data: mergeData) {
		this.dialog
			.open(MergeManagerEditorDialogComponent, {
				minWidth: '50%',
				data: { ...structuredClone(data), conflictStatus: 'RESOLVED' },
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((updatedData) => {
					if (
						updatedData === undefined ||
						updatedData === null ||
						updatedData == '' ||
						updatedData.attrMergeData.mergeValue ===
							data.attrMergeData.mergeValue
					) {
						return of();
					}
					return this.commitBranchService.updateMergeConflicts(
						updatedData,
						this.mergeBranchId,
						this.branchId,
						this.parentBranchId
					);
				})
			)
			.subscribe();
	}

	getTableHeaderByName(header: keyof mergeData | keyof attrMergeData) {
		return this.headerService.getHeaderByName(
			mergeManagerHeaderDetails,
			header
		);
	}

	openContextMenu(event: MouseEvent, data: mergeData) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			data: data,
		};
		this.matMenuTrigger.openMenu();
	}
}
