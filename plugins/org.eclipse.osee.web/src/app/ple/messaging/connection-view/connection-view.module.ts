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


@NgModule({
  declarations: [ConnectionViewComponent, BranchTypeSelectorComponent, BranchSelectorComponent, BaseComponent, GraphComponent],
  imports: [
    CommonModule,
    ConnectionViewRoutingModule,
    MatRadioModule,
    MatFormFieldModule,
    FormsModule,
    MatSelectModule,
    NgxGraphModule
  ]
})
export class ConnectionViewModule { }
