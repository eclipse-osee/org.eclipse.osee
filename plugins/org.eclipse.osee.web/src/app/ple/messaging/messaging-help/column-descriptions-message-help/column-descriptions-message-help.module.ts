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

import { ColumnDescriptionsMessageHelpRoutingModule } from './column-descriptions-message-help-routing.module';
import { ColumnDescriptionsMessageHelpComponent } from './column-descriptions-message-help.component';
import { MatTableModule } from '@angular/material/table';
import { MatListModule } from '@angular/material/list';


@NgModule({
  declarations: [ColumnDescriptionsMessageHelpComponent],
  imports: [
    CommonModule,
    MatTableModule,
    MatListModule,
    ColumnDescriptionsMessageHelpRoutingModule
  ]
})
export class ColumnDescriptionsMessageHelpModule { }
