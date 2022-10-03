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
import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { BehaviorSubject, combineLatest, from, iif, of, ReplaySubject } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap, debounceTime, concatMap, reduce, take, tap } from 'rxjs/operators';
import { ATTRIBUTETYPEID } from '../../../../../../types/constants/AttributeTypeId.enum';
import { EnumsService } from '../../../services/http/enums.service';
import { TypesService } from '../../../services/http/types.service';
import { CurrentQueryService } from '../../../services/ui/current-query.service';
import { logicalTypeFieldInfo, logicalTypeFormDetail } from '../../../types/logicaltype.d';
import { andQuery, MimQuery, PlatformTypeQuery } from '../../../types/MimQuery';
import { logicalTypefieldValue } from '../../../types/newTypeDialogDialogData';
import { PlatformType } from '../../../types/platformType';

@Component({
  selector: 'osee-new-type-form',
  templateUrl: './new-type-form.component.html',
  styleUrls: ['./new-type-form.component.sass']
})
export class NewTypeFormComponent implements OnInit, OnChanges {
  units = this.constantEnumService.units;
  @Input() logicalType: string = '';
  _logicalType = new ReplaySubject<string>(1);
  formInfo = this._logicalType.pipe(
    filter(val => val !== ''),
    distinctUntilChanged(),
    debounceTime(500),
    switchMap(type => this.typesService.getLogicalTypeFormDetail(type))
  );
  private _formValues = new BehaviorSubject<Map<string, string>>(new Map());
  private  _attributesUnique = this._formValues.pipe(
    debounceTime(500),
    switchMap(values => of(values).pipe(
      concatMap(v => from(v.entries()).pipe(
        filter(([attribute,value])=>value!=='' && attribute !== ATTRIBUTETYPEID.NAME),
        map(([attribute, value]) => new andQuery(attribute as ATTRIBUTETYPEID, value))
      )),
      take(values.size),
      reduce((acc, curr) => [...acc, curr], [] as andQuery[]),
      map(queries => new PlatformTypeQuery(undefined, queries)),
      debounceTime(1000),
      switchMap(query => this.queryService.queryExact(query as MimQuery<PlatformType>)),
      switchMap(results => iif(() => results.length > 0,
        of(true),
        of(false)
      ))
    ))
  );

  @Output() nameUnique= this._formValues.pipe(
    debounceTime(500),
    switchMap(values => of(values).pipe(
      concatMap(v => from(v.entries()).pipe(
        filter(([attribute,value])=>value!=='' && attribute === ATTRIBUTETYPEID.NAME),
        map(([attribute, value]) => new andQuery(attribute as ATTRIBUTETYPEID, value))
      )),
      take(values.size),
      reduce((acc, curr) => [...acc, curr], [] as andQuery[]),
      map(queries => new PlatformTypeQuery(undefined, queries)),
      debounceTime(1000),
      switchMap(query => this.queryService.queryExact(query as MimQuery<PlatformType>)),
      switchMap(results => iif(() => results.length > 0,
        of(true),
        of(false)
      ))
    ))
  );

  @Output() attributesUnique = combineLatest([this._attributesUnique, this.nameUnique]).pipe(
    switchMap(([attributes,name])=>iif(()=>name || attributes,of('Either the Name is not unique or the type already exists - please use types search page'),of("")))
  )


  @Output() stepComplete = combineLatest([this.formInfo, this._formValues]).pipe(
    switchMap(([formInfo, values]) => iif(() => values.size >= formInfo.fields.filter(v => v.required).length,
      of(values).pipe(
        //turn values.keys() from iterator to array and compare to formInfo.fields.filter(v=>v.attributeTypeId)
        concatMap(values => from(values.keys()).pipe(
          
        )),
        take(values.size),
        reduce((acc, curr) => [...acc, curr], [] as string[]),
        map(keys => keys.every((value, index) => formInfo.fields.map(v => v.attributeTypeId).includes(value)))
      ), //path: step could be complete
      of(false) //not possible for step to be complete
    ))
  );
  @Output("attributes") private _nameToValueMap = new BehaviorSubject<Map<string, string>>(new Map());

  @Output("fields") private _attrnameToLogicalTypeFields = new BehaviorSubject<Map<string, logicalTypefieldValue>>(new Map());
  constructor (private typesService: TypesService, private constantEnumService: EnumsService, private queryService: CurrentQueryService) { }
  ngOnChanges(changes: SimpleChanges): void {
    this._logicalType.next(this.logicalType);
  }

  ngOnInit(): void {
    this._logicalType.next(this.logicalType);
  }

  /**really messy, need to find a better way to handle this (obj compare?) */
  updatedFormValue(event: logicalTypeFieldInfo, index: number) {
    const currentValue = this._formValues.getValue();
    currentValue.set(event.attributeTypeId, event.value || '');
    this._formValues.next(currentValue);
    const currentNames = this._nameToValueMap.getValue();
    currentNames.set(event.name, event.value || '');
    this._nameToValueMap.next(currentNames);
    const currentAttr = this._attrnameToLogicalTypeFields.getValue();
    currentAttr.set(event.attributeType, {name:event.attributeType,value:event.value||''});
    this._attrnameToLogicalTypeFields.next(currentAttr);
  }
}
