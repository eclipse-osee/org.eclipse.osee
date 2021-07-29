import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConnectionViewRoutingModule } from './connection-view-routing.module';
import { ConnectionViewComponent } from './connection-view.component';
import { BranchTypeSelectorComponent } from './components/misc/branch-type-selector/branch-type-selector.component';
import { BranchSelectorComponent } from './components/misc/branch-selector/branch-selector.component';
import { BaseComponent } from './components/base/base/base.component';
import { GraphComponent } from './components/misc/graph/graph.component';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { MatRadioModule } from '@angular/material/radio';
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


@NgModule({
  declarations: [ConnectionViewComponent, BranchTypeSelectorComponent, BranchSelectorComponent, BaseComponent, GraphComponent, EditConnectionDialogComponent, ConfirmRemovalDialogComponent, EditNodeDialogComponent, CreateConnectionDialogComponent, CreateNewNodeDialogComponent],
  imports: [
    CommonModule,
    ConnectionViewRoutingModule,
    MatRadioModule,
    MatDialogModule,
    MatFormFieldModule,
    MatButtonModule,
    MatMenuModule,
    FormsModule,
    MatSelectModule,
    MatInputModule,
    NgxGraphModule
  ]
})
export class ConnectionViewModule { }
