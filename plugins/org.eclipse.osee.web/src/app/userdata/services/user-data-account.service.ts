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
import { HttpClient } from '@angular/common/http';
import { jitOnlyGuardedExpression } from '@angular/compiler/src/render3/util';
import { Injectable } from '@angular/core';
import { iif, Observable, of } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { apiURL, environment, OSEEAuthURL } from 'src/environments/environment';
import { user } from '../types/user-data-user';

@Injectable({
  providedIn: 'root',
})
export class UserDataAccountService {
  constructor(private http: HttpClient) {}
  private _user = iif(
    () => environment.production,
    this.http.get<user>(OSEEAuthURL),
    of({
      id: '61106791',
      name: 'Joe Smith',
      guid: null,
      active: false,
      description: null,
      workTypes: [],
      tags: [],
      userId: '61106791',
      email: '',
      loginIds: [],
      savedSearches: [],
      userGroups: [],
      artifactId: '',
      idString: '',
      idIntValue: 0,
      uuid: 0,
    })
  ).pipe(
    shareReplay({bufferSize:1,refCount:true})
  );
  public get user(): Observable<user> {
    return this._user;
  }
}
