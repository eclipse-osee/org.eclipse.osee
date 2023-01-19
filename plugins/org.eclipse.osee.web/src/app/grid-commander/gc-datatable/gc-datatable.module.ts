/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';

import { DeleteRowDialogComponent } from './delete-row-dialog/delete-row-dialog.component';
import { GcDatatableComponent } from './gc-datatable.component';
import { FormsModule } from '@angular/forms';
import { NoDataToDisplayComponent } from './no-data-to-display/no-data-to-display/no-data-to-display.component';

@NgModule({
	declarations: [
		DeleteRowDialogComponent,
		GcDatatableComponent,
		NoDataToDisplayComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		MatButtonModule,
		MatCardModule,
		MatCheckboxModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatPaginatorModule,
		MatSelectModule,
		MatSortModule,
		MatTableModule,
		MatToolbarModule,
		MatTooltipModule,
	],
	exports: [GcDatatableComponent],
})
export class GcDatatableModule {}
