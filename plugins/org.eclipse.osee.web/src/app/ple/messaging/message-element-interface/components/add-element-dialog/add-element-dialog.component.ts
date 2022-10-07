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
import { BehaviorSubject, combineLatest, from, iif, of } from 'rxjs';
import { concatMap, debounceTime, distinctUntilChanged, filter, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { PlatformType } from '../../../shared/types/platformType';
import { CurrentStructureService } from '../../services/current-structure.service';
import { AddElementDialog } from '../../types/AddElementDialog.d';
import { element } from '../../../shared/types/element';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../../shared/types/newTypeDialogDialogData';
import { applic } from '../../../../../types/applicability/applic';
import { enumeration } from '../../../shared/types/enum';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { TypesUIService } from '../../../shared/services/ui/types-ui.service';
import { andNameQuery, andQuery,  MimQuery, PlatformTypeQuery } from '../../../shared/types/MimQuery';

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
          of(types !== undefined && (types[0].name + ' selected.')).pipe(
            tap(v => {
              this.data.element.enumLiteral = this.data.type.enumSet?.description||'';
            })
          ),
          iif(() => types !== undefined && types.length !== 1 && this.data.type.id !== '',
            of('No exact match found.'), iif(() => types !== undefined && types.length !== 1 && this.data.type.id === '', of(''), of(this.data.type.name + ' selected.').pipe(
              tap(v => {
                this.data.element.enumLiteral = this.data.type.enumSet?.description||'';
              })
            )))),
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
  openPlatformTypeDialog(event?: Event) {
    event?.stopPropagation();
    this.typeDialogOpen = !this.typeDialogOpen;
    if (this.typeDialogOpen) {
      this.searchOpen = false;
    }
  }
  resetDialog() {
    this.searchOpen = false;
    this.typeDialogOpen = false;
  }
  openSearch(event?: Event) {
    event?.stopPropagation();
    this.searchOpen = !this.searchOpen;
    if (this.searchOpen) {
      this.typeDialogOpen = false;
    }
  }
  receivePlatformTypeData(value: newPlatformTypeDialogReturnData) {
    this.typeDialogOpen = !this.typeDialogOpen;
    const { fields, createEnum, ...enumData } = value;
    this.mapTo(fields, createEnum, enumData).pipe(
      concatMap(newElement => from(newElement.results.ids).pipe(
        concatMap((createdElement) => this.structures.getType(createdElement).pipe(
          filter(value=>value.id!=='-1' && value.id!==''),
          tap((v) => {
            this._ui.updated = true;
            this.loadingTypes = true;
            this.data.type = v as Required<PlatformType>;
            if (v as Required<PlatformType> && v.interfaceLogicalType === 'enumeration') {
              this.data.element.enumLiteral = value.enumSetDescription; 
            }
            const queries: andQuery[] = [];
            queries.push(new andNameQuery(v.name));
            const query = new PlatformTypeQuery(undefined, queries);
            this.queryMode.next(true);
            this.query.next(query);
          })
        ))
      )),
    ).subscribe();
  }
  mapTo(results: logicalTypefieldValue[], newEnum: boolean, enumData: { enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    let resultingObj: Partial<PlatformType> = {};
    results.forEach((el) => {
      let name = el.name.charAt(0).toLowerCase() + el.name.slice(1);
      (resultingObj as any)[name]=el.value
    })
    return this.typeDialogService.createType(resultingObj,enumData.enumSetId!=='1'&& enumData.enumSetId!=='',enumData);
  }
  compareTypes(o1: PlatformType, o2: PlatformType) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }

  updateEnumLiteral() {
    this.data.element.enumLiteral = this.data.type.enumSet?.description||'';
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
