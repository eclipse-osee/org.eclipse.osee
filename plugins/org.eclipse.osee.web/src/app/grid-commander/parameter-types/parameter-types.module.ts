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
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { ActionStateButtonModule } from '../../shared-components/components/action-state-button/action-state-button.module';
import { BranchPickerModule } from '../../shared-components/components/branch-picker/branch-picker.module';

import { HideColumnCommandComponent } from './parameter-multiple-select/hide-column-command/hide-column-command.component';
import { ParameterBooleanComponent } from './parameterBoolean/parameter-boolean.component';
import { ParameterBranchComponent } from './parameterBranch/parameter-branch.component';
import { ParameterIntegerComponent } from './parameterInteger/parameter-integer.component';
import { ParameterMultipleSelectComponent } from './parameter-multiple-select/parameter-multiple-select.component';
import { ParameterSingleSelectComponent } from './parameter-single-select/parameter-single-select.component';
import { ParameterStringComponent } from './parameterString/parameter-string.component';
import { ParameterTypesComponent } from './parameter-types.component';
import { MatButtonModule } from '@angular/material/button';
import { SharedModule } from '../shared/shared.module';

@NgModule({
	declarations: [
		HideColumnCommandComponent,
		ParameterBooleanComponent,
		ParameterBranchComponent,
		ParameterIntegerComponent,
		ParameterMultipleSelectComponent,
		ParameterSingleSelectComponent,
		ParameterStringComponent,
		ParameterTypesComponent,
	],
	imports: [
		ActionStateButtonModule,
		BranchPickerModule,
		CommonModule,
		FormsModule,
		MatButtonModule,
		MatChipsModule,
		MatFormFieldModule,
		MatIconModule,
		MatInputModule,
		MatSelectModule,
		SharedModule,
	],
	exports: [ParameterTypesComponent],
})
export class ParameterTypesModule {}
