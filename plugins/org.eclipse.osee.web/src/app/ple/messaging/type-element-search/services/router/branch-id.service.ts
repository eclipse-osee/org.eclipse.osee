import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BranchIdService {
  private _branchId: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor () { }
  
  get BranchId() {
    return this._branchId;
  }

  set id(value: string) {
    if (value != '0'&& value !='-1'&& Number(value)>0 && !isNaN(Number(value))) {
      this._branchId.next(value); 
    } else {
      throw new Error('Id is not a valid value. Invalid Value:'+value+' Valid values: ID>0');
    }
  }

  get id() {
    return this._branchId.getValue();
  }
}
