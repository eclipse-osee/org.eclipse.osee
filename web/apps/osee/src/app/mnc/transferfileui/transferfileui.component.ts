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
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {
	MatCellDef,
	MatColumnDef,
	MatHeaderCellDef,
	MatTableModule,
} from '@angular/material/table';
import { UiService } from '@osee/shared/services';
import { BehaviorSubject, map } from 'rxjs';
import { TransferFileService } from '../../mnc/services/transfer-file/transfer-file.service';
import { ExportTableComponent } from '../../mnc/transferfileui/export-table/export-table.component';
import { GenerateExportComponent } from '../../mnc/transferfileui/generate-export/generate-export.component';
import { TransferData } from '../types/transfer-file/transferdata';
@Component({
	selector: 'osee-transferfile',
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './transferfileui.component.html',
	imports: [
		AsyncPipe,
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
		ExportTableComponent,
		GenerateExportComponent,
	],
})
export class TransferfileuiComponent {
	protected fileService = inject(TransferFileService);
	protected uiService = inject(UiService);
	exportFilter = this.fileService.exportId;
	id = new BehaviorSubject<string>('');
	_exportedData = this.fileService.exportData.pipe(
		map((data) => {
			const tempData: TransferData[] = [];
			if (data.tables.length !== 0) {
				data.tables[0].rows.forEach((row) => {
					const tempObj: TransferData = {
						source: '',
						date: '',
						file: '',
					};
					row.values.forEach((cell, index) => {
						if (index === 0) {
							tempObj.source = cell;
						}
						if (index === 1) {
							tempObj.date = cell;
						}
						if (index === 2) {
							tempObj.file = cell;
						}
					});
					tempData.push(tempObj);
				});
			}
			return tempData;
		}),
		takeUntilDestroyed()
	);
	exportedData = toSignal(this._exportedData, { initialValue: [] });
	errorMessage = '';
	onSearch() {
		this.fileService.onEnter.next();
	}
	clear() {
		this.fileService.exportId.set(''); // Reset filter to show all data
	}
}
export default TransferfileuiComponent;
