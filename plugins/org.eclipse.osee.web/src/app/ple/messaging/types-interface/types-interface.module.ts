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

import { TypesInterfaceRoutingModule } from './types-interface-routing.module';
import { TypesInterfaceComponent } from './types-interface.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { PleSharedMaterialModule } from '../../ple-shared-material/ple-shared-material.module';
import { LayoutModule } from '@angular/cdk/layout';
import { TypeGridComponent } from './components/type-grid/type-grid.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { UsermenuComponent } from './components/menus/usermenu/usermenu.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';


@NgModule({
  declarations: [TypesInterfaceComponent, TypeGridComponent, UsermenuComponent],
  imports: [
    CommonModule,
    MatGridListModule,
    LayoutModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    MatMenuModule,
    PleSharedMaterialModule,
    SharedMessagingModule,
    TypesInterfaceRoutingModule
  ]
})
export class TypesInterfaceModule { }
