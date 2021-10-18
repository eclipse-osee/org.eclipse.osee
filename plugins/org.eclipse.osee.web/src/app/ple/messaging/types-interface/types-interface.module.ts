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
import { PlatformTypeCardComponent } from './components/platform-type-card/platform-type-card.component';

import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { PleSharedMaterialModule } from '../../ple-shared-material/ple-shared-material.module';
import { LayoutModule } from '@angular/cdk/layout';
import { TypeGridComponent } from './components/type-grid/type-grid.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { EditTypeDialogComponent } from './components/edit-type-dialog/edit-type-dialog.component';
import { NewTypeDialogComponent } from './components/new-type-dialog/new-type-dialog.component';
import { MatSelectModule } from '@angular/material/select';
import {MatStepperModule} from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { MatProgressBarModule } from '@angular/material/progress-bar';


@NgModule({
  declarations: [TypesInterfaceComponent, PlatformTypeCardComponent, TypeGridComponent, EditTypeDialogComponent, NewTypeDialogComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatGridListModule,
    LayoutModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatSelectModule,
    MatStepperModule,
    MatProgressBarModule,
    FormsModule,
    MatSlideToggleModule,
    MatTableModule,
    PleSharedMaterialModule,
    SharedMessagingModule,
    TypesInterfaceRoutingModule
  ]
})
export class TypesInterfaceModule { }
