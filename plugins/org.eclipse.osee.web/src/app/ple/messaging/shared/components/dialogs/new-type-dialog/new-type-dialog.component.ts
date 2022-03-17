/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialogRef, MatDialogState } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject, from, Observable, Subject } from 'rxjs';
import {
  concatMap,
  debounceTime,
  distinctUntilChanged,
  share,
  switchMap,
  tap,
} from 'rxjs/operators';
import { applic } from '../../../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../../types/enum';
import { logicalType, logicalTypeFieldInfo, logicalTypeFormDetail } from '../../../types/logicaltype';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../../../types-interface/types/newTypeDialogDialogData';
import { PlatformType } from '../../../types/platformType';
import { ApplicabilityListUIService } from '../../../services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../../services/ui/enumeration-ui.service';
import { TypesService } from '../../../services/http/types.service';
import { EnumsService } from '../../../services/http/enums.service';

@Component({
  selector: 'app-new-type-dialog',
  templateUrl: './new-type-dialog.component.html',
  styleUrls: ['./new-type-dialog.component.sass'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '60vh', overflowY: 'auto' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ]
})
export class NewTypeDialogComponent implements OnInit {
  returnObj: Partial<PlatformType> = {};
  type: string = "";
  private _typeName:string =""
  private _typeSubject: Subject<string> = new Subject();
  @Output() dialogClosed = new EventEmitter<newPlatformTypeDialogReturnData>();
  logicalTypes: Observable<logicalType[]> = this.typesService.logicalTypes;
  fields: logicalTypefieldValue[] = [];
  formInfo = this._typeSubject.pipe(
    debounceTime(500),
    distinctUntilChanged(),
    switchMap((x) =>
      this.typesService.getLogicalTypeFormDetail(x).pipe(share())
    ),
    share()
  );
  formDetail!: logicalTypeFormDetail;
  private _fieldObs = this.formInfo.pipe(
    tap((c) => {
      this.fields = [];
      this._typeName = c.name;
    }),
    concatMap((a) => from(a.fields)),
    tap((x) => {
      this.fields.push({
        name: x.attributeType,
        value: '',
      });
    }),
    debounceTime(200),
    tap((z) => {
      this.fields.push({
        name: "interfaceLogicalType",
        value: this._typeName
      })
    })
  );
  createNewEnum = new BehaviorSubject<boolean>(false);
  dataSource = new MatTableDataSource<enumeration>();
  applics = this.applicabilityService.applic;
  enumSets = this.enumSetService.enumSets;
  enumSet: enumerationSet = {
    name: '',
    description: '',
    applicability:
    {
      id: '1',
      name:"Base"
      }
  }
  units = this.constantEnumService.units;
  disableClose = false;
  constructor(
    public dialogRef: MatDialogRef<NewTypeDialogComponent>,
    private typesService: TypesService,
    private constantEnumService: EnumsService,
    private applicabilityService: ApplicabilityListUIService, private enumSetService: EnumerationUIService,
  ) {
    this._fieldObs.subscribe();
    this.formInfo.subscribe((value) => {
      this.formDetail = value;
    })
    this.dataSource.data = [];
    if (this.dialogRef.id !== 'new-type-dialog') {
      this.disableClose=true;
    }
  }

  ngOnInit(): void { }
  /**
   * sets the current type to query
   * @param id id of type
   */
  setType(id: string) {
    this._typeSubject.next(id);
  }
  /**
   * Validates the second step of the form
   * @returns true if all required fields have a filled in value
   */
  isStep2Complete() {
    let result: boolean = true;
    if (this.formDetail !== undefined) {
      this.formDetail.fields.forEach((value, index) => {
        if (value.required && this.fields[index].value.length === 0) {
          if (value.editable) {
            result = false;
          }
          if (!value.editable && this.fields[index].value !== value.defaultValue) {
            result = false; 
          }
        }
      })
    }
    return result;
  }
  setDefaultValue(form: logicalTypeFieldInfo, index: number) {
    if (this.fields[index].name === form.attributeType) {
      if (this.fields[index].value === null || this.fields[index].value === undefined || this.fields[index].value === "") {
        this.fields[index].value = form.defaultValue;
      }
    }
  }

  getTypeName() {
    return this._typeName;
  }

  toggleEnumCreationState() {
    this.createNewEnum.next(!this.createNewEnum.getValue())
  }

  compareApplics(o1:applic,o2:applic) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }
  compareEnumSet(o1: enumerationSet, o2: enumerationSet) {
    return o1?.id === o2?.id && o1?.name === o2?.name;
  }
  validateEnumLengthIsBelowMax() {
    let bitSize = this.fields.filter((o) => o.name === 'InterfacePlatformTypeBitSize')[0]
    if (this.dataSource.data.length >= 2 ** (parseInt(bitSize.value))) {
      return true;
    }
    return false;
  }
  addEnum() {
    let enumData = [
      ...this.dataSource.data,
      {name:'',ordinal:((this.dataSource.data[this.dataSource.data.length - 1]?.ordinal !==undefined ? (this.dataSource.data[this.dataSource.data.length - 1].ordinal) : -1)+1),applicability:{id:'1',name:'Base'}}
    ]
    this.dataSource.data=enumData;
  }

  get ReturnData() {
    let enumdescription: string = " ";
    this.dataSource.data.forEach((value, index) => {
      if (index != 0) {
        enumdescription+=" , "
      }
      enumdescription+=(value.name+"="+value.ordinal)
    })
    let returnValue:newPlatformTypeDialogReturnData = {
      fields: [
        ...this.fields, 
      ],
      createEnum: this.createNewEnum.getValue(),
      enumSetName: this.enumSet?.name || '',
      enumSetId:this.enumSet?.id||'-1',
      enumSetDescription: (this.enumSet?.description||'')+enumdescription,
      enumSetApplicability: this.enumSet?.applicability || { id: '1', name: "Base" },
      enums: [
        ...(this.dataSource.data!==[]? this.dataSource.data : this.enumSet?.enumerations||[]) 
      ]
    }
    return returnValue;
  }
  closeDialog() {
    return this.ReturnData;
  }
  hideTypeDialog() {
    this.dialogClosed.emit(this.ReturnData);
  }
}
