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
import { AsyncPipe, DatePipe, NgClass } from '@angular/common';
import { Component, Input, viewChild, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
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
import { HeaderService } from '@osee/shared/services';
import { of, switchMap, take } from 'rxjs';
import { MergeManagerEditorDialogComponent } from './merge-manager-editor-dialog/merge-manager-editor-dialog.component';
import { mergeManagerHeaderDetails } from './merge-manager-table-headers';
import { CommitBranchService } from '@osee/commit/services';
import { attrMergeData, mergeData } from '@osee/commit/types';

@Component({
	selector: 'osee-merge-manager-table',
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		DatePipe,
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
		MatMenu,
		MatMenuContent,
		MatMenuItem,
		MatIcon,
		MatMenuTrigger,
	],
	templateUrl: './merge-manager-table.component.html',
})
export class MergeManagerTableComponent {
	dialog = inject(MatDialog);
	private headerService = inject(HeaderService);
	private commitBranchService = inject(CommitBranchService);

	@Input({ required: true }) mergeData!: mergeData[];
	@Input({ required: true }) mergeBranchId!: string;
	@Input({ required: true }) branchId!: string;
	@Input({ required: true }) destBranchId!: string;

	matMenuTrigger = viewChild.required(MatMenuTrigger);

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

	selectSourceValue(data: mergeData) {
		data.attrMergeData.mergeValue = data.attrMergeData.sourceValue;
		data.conflictStatus = 'RESOLVED';
		this.commitBranchService
			.updateMergeConflicts(
				data,
				this.mergeBranchId,
				this.branchId,
				this.destBranchId
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
				this.destBranchId
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
				this.destBranchId
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
						this.destBranchId
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
		this.matMenuTrigger().menuData = {
			data: data,
		};
		this.matMenuTrigger().openMenu();
	}
}
