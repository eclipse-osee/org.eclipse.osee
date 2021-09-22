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
export class SearchService {

  private _searchTerm: BehaviorSubject<string> = new BehaviorSubject<string>("");
  constructor () { }
  
  get searchTerm() {
    return this._searchTerm;
  }

  set search(value: string) {
    this._searchTerm.next(value)
  }

  get search() {
    return this._searchTerm.getValue();
  }
}
