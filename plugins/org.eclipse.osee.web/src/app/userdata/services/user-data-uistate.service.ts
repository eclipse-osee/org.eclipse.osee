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
import { BehaviorSubject, Subject } from 'rxjs';
import { share } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserDataUIStateService {
	private _userId = new BehaviorSubject<string>("");
	private _userName = new BehaviorSubject<string>("");
	private _userGroups = new BehaviorSubject<string[]>([]);
  constructor() { }

  public set userIdNum(userId: string) {
    this._userId.next(userId);
  }
  public get userId() {
    return this._userId.pipe(share());
  }
  public set userNameString(userName: string) {
    this._userName.next(userName);
  }
  public get userName() {
    return this._userName;
  }
  public set userGroupsString(userGroups: string[]) {
    this._userGroups.next(userGroups);
  }
  public get userGroups() {
    return this._userGroups;
  }
}
