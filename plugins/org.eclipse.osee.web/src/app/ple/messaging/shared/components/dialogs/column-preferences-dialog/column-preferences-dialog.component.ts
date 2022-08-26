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
import { defaultEditElementProfile, defaultEditStructureProfile, defaultViewElementProfile, defaultViewStructureProfile } from '../../../constants/defaultProfiles';
import { EditAuthService } from '../../../services/edit-auth-service.service';
import { HeaderService } from '../../../services/ui/header.service';
import { element } from '../../../types/element';
import { settingsDialogData } from '../../../types/settingsdialog';
import { structure } from '../../../types/structure';

@Component({
  selector: 'app-column-preferences-dialog',
  templateUrl: './column-preferences-dialog.component.html',
  styleUrls: ['./column-preferences-dialog.component.sass']
})
export class ColumnPreferencesDialogComponent implements OnInit {
  editability: Observable<boolean> = this.editAuthService.branchEditability.pipe(
    map(x=>x?.editable)
  )

  constructor(public dialogRef: MatDialogRef<ColumnPreferencesDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: settingsDialogData, private editAuthService: EditAuthService, private _headerService: HeaderService) {
    this.editAuthService.BranchIdString = data.branchId;
   }

  ngOnInit(): void {
  }

  onNoClick() {
    this.dialogRef.close();
  }

  getHeaderByName(value: keyof structure|keyof element, type: 'structure'|'element') {
    return this._headerService.getHeaderByName(value,type);
  }

  resetToDefaultHeaders(event: MouseEvent) {
    if (this.data.editable) {
      this.data.allowedHeaders1 = defaultEditStructureProfile;
      this.data.allowedHeaders2 = defaultEditElementProfile;
      return;
    }
    this.data.allowedHeaders1 = defaultViewStructureProfile;
    this.data.allowedHeaders2 = defaultViewElementProfile;
  }

  /**
   * solely for generating test attributes for integration tests, do not use elsewhere
   */
  /* istanbul ignore next */ 
  isChecked(columnNumber:0|1, preference:(keyof structure | keyof element) ) {
    const headerList = this.getHeaderList(columnNumber);
    return headerList.includes(preference);
  }
  /**
   * solely for generating test attributes for integration tests, do not use elsewhere
   */
  /* istanbul ignore next */ 
  getHeaderList(columnNumber: 0 | 1): (keyof element)[]|(keyof structure)[] {
    if (columnNumber) {
      return this.data.allowedHeaders2;
    }
    return this.data.allowedHeaders1;
  }
}
