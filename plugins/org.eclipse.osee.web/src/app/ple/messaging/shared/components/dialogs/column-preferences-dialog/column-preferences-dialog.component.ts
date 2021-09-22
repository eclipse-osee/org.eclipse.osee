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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EditAuthService } from '../../../services/edit-auth-service.service';
import { settingsDialogData } from '../../../types/settingsdialog';

@Component({
  selector: 'app-column-preferences-dialog',
  templateUrl: './column-preferences-dialog.component.html',
  styleUrls: ['./column-preferences-dialog.component.sass']
})
export class ColumnPreferencesDialogComponent implements OnInit {
  editability: Observable<boolean> = this.editAuthService.branchEditability.pipe(
    map(x=>x?.editable)
  )

  constructor(public dialogRef: MatDialogRef<ColumnPreferencesDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: settingsDialogData, private editAuthService: EditAuthService) {
    this.editAuthService.BranchIdString = data.branchId;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }
}
