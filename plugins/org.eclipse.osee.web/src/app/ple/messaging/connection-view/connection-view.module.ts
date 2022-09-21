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

import { ConnectionViewRoutingModule } from './connection-view-routing.module';
import { ConnectionViewComponent } from './connection-view.component';
import { BaseComponent } from './components/base/base/base.component';
import { GraphComponent } from './components/misc/graph/graph.component';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { EditConnectionDialogComponent } from './components/dialogs/edit-connection-dialog/edit-connection-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ConfirmRemovalDialogComponent } from './components/dialogs/confirm-removal-dialog/confirm-removal-dialog.component';
import { EditNodeDialogComponent } from './components/dialogs/edit-node-dialog/edit-node-dialog.component';
import { CreateConnectionDialogComponent } from './components/dialogs/create-connection-dialog/create-connection-dialog.component';
import { CreateNewNodeDialogComponent } from './components/dialogs/create-new-node-dialog/create-new-node-dialog.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DiffViewsModule } from '../../diff-views/diff-views/diff-views.module';
import { GraphLinkMenuComponent } from './components/menu/graph-link-menu/graph-link-menu.component';
import { GraphNodeMenuComponent } from './components/menu/graph-node-menu/graph-node-menu.component';
import { UsermenuComponent } from './components/menu/usermenu/usermenu.component';
import { MatIconModule } from '@angular/material/icon';
import { BranchPickerModule } from '../../../shared-components/components/branch-picker/branch-picker.module';
import { ActionStateButtonModule } from '../../../shared-components/components/action-state-button/action-state-button.module';
import { BranchUndoButtonModule } from '../../../shared-components/components/branch-undo-button/branch-undo-button.module';
import { MatOptionLoadingModule } from '../../../shared-components/mat-option-loading/mat-option-loading.module';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';


@NgModule({
  declarations: [ConnectionViewComponent, BaseComponent, GraphComponent, EditConnectionDialogComponent, ConfirmRemovalDialogComponent, EditNodeDialogComponent, CreateConnectionDialogComponent, CreateNewNodeDialogComponent, GraphLinkMenuComponent, GraphNodeMenuComponent, UsermenuComponent],
  imports: [
    CommonModule,
    ConnectionViewRoutingModule,
    DiffViewsModule,
    BranchPickerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatButtonModule,
    MatMenuModule,
    FormsModule,
    MatSelectModule,
    MatInputModule,
    MatSidenavModule,
    MatIconModule,
    ActionStateButtonModule,
    BranchUndoButtonModule,
    MatOptionLoadingModule,
    MatProgressSpinnerModule,
    NgxGraphModule
  ]
})
export class ConnectionViewModule { }
