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
    FormsModule,
    MatSlideToggleModule,
    PleSharedMaterialModule,
    TypesInterfaceRoutingModule
  ]
})
export class TypesInterfaceModule { }
