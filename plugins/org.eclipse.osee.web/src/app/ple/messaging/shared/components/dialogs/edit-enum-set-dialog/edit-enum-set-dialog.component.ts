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
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { map, scan, switchMap, tap } from 'rxjs/operators';
import { applic } from '../../../../../../types/applicability/applic';
import { enumerationSet, enumeration } from '../../../types/enum';
import { EnumerationUIService } from '../../../services/ui/enumeration-ui.service';
import { ApplicabilityListUIService } from '../../../services/ui/applicability-list-ui.service';
import { PreferencesUIService } from '../../../services/ui/preferences-ui.service';
import { enumsetDialogData } from '../../../types/EnumSetDialogData';
import { TypesUIService } from '../../../services/ui/types-ui.service';

@Component({
  selector: 'app-edit-enum-set-dialog',
  templateUrl: './edit-enum-set-dialog.component.html',
  styleUrls: ['./edit-enum-set-dialog.component.sass']
})
export class EditEnumSetDialogComponent implements OnInit {

  applic = this.applicabilityService.applic;
  private _addEnum = new BehaviorSubject<enumeration | undefined>(undefined);
  private _addEnums = this._addEnum.pipe(
    scan((acc,curr)=>[...acc,curr],[] as (enumeration|undefined)[])
  )
  enumObs = combineLatest([this.data, this._addEnums]).pipe(
    switchMap(([{ id, isOnEditablePage }, AddEnums]) => this.enumSetService.getEnumSet(id).pipe(
      switchMap((val) => of(val).pipe(
        map((val) => {
          const enums = AddEnums.filter((val) => val !== undefined) as enumeration[];
          val.enumerations = [...(val?.enumerations ? val.enumerations : []), ...enums]
          this.enumSet.enumerations = [...enums];
          return val;
        })
      ))
    )),
    tap((enumSet) => {
      this.enumSet.id = enumSet.id;
    }),
  )

  isOnEditablePage = this.data.pipe(
    map(({id,isOnEditablePage})=>isOnEditablePage)
  )
  enumSet: Partial<enumerationSet> = {
  };
  inEditMode = this.preferenceService.inEditMode;
  _type = this.data.pipe(
    switchMap(({ id, isOnEditablePage }) => this.typeService.getType(id)),
  )
  constructor (public dialogRef: MatDialogRef<EditEnumSetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: Observable<enumsetDialogData>, private enumSetService: EnumerationUIService, private applicabilityService: ApplicabilityListUIService, private preferenceService: PreferencesUIService, private typeService: TypesUIService) { }

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
  addEnum(length: number) {
    this._addEnum.next({name:'',ordinal:length,applicability:{id:'1',name:'Base'}})
  }
}
