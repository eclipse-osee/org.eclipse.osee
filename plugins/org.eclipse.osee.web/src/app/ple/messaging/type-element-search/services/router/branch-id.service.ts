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
