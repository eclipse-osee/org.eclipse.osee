import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RouteStateService {

  private _branchType: BehaviorSubject<string> = new BehaviorSubject<string>("");
  private _branchId: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor () { }
  
  get type() {
    return this._branchType
  }

  get id() {
    return this._branchId;
  }

  set branchType(value: string) {
    this._branchType.next(value);
  }

  set branchId(value: string) {
    this._branchId.next(value);
  }
}
