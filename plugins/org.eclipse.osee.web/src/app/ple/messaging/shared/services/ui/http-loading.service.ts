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
export class HttpLoadingService {

  private _isLoading: BehaviorSubject<String> = new BehaviorSubject<String>("true");
  constructor () { }
  
  get isLoading() {
    return this._isLoading;
  }

  set loading(value: boolean) {
    this._isLoading.next(value.toString());
  }

}
