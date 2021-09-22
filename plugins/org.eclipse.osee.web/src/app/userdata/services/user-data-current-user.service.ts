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
import { from, iif, zip,Observable, of } from 'rxjs';
import { switchMap, repeatWhen, share, mergeMap,filter, tap, finalize, take } from 'rxjs/operators';
import { user } from '../types/user-data-user';
import { UserDataAccountService } from './user-data-account.service';
import { UserDataUIStateService } from './user-data-uistate.service';

@Injectable({
  providedIn: 'root'
})
export class UserDataCurrentUserService {

  private _userDisplayable: Observable<user>=this.uiStateService.userId.pipe(
    switchMap(val => iif(() => val !== '',
    this.accountService.getUser()
    )),
    share()
  )

  constructor(private uiStateService: UserDataUIStateService, private accountService: UserDataAccountService) {
   }
  public get userDisplayable() {
    return this._userDisplayable;
  }
}