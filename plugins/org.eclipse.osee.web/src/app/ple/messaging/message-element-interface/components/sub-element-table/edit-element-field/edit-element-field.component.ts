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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, combineLatest, from, iif, of, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, scan, share, switchMap, tap } from 'rxjs/operators';
import { CurrentStateService } from '../../../services/current-state.service';
import { element } from '../../../types/element';
import { PlatformType } from '../../../types/platformtype';

@Component({
  selector: 'osee-messaging-edit-element-field',
  templateUrl: './edit-element-field.component.html',
  styleUrls: ['./edit-element-field.component.sass']
})
export class EditElementFieldComponent<T extends keyof element=any> implements OnInit {
  availableTypes = this.structureService.types;
  @Input() structureId: string = '';
  @Input() elementId: string = '';
  @Input() header: (T & string) | '' = '';
  @Input() value: T = {} as T;
  @Input() elementStart: number =0;
  @Input() elementEnd: number = 0;
  @Input() editingDisabled: boolean = false;

  @Input() platformTypeId: string = "";
  @Output() contextMenu = new EventEmitter<MouseEvent>();
  private _value: Subject<T> = new Subject();
  private _immediateValue: Subject<T> = new Subject();
  private _units: Subject<string> = new Subject();
  _element: Partial<element> = {
    id:this.elementId
  };
  _location = combineLatest([this.structureService.branchType, this.structureService.BranchId]).pipe(
    switchMap(([type, id]) => of({ type: type, id: id }))
  )
  private _typeValue: BehaviorSubject<T> = new BehaviorSubject(this.value);
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x) => this._element[this.header] = x),
    tap(() => {
      this._element.id = this.elementId;
    }),
  )
  private _updateUnits = this._units.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap((unit)=>this.structureService.updatePlatformTypeValue({id:this.platformTypeId,interfacePlatformTypeUnits:unit}))
  )
  private _immediateUpdateValue=this._immediateValue.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x) => this._element[this.header] = x),
    tap(() => {
      this._element.id = this.elementId;
    }),
    switchMap(()=>this.structureService.partialUpdateElement(this._element,this.structureId))
  )
  private _focus = new Subject<string | null>();
  private _updateValue = combineLatest([this._sendValue, this._focus]).pipe(
    scan((acc, curr) => { if (acc.type === curr[1]) { acc.count++ } else { acc.count = 0; acc.type = curr[1] } acc.value = curr[0];return acc; }, { count: 0, type: '',value:undefined } as { count: number, type: string | null,value:T|undefined }),
    switchMap((update) => iif(() => update.type === null, of(true).pipe(
      switchMap(()=>this.structureService.partialUpdateElement(this._element,this.structureId))
    ), of(false))),
  )
  filteredTypes = combineLatest(this._typeValue,this.availableTypes).pipe(
    switchMap(val => from(val[1]).pipe(
      filter((val: PlatformType) => val.name.toLowerCase().includes(this.isString(this.value)?this.value.toLowerCase():this.value as unknown as string)),
      scan((acc, curr) => [...acc, curr], [] as PlatformType[]),
    )),
  )
  private _type: Subject<string> = new Subject();
  private _sendType = this._type.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap((value)=>this.structureService.changeElementPlatformType(this.structureId,this.elementId,value))
  )

  applics = this.structureService.applic;
  units = this.structureService.units;
  menuPosition = {
    x: '0',
    y:'0'
  }
  constructor (private structureService: CurrentStateService,private route: ActivatedRoute) {
    this._updateValue.subscribe();
    this._immediateUpdateValue.subscribe();
    this._sendType.subscribe();
    this._updateUnits.subscribe();
   }

  ngOnInit(): void {
  }

  updateElement(header: string, value: T) {
    this._value.next(value);
  }
  updateImmediately(header: string, value: T) {
    this._immediateValue.next(value);
  }
  updateType(value: string) {
    this._type.next(value);
  }

  updateTypeAhead(value: any) {
    this._typeValue.next(value);
  }

  compareApplics(o1:any,o2:any) {
    return o1.id===o2.id && o1.name===o2.name
  }

  openMenu(event: MouseEvent,location: T) {
    event.preventDefault();
    this.contextMenu.emit(event);
  }
  isString(val: T|string):val is string{
    return typeof val === 'string' || val instanceof String
  }
  focusChanged(event: string|null) {
    this._focus.next(event);
  }
  updateUnits(event: string) {
    this._units.next(event);
  }
}
