import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UiService {

  private _filter: BehaviorSubject<string> = new BehaviorSubject<string>("");

  private _UpdateRequired: Subject<boolean> = new Subject<boolean>();
  private _branchId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
  private _connectionId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
  constructor() { }

  get filter() {
    return this._filter
  }

  set filterString(filter: string) {
    if (filter !== this._filter.getValue()) {
      this._filter.next(filter); 
    }
  }

  get UpdateRequired() {
    return this._UpdateRequired;
  }

  set updateMessages(value: boolean) {
    this._UpdateRequired.next(value);
  }

  get BranchId() {
    return this._branchId;
  }

  set BranchIdString(value: string) {
    this._branchId.next(value);
  }

  get connectionId() {
    return this._connectionId;
  }

  set connectionIdString(value: string) {
    this._connectionId.next(value);
  }
}
