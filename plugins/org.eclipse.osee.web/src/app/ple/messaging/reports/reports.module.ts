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

import { ReportsRoutingModule } from './reports-routing.module';
import { ReportsComponent } from './reports.component';
import { BranchPickerModule } from 'src/app/shared-components/components/branch-picker/branch-picker.module';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
  declarations: [
    ReportsComponent
  ],
  imports: [
    BranchPickerModule,
    CommonModule,
    MatButtonModule,
    ReportsRoutingModule
  ]
})
export class ReportsModule { }
