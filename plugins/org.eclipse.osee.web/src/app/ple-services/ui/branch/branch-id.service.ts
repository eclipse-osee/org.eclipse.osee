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

  private _branchId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
  id = this._branchId.asObservable();
  constructor () { }
  get BranchId() {
    return this._branchId;
  }
  /**
   * @deprecated will be replacing functionality of BranchId with BranchIdAsObservable()
   */
  get BranchIdAsObservable() {
    return this.id;
  }

  set BranchIdValue(value: string|number) {
    this._branchId.next(value.toString());
  }
}
