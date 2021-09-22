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
import { switchMap, tap } from 'rxjs/operators';
import { applic } from '../../../shared/types/NamedId.applic';
import { CurrentTypesService } from '../../services/current-types.service';
import { enumerationSet } from '../../types/enum';

@Component({
  selector: 'app-edit-enum-set-dialog',
  templateUrl: './edit-enum-set-dialog.component.html',
  styleUrls: ['./edit-enum-set-dialog.component.sass']
})
export class EditEnumSetDialogComponent implements OnInit {

  applic = this.typesService.applic;
  enumObs = this.data.pipe(
    switchMap((id) => this.typesService.getEnumSet(id)),
    tap((value) => {
      this.enumSet.description=''
      this.enumSet.id = value.id;
    })
  )
  enumSet: Partial<enumerationSet> = {
    name: '',
    description: '',
    applicability: {
      id: '',
      name:''
    }
  };
  inEditMode = this.typesService.inEditMode;
  constructor(public dialogRef: MatDialogRef<EditEnumSetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: Observable<string>,private typesService: CurrentTypesService) { }

  ngOnInit(): void {
  }

  setName(value: string) {
    this.enumSet.name = value;
  }
  setApplicability(value: applic) {
    this.enumSet.applicability = value;
  }

  setDescription(value: string) {
    this.enumSet.description = value;
  }

  compareApplics(o1:applic,o2:applic) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
