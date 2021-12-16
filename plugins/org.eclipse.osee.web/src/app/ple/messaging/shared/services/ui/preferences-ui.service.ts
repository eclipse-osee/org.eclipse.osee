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
import { combineLatest, from } from 'rxjs';
import { share, filter, switchMap, repeatWhen, shareReplay, map, reduce } from 'rxjs/operators';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { MimPreferencesService } from '../http/mim-preferences.service';

@Injectable({
  providedIn: 'root'
})
export class PreferencesUIService {

  private _preferences = combineLatest([this.ui.id, this.userService.user]).pipe(
    share(),
    filter(([id, user]) => id !== "" && id !== '-1'),
    switchMap(([id, user]) => this.preferenceService.getUserPrefs(id, user).pipe(
      repeatWhen(_ => this.ui.update),
      share(),
      shareReplay({ bufferSize: 1, refCount: true })
    )),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  private _inEditMode = this.preferences.pipe(
    map((x) => x.inEditMode)
  );

  private _branchPrefs = combineLatest([this.ui.id, this.userService.user]).pipe(
    share(),
    switchMap(([branch, user]) => this.preferenceService.getBranchPrefs(user).pipe(
      repeatWhen(_ => this.ui.update),
      share(),
      switchMap((branchPrefs) => from(branchPrefs).pipe(
        filter((pref) => !pref.includes(branch + ":")),
        reduce((acc, curr) => [...acc, curr], [] as string[])
      )),
    )),
    shareReplay({ bufferSize: 1, refCount: true })
  );
  constructor (private ui: UiService, private userService: UserDataAccountService, private preferenceService: MimPreferencesService) { }
  
  public get preferences() {
    return this._preferences;
  }
  public get inEditMode() {
    return this._inEditMode;
  }
  public get BranchPrefs() {
    return this._branchPrefs;
  }
}
