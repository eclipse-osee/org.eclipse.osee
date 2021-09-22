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
