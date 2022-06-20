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
import { BehaviorSubject, combineLatest, iif, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { PlatformType } from '../../../shared/types/platformType';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddElementDialog } from '../../types/AddElementDialog';
import { element } from '../../../shared/types/element';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../../shared/types/newTypeDialogDialogData';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration } from '../../../shared/types/enum';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { TypesUIService } from '../../../shared/services/ui/types-ui.service';
import { MimQuery, PlatformTypeQuery } from '../../../shared/types/MimQuery';

@Component({
  selector: 'osee-messaging-add-element-dialog',
  templateUrl: './add-element-dialog.component.html',
  styleUrls: ['./add-element-dialog.component.sass']
})
export class AddElementDialogComponent implements OnInit {

  availableElements = this.structures.availableElements;
  storedId: string = '-1';
  loadingTypes = false;
  types = this.structures.types;
  typeDialogOpen = false;
  searchOpen = false;
  private queryMode = new BehaviorSubject<boolean>(false);
  private query = new BehaviorSubject<MimQuery<PlatformType>|undefined>(undefined);
  availableTypes = combineLatest([this.queryMode, this.query]).pipe(
    debounceTime(100),
    switchMap(([mode, query]) => iif(() => mode === true && query !== undefined, this.structures.query(query as MimQuery<PlatformType>).pipe(
      distinctUntilChanged(),
      map((result) => {
        if (result.length === 1) {
          this.data.type=result[0]
        }
        return result;
      }),
    ), of(undefined)),
    ),
    shareReplay({bufferSize:1,refCount:true})
  )
  platformTypeState = this.availableTypes.pipe(
    switchMap((types) =>
      iif(() => types !== undefined,
        iif(() => types !== undefined && types.length === 1,
          of(types !== undefined && (types[0].name + ' selected.')),
          iif(() => types !== undefined && types.length !== 1 && this.data.type.id !== '',
            of('No exact match found.'), iif(()=>types !== undefined && types.length !== 1 && this.data.type.id === '',of(''),of(this.data.type.name+' selected.')))),
        of('')))
  )
  constructor (public dialog: MatDialog, private structures: CurrentStructureService, public dialogRef: MatDialogRef<AddElementDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddElementDialog, private typeDialogService: TypesUIService, private _ui: UiService) { 
  }

  ngOnInit(): void {
    this.query.next(new PlatformTypeQuery());
    this.queryMode.next(true);
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
    if (this.typeDialogOpen) {
      this.searchOpen = false;
    }
  }
  resetDialog() {
    this.searchOpen = false;
    this.typeDialogOpen = false;
  }
  openSearch() {
    this.searchOpen = !this.searchOpen;
    if (this.searchOpen) {
      this.typeDialogOpen = false;
    }
  }
  receivePlatformTypeData(value: newPlatformTypeDialogReturnData) {
    this.typeDialogOpen = !this.typeDialogOpen;
    const { fields, createEnum, ...enumData } = value;
    this.mapTo(fields, createEnum, enumData).pipe(
      switchMap((createdElement) => this.structures.getType(createdElement.results.ids[0]).pipe(
        tap((v) => {
          this._ui.updated = true;
          this.loadingTypes = true;
          this.data.type = v as Required<PlatformType>;
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
  receiveQuery(query: PlatformTypeQuery) {
    //close the dialog
    this.searchOpen = !this.searchOpen;
    //switch observable to query type
    this.queryMode.next(true);
    //set query to query
    this.query.next(query);
  }
}
