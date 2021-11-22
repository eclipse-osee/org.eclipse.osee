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
import { ReplaySubject, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { transportType } from 'src/app/ple/messaging/shared/types/connection';
import { transactionToken } from 'src/app/transactions/transaction';
import { applic } from 'src/app/types/applicability/applic';

@Injectable({
  providedIn: 'root'
})
export class SideNavService {

  private _sideNavContent = new ReplaySubject<{opened:boolean, field:string, currentValue:string|number|applic|transportType|boolean, previousValue?:string|number|applic|transportType|boolean,transaction?:transactionToken,user?:string,date?:string}>();
  constructor () { }
  
  get sideNavContent() {
    return this._sideNavContent;
  }
  set sideNav(value:{opened:boolean, field:string, currentValue:string|number|applic|transportType|boolean, previousValue?:string|number|applic|transportType|boolean,transaction?:transactionToken,user?:string,date?:string}) {
    this._sideNavContent.next(value);
  }

  get opened() {
    return this.sideNavContent.pipe(
      map((val)=>val.opened)
    )
  }
}
