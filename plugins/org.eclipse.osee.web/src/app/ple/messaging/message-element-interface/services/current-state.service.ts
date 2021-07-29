import { Injectable } from '@angular/core';
import { combineLatest, from, Observable, of } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, switchMap, repeatWhen, mergeMap, scan, distinct, tap, shareReplay, first } from 'rxjs/operators';
import { element } from '../types/element';
import { structure } from '../types/structure';
import { ElementService } from './element.service';
import { MessagesService } from './messages.service';
import { PlatformTypeService } from './platform-type.service';
import { StructuresService } from './structures.service';
import { UiService } from './ui.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentStateService {

  private _structures = combineLatest(this.ui.filter, this.ui.BranchId, this.ui.messageId, this.ui.subMessageId,this.ui.connectionId).pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    switchMap(x => this.structure.getFilteredStructures(...x).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )

  //private _types = this.typeService.getTypes(this.BranchId.getValue());
  private _types = this.ui.BranchId.pipe(
    share(),
    switchMap(x => this.typeService.getTypes(x).pipe(
      repeatWhen(_ => this.ui.UpdateRequired),
      share(),
      shareReplay(1),
    )),
    shareReplay(1),
  )

  constructor (private ui: UiService, private structure: StructuresService, private messages:MessagesService, private elements:ElementService, private typeService: PlatformTypeService) { }
  
  get structures() {
    return this._structures;
  }

  set filter(value: string) {
    this.ui.filterString = value;
  }

  set branchId(value: string) {
    this.ui.BranchIdString = value;
  }

  get BranchId() {
    return this.ui.BranchId;
  }

  set messageId(value: string) {
    this.ui.messageIdString = value;
  }

  get MessageId() {
    return this.ui.messageId;
  }

  get SubMessageId() {
    return this.ui.subMessageId
  }

  set subMessageId(value: string) {
    this.ui.subMessageIdString = value;
  }

  set connection(id: string) {
    this.ui.connectionIdString = id;
  }

  get connectionId() {
    return this.ui.connectionId;
  }

  private get structureObservable(){
    return this.messages.getMessages(this.BranchId.getValue(),this.connectionId.getValue()).pipe(
      mergeMap(messages => from(messages).pipe(
        mergeMap(message => of(message?.subMessages).pipe(
          mergeMap(submessage => from(submessage).pipe(
            distinct((x) => { return x.id }),
            mergeMap((submessage) => this.structure.getFilteredStructures("", this.BranchId.getValue(), message?.id, submessage?.id,this.connectionId.getValue()).pipe(
              mergeMap(structures => from(structures).pipe(
                distinct((structure)=>{return structure.id})
              ))
            )),
          )),
        )),
      )),
    )
  }
  get availableStructures(): Observable<structure[]> {
    return this.structureObservable.pipe(
      scan((acc, curr) => [...acc, curr], [] as structure[]),
    )
  }

  get availableElements(): Observable<element[]>{
    return this.structureObservable.pipe(
      mergeMap((value) => from(value.elements).pipe(
        distinct()
      )),
      scan((acc, curr) => [...acc, curr], [] as element[]),
    )
  }

  get types() {
    return this._types;
  }

  createStructure(body:Partial<structure>) {
    return this.structure.createStructure(body, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(),this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  relateStructure(structureId:string) {
    return this.structure.relateStructure(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }
  partialUpdateStructure(body:Partial<structure>) {
    return this.structure.partialUpdateStructure(body, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(),this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    );
  }

  partialUpdateElement(body: Partial<element>, structureId: string) {
    return this.elements.partialUpdateElement(body, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    )
  }

  createNewElement(body: Partial<element>, structureId:string, typeId: string) {
    return this.elements.createNewElement(body, this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId,this.connectionId.getValue()).pipe(
      switchMap((val) => this.changeElementPlatformType(structureId, val.ids[0], typeId)),
      first()
    )
  }
  relateElement(structureId: string, elementId: string) {
    return this.elements.relateElement(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId,elementId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    )
  }
  changeElementPlatformType(structureId:string,elementId:string,typeId:string) {
    return this.elements.relateElementToPlatformType(this.BranchId.getValue(), this.MessageId.getValue(), this.SubMessageId.getValue(), structureId, elementId, typeId,this.connectionId.getValue()).pipe(
      tap(() => {
        this.ui.updateMessages = true;
      })
    )
  }
}
