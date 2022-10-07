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
import { BranchPickerModule } from './components/branch-picker/branch-picker.module';
import { ActionStateButtonModule } from './components/action-state-button/action-state-button.module';
import { MatOptionLoadingModule } from './mat-option-loading/mat-option-loading.module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BranchPickerModule,
    MatOptionLoadingModule,
    ActionStateButtonModule
  ]
})
export class SharedComponentsModule { }
