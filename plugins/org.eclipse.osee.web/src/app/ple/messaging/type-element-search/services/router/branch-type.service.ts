import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {branchType} from '../../types/BranchTypes'


@Injectable({
  providedIn: 'root'
})
export class BranchTypeService {
  private _branchType: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor () { }
  
  get BranchType() {
    return this._branchType
  }

  set type(value: branchType) {
    if (value === 'working') {
      this._branchType.next(value); 
    } else if (value === 'product line') {
      this._branchType.next('baseline')
    }
    else {
      throw new Error('Type is not a valid value. Invalid Value:'+value+' Valid values: product line,working');
    }
  }

  get type() {
    return this._branchType.getValue() as branchType;
  }
}
