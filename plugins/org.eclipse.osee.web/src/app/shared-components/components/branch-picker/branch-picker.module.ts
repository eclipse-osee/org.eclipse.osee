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
import { BranchPickerComponent } from './branch-picker/branch-picker.component';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { BranchSelectorComponent } from './branch-selector/branch-selector.component';
import { BranchTypeSelectorComponent } from './branch-type-selector/branch-type-selector.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatOptionLoadingModule } from '../../mat-option-loading/mat-option-loading.module';

@NgModule({
	declarations: [
		BranchTypeSelectorComponent,
		BranchSelectorComponent,
		BranchPickerComponent,
	],
	imports: [
		CommonModule,
		MatRadioModule,
		MatFormFieldModule,
		MatOptionLoadingModule,
		FormsModule,
		MatProgressSpinnerModule,
		MatSelectModule,
	],
	exports: [BranchPickerComponent],
})
export class BranchPickerModule {}
