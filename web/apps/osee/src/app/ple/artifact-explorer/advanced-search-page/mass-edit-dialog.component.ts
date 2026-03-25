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

/**
 * Author: Eihab Khudhair (ekhudhai)
 * Task 208 - Implement backend mass update endpoint integration
 * Dialog now receives valid selectable attribute types from the page.
 */
export type MassEditDialogData = {
  selectedIds: string[];
  attributeTypes: { id: string; name: string }[];
};

export type MassEditDialogResult =
  | { action: 'cancel' }
  | {
      action: 'apply';
      attributeTypeId?: string;
      value?: string;
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
  ],
  templateUrl: './mass-edit-dialog.component.html',
})
export class MassEditDialogComponent {
  private dialogRef = inject(MatDialogRef<MassEditDialogComponent, MassEditDialogResult>);
  data =
    inject<MassEditDialogData>(MAT_DIALOG_DATA, { optional: true }) ?? {
      selectedIds: [],
      attributeTypes: [],
    };

  attributeTypeId = '';
  value = '';

  onCancel(): void {
    this.dialogRef.close({ action: 'cancel' });
  }

  onApply(): void {
    this.dialogRef.close({
      action: 'apply',
      attributeTypeId: (this.attributeTypeId || '').trim() || undefined,
      value: (this.value || '').trim() || undefined,
    });
  }
}