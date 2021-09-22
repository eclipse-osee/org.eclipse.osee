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
import { ColumnPreferencesDialogComponent } from './components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { ConvertMessageInterfaceTitlesToStringPipe } from './pipes/convert-message-interface-titles-to-string.pipe';

@NgModule({
  declarations: [ColumnPreferencesDialogComponent, ConvertMessageInterfaceTitlesToStringPipe],
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatListModule,
    MatCheckboxModule,
    FormsModule,
    MatButtonModule,
  ],
  exports:[ConvertMessageInterfaceTitlesToStringPipe,ColumnPreferencesDialogComponent]
})
export class SharedMessagingModule {}
