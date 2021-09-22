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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { user } from 'src/app/userdata/types/user-data-user';
import { PlConfigActionService } from './pl-config-action.service';

@Injectable({
  providedIn: 'root'
})
export class PlConfigUserService {

  constructor(private actionService: PlConfigActionService) { }
  private _getSortedUsers = this.actionService.users.pipe(map(results => results.sort((a,b) => { return a.name < b.name ? -1 : 1})));
  public get usersSorted():Observable<user[]> {
    return this._getSortedUsers;
  }

}
