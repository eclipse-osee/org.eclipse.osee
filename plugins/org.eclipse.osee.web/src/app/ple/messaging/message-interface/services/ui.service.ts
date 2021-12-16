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
import { UiService } from 'src/app/ple-services/ui/ui.service';

@Injectable({
  providedIn: 'root'
})
export class MessageUiService {

  private _filter: BehaviorSubject<string> = new BehaviorSubject<string>("");
  private _connectionId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
  constructor(private ui: UiService) { }

  get filter() {
    return this._filter
  }

  set filterString(filter: string) {
    if (filter !== this._filter.getValue()) {
      this._filter.next(filter); 
    }
  }

  get UpdateRequired() {
    return this.ui.update;
  }

  set updateMessages(value: boolean) {
    this.ui.updated = value;
  }

  get BranchId() {
    return this.ui.id;
  }

  get type() {
    return this.ui.type;
  }

  set BranchIdString(value: string) {
    this.ui.idValue = value;
  }

  get connectionId() {
    return this._connectionId;
  }

  set connectionIdString(value: string) {
    this._connectionId.next(value);
  }

  set DiffMode(value:boolean) {
    this.ui.diffMode = value;
  }

  get isInDiff() {
    return this.ui.isInDiff;
  }
}
