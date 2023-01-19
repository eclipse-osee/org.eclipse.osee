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
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';

import { CheckboxContainerComponent } from './checkbox-container/checkbox-container.component';
import { ColumnFilterComponent } from '../gc-datatable/filter-component/column-filter/column-filter.component';
import { CommandPaletteComponent } from './command-palette/command-palette.component';
import { ParameterTypesModule } from '../parameter-types/parameter-types.module';
import { TableFilterComponent } from '../gc-datatable/filter-component/table-filter.component';
import { SharedModule } from '../shared/shared.module';
import { MatDialogModule } from '@angular/material/dialog';

@NgModule({
	declarations: [
		CommandPaletteComponent,
		CheckboxContainerComponent,
		TableFilterComponent,
		ColumnFilterComponent,
	],
	imports: [
		ParameterTypesModule,
		CommonModule,
		FormsModule,
		MatAutocompleteModule,
		MatButtonModule,
		MatCheckboxModule,
		MatChipsModule,
		MatDialogModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatSelectModule,
		MatTooltipModule,
		SharedModule,
	],
	exports: [CommandPaletteComponent],
})
export class CommandPaletteModule {}
