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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef, MatDialogState } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject, from, iif, Observable, Subject, of, combineLatest, ReplaySubject } from 'rxjs';
import {
  concatMap,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  reduce,
  share,
  switchMap,
  take,
  tap,
} from 'rxjs/operators';
import { applic } from '../../../../../../types/applicability/applic';
import { enumeration, enumerationSet } from '../../../types/enum';
import { logicalType, logicalTypeFieldInfo, logicalTypeFormDetail } from '../../../types/logicaltype.d';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../../types/newTypeDialogDialogData';
import { PlatformType } from '../../../types/platformType';
import { ApplicabilityListUIService } from '../../../services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../../services/ui/enumeration-ui.service';
import { TypesService } from '../../../services/http/types.service';
import { EnumsService } from '../../../services/http/enums.service';
import { validateEnumLengthIsBelowMax } from '../../../functions/validateEnumLength';

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
  // should deprecate/remove this soon - LV
  private _fieldObs = this.formInfo.pipe(
    tap((c) => {
      this.fields = [];
      this._typeName = c.name;
    }),
    concatMap((a) => from(a.fields)),
    tap((x) => {
      this.fields.push({
        name: x.attributeType,
        value: this.preFillData!==undefined && this.preFillData.length>0 && Object.entries(this.preFillData[0]).map((v)=>v[0]).some((key)=>key.toLowerCase()===x.attributeType.toLowerCase())?Object.entries(this.preFillData[0]).filter((entry)=>entry[0].toLowerCase()===x.attributeType.toLowerCase())[0][1]:'',
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
  _enumSets = this.enumSetService.enumSets;
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
  @Input() preFillData?: PlatformType[];
  private _isStep2Complete = new Subject<boolean>();
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
  private _attributesUnique = new ReplaySubject<string>();
  private _enumsUnique = new BehaviorSubject<boolean>(false);
  private _attributeList = new ReplaySubject<Map<string, string>>();
  private _attributes = new BehaviorSubject(new Map<string, logicalTypefieldValue>());
  
  enumSets = combineLatest([this._attributeList, this._enumSets]).pipe(
    switchMap(([attributes, enumSets]) => of({ bitSize:attributes.get('Bit Size') || '0', enumSets:enumSets }).pipe(
      switchMap(({ bitSize, enumSets }) => of(enumSets).pipe(
        concatMap(enumSets => from(enumSets).pipe(
          filter(enumSet=>!validateEnumLengthIsBelowMax(enumSet.enumerations?.length||0,parseInt(bitSize)))
        )),
        reduce((acc,curr)=>[...acc,curr],[] as enumerationSet[])
      ))
    ))
  )

  ngOnInit(): void {
    if (this.preFillData !== undefined && this.preFillData.length > 0) {
      this.logicalTypes.pipe(
        concatMap((lt) => from(lt)),
        filter((logicalType) => this.preFillData !== undefined && this.preFillData.length > 0 && logicalType.name === this.preFillData[0].interfaceLogicalType),
        take(1),
        map(lt => { this.setType(lt.id); this.type = lt.id; this._typeName = lt.name; })
      ).subscribe();
    }
   }
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
  get isStep2Complete() {
    return this._isStep2Complete;
  }

  get typeName() {
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

  get ReturnData() {
    let enumdescription: string = " ";
    this.dataSource.data.forEach((value, index) => {
      if (index != 0) {
        enumdescription+=" , "
      }
      enumdescription+=(value.name+"="+value.ordinal)
    })
    const fields = [
      ...this.fields,
      ...this._attributes.getValue().values(),
      {
        name: 'interfaceLogicalType',
        value:this._typeName
      }
    ];
    let returnValue:newPlatformTypeDialogReturnData = {
      fields: fields,
      createEnum: this.createNewEnum.getValue(),
      enumSetName: this.enumSet?.name || '',
      enumSetId:this.enumSet?.id||'-1',
      enumSetDescription: (this.enumSet?.description||'')+enumdescription,
      enumSetApplicability: this.enumSet?.applicability || { id: '1', name: "Base" },
      enums: this.enumSet?.enumerations||[]
    }
    return returnValue;
  }
  closeDialog() {
    return this.ReturnData;
  }
  hideTypeDialog() {
    this.dialogClosed.emit(this.ReturnData);
  }
  attributesUnique(value: string) {
    this._attributesUnique.next(value);
  }

  enumSetUnique(value: boolean) {
    this._enumsUnique.next(value);
  }

  step2Complete(value: boolean) {
    this._isStep2Complete.next(value);
  }
  get isUnique() {
    return combineLatest([this.createNewEnum, this._attributesUnique, this._enumsUnique]).pipe(
      switchMap(([newEnum, attributes, enums]) => iif(() => newEnum,
        of([attributes, enums]).pipe(
          switchMap(([attr, enums]) => iif(() => attr === '' && enums === true,
            of(true),
            of(false)
          ))
        ),
        of(attributes).pipe(
          switchMap(attr => iif(() => attr === '',
            of(true),
            of(false)
          )),
        )
      ))
    )
  }
  attributesUpdate(value: Map<string, string>) {
    this._attributeList.next(value);
  }
  get attributes() {
    return this._attributeList;
  }
  fieldsUpdate(value: Map<string, logicalTypefieldValue>) {
    this._attributes.next(value);
  }
  updateEnumSet(value: enumerationSet) {
    this.enumSet = value;
  }
}
