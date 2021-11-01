/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { MimSingleDiffComponent } from '../mim-single-diff/mim-single-diff.component';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';



@NgModule({
  declarations: [MimSingleDiffComponent],
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule
  ],
  exports:[MimSingleDiffComponent]
})
export class DiffViewsModule { }
