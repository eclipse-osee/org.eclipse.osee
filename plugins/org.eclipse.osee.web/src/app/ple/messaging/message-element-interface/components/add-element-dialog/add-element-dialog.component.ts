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
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { BehaviorSubject, from, of, OperatorFunction } from 'rxjs';
import { concatMap, filter, groupBy, mergeMap, reduce, repeatWhen, switchMap, take, tap } from 'rxjs/operators';
import { PlatformType } from '../../../shared/types/platformType';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { element } from '../../../shared/types/element';
import { NewTypeDialogComponent } from '../../../shared/components/dialogs/new-type-dialog/new-type-dialog.component';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../../types-interface/types/newTypeDialogDialogData';
import { NewTypeDialogService } from '../../../shared/services/ui/new-type-dialog.service';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration } from '../../../shared/types/enum';
import { UiService } from '../../../../../ple-services/ui/ui.service';

@Component({
  selector: 'osee-messaging-add-element-dialog',
  templateUrl: './add-element-dialog.component.html',
  styleUrls: ['./add-element-dialog.component.sass']
})
export class AddElementDialogComponent implements OnInit {

  availableElements = this.structures.availableElements;
  storedId: string = '-1';
  availableTypes = this.structures.types.pipe(
    switchMap((types) => of(types).pipe(
      concatMap((array) => from(array).pipe(
        groupBy((p) => p.interfaceLogicalType),
        mergeMap((grouped) => grouped.pipe(
          reduce((acc, curr) => { acc.type = grouped.key; acc.types = [...acc.types, curr]; return acc; }, {type:grouped.key,types:[]} as {type:string,types:PlatformType[]})
        ))
      )),
      reduce((acc,curr)=>[...acc,curr],[] as {type:string,types:PlatformType[]}[])
    )),
  );
  typeDialogOpen = false;
  constructor(public dialog: MatDialog,private structures:CurrentStructureService,public dialogRef: MatDialogRef<AddElementDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddElementDialog, private typeDialogService: NewTypeDialogService, private _ui: UiService) { }

  ngOnInit(): void {
  }

  createNew() {
    this.data.element.id = '-1';
   }
  storeId(value: element) {
    this.storedId = value.id || '-1';
  }

  moveToStep(index: number, stepper: MatStepper) {
    stepper.selectedIndex = index - 1;
  }
  moveToReview(stepper: MatStepper) {
    this.data.element.id = this.storedId;
    this.moveToStep(3, stepper);
  }
  openPlatformTypeDialog() {
    this.typeDialogOpen = !this.typeDialogOpen;
  }
  receivePlatformTypeData(value: newPlatformTypeDialogReturnData) {
    this.typeDialogOpen = !this.typeDialogOpen;
    const { fields, createEnum, ...enumData } = value;
    this.mapTo(fields, createEnum, enumData).pipe(
      switchMap((createdElement) => this.structures.getFilteredTypes(fields.find(f => f.name === 'Name')?.value || '').pipe(
        tap((v) => {
          this._ui.updated = true;
          this.data.type = v[0] as Required<PlatformType>;
        })
      ))
    ).subscribe();
  }
  mapTo(results: logicalTypefieldValue[], newEnum: boolean, enumData: { enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    let resultingObj: Partial<PlatformType> = {};
    results.forEach((el) => {
      let name = el.name.charAt(0).toLowerCase() + el.name.slice(1);
      (resultingObj as any)[name]=el.value
    })
    return this.typeDialogService.createType(resultingObj,newEnum,enumData);
  }
  compareTypes(o1: PlatformType, o2: PlatformType) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }
}
