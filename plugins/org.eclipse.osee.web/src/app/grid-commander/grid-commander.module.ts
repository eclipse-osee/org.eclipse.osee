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

import { CommandPaletteModule } from './command-palette/command-palette.module';
import { GcDatatableModule } from './gc-datatable/gc-datatable.module';
import { GridCommanderComponent } from './grid-commander.component';
import { GridCommanderRoutingModule } from './grid-commander-routing.module';
import { ParameterTypesModule } from './parameter-types/parameter-types.module';
import { CreateCommandFormComponent } from './create-form/create-command-form/create-command-form.component';

@NgModule({
	declarations: [GridCommanderComponent],
	imports: [
		CommonModule,
		FormsModule,
		CommandPaletteModule,
		GridCommanderRoutingModule,
		GcDatatableModule,
		ParameterTypesModule,
		CreateCommandFormComponent,
	],
	providers: [],
})
export class GridCommanderModule {}
