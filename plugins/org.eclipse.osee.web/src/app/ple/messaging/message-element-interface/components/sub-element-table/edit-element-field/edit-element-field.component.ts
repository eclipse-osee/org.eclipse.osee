import { Component, Input, OnInit } from '@angular/core';
import { combineLatest } from 'rxjs';
import { BehaviorSubject, from, Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, map, tap, switchMap, filter, scan } from 'rxjs/operators';
import { CurrentStateService } from '../../../services/current-state.service';
import { element } from '../../../types/element';
import { PlatformType } from '../../../types/platformtype';

@Component({
  selector: 'osee-messaging-edit-element-field',
  templateUrl: './edit-element-field.component.html',
  styleUrls: ['./edit-element-field.component.sass']
})
export class EditElementFieldComponent implements OnInit {
  availableTypes = this.structureService.types;
  @Input() structureId: string = '';
  @Input() elementId: string = '';
  @Input() header: string = '';
  @Input() value: any = '';
  @Input() elementStart: number =0;
  @Input() elementEnd: number = 0;
  private _value: Subject<string> = new Subject();
  _element: Partial<element> = {
    id:this.elementId
  };
  private _typeValue: BehaviorSubject<any> = new BehaviorSubject(this.value);
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x: any) => this._element[this.header as keyof element] = x),
    tap(() => {
      this._element.id = this.elementId;
    }),
    switchMap(val=>this.structureService.partialUpdateElement(this._element,this.structureId))
  )
  filteredTypes = combineLatest(this._typeValue,this.availableTypes).pipe(
    switchMap(val => from(val[1]).pipe(
      filter((val: PlatformType) => val.name.toLowerCase().includes(this.value.toLowerCase())),
      scan((acc, curr) => [...acc, curr], [] as PlatformType[]),
    )),
  )
  private _type: Subject<string> = new Subject();
  private _sendType = this._type.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(val=>this.structureService.changeElementPlatformType(this.structureId,this.elementId,val))
  )

  applics = this.structureService.applic;
  constructor (private structureService: CurrentStateService) {
    this._sendValue.subscribe();
    this._sendType.subscribe();
   }

  ngOnInit(): void {
  }

  updateElement(header: string, value: string) {
    this._value.next(value);
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
}