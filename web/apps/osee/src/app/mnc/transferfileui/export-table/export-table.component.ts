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
import {
	Component,
	effect,
	ChangeDetectionStrategy,
	input,
	inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
	MatTableDataSource,
	MatColumnDef,
	MatHeaderCellDef,
	MatCellDef,
	MatTableModule,
} from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { TransferFileService } from '../../../mnc/services/transfer-file/transfer-file.service';
import { TransferData } from '../../types/transfer-file/transferdata';

@Component({
	selector: 'osee-export-table',
	imports: [
		CommonModule,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		MatSnackBarModule,
		MatDialogModule,
		MatColumnDef,
		MatHeaderCellDef,
		MatCellDef,
		MatTableModule,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './export-table.component.html',
})
export class ExportTableComponent {
	protected fileService = inject(TransferFileService);
	displayedColumns: string[] = ['source', 'date', 'file'];
	dataSource = new MatTableDataSource<TransferData>();
	id = new BehaviorSubject<string>('');
	errorMessage = '';
	exportedData = input.required<TransferData[]>();
	_updateDataSource = effect(
		() => {
			this.dataSource.data = this.exportedData();
		},
		{ allowSignalWrites: true }
	);
	download(selectedFile: string): void {
		if (selectedFile.length > 0) {
			this.fileService
				.downloadFile(selectedFile)
				.subscribe((response) => {
					const blob: Blob = response.body as Blob;
					const link = document.createElement('a');
					link.href = window.URL.createObjectURL(blob);
					link.download = selectedFile;
					link.click();
				});
		} else {
			// pop up error message
			this.errorMessage = 'Unable to download file: ' + selectedFile;
		}
	}
}
