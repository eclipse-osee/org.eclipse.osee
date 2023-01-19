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
import { MatCardModule } from '@angular/material/card';

import { CommandPaletteModule } from './command-palette/command-palette.module';
import { GcDatatableModule } from './gc-datatable/gc-datatable.module';
import { GridCommanderComponent } from './grid-commander.component';
import { GridCommanderRoutingModule } from './grid-commander-routing.module';
import { ParameterTypesModule } from './parameter-types/parameter-types.module';

@NgModule({
	declarations: [GridCommanderComponent],
	imports: [
		CommonModule,
		FormsModule,
		CommandPaletteModule,
		GridCommanderRoutingModule,
		GcDatatableModule,
		ParameterTypesModule,
	],
	providers: [],
})
export class GridCommanderModule {}
