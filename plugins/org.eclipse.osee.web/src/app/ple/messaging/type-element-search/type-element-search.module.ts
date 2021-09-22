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

import { TypeElementSearchRoutingModule } from './type-element-search-routing.module';
import { TypeElementSearchComponent } from './type-element-search.component';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { BranchTypeSelectorComponent } from './components/branch-type-selector/branch-type-selector.component';
import { BranchSelectorComponent } from './components/branch-selector/branch-selector.component';
import { ElementTableComponent } from './components/element-table/element-table.component';
import { ElementTableSearchComponent } from './components/element-table-search/element-table-search.component';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';


@NgModule({
  declarations: [TypeElementSearchComponent, BranchTypeSelectorComponent, BranchSelectorComponent, ElementTableComponent, ElementTableSearchComponent],
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatRadioModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    SharedMessagingModule,
    OseeStringUtilsPipesModule,
    OseeStringUtilsDirectivesModule,
    TypeElementSearchRoutingModule
  ]
})
export class TypeElementSearchModule { }
