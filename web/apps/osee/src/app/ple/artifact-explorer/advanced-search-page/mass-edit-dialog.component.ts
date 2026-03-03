/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *
 * Author: Eihab Khudhair (ekhudhai)
 * Task 207 - Create Mass Edit dialog
 **********************************************************************/

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';

export type MassEditDialogData = {
  selectedIds: string[];
};

export type MassEditDialogResult =
  | { action: 'cancel' }
  | {
      action: 'apply';
      // Task 207 is UI-only, so we just return what user chose in the dialog
      field: 'attribute' | 'relations' | 'type' | 'custom';
      attributeName?: string;
      value?: string;
      applyToAllSelected: boolean;
    };

@Component({
  selector: 'osee-mass-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatCheckboxModule,
  ],
  templateUrl: './mass-edit-dialog.component.html',
})
export class MassEditDialogComponent {
  private dialogRef = inject(MatDialogRef<MassEditDialogComponent, MassEditDialogResult>);
  data = inject<MassEditDialogData>(MAT_DIALOG_DATA, { optional: true }) ?? { selectedIds: [] };

  /**
   * Author: Eihab Khudhair (ekhudhai)
   * Task 207 - Dialog UI state (UI-only)
   */
  field: 'attribute' | 'relations' | 'type' | 'custom' = 'attribute';
  attributeName = '';
  value = '';
  applyToAllSelected = true;

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }

  onApply(): void {
    this.dialogRef.close({
      action: 'apply',
      field: this.field,
      attributeName: (this.attributeName || '').trim() || undefined,
      value: (this.value || '').trim() || undefined,
      applyToAllSelected: !!this.applyToAllSelected,
    });
  }
}