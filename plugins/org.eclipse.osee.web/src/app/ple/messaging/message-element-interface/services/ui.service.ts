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
import { UiService } from 'src/app/ple-services/ui/ui.service';

@Injectable({
  providedIn: 'root'
})
export class ElementUiService {

  private _filter: BehaviorSubject<string> = new BehaviorSubject<string>("");

  private _messageId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
  private _subMessageId: BehaviorSubject<string> = new BehaviorSubject<string>("0");
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

  set BranchIdString(value: string) {
    this.ui.idValue = value;
  }

  get messageId() {
    return this._messageId;
  }

  set messageIdString(value: string) {
    this._messageId.next(value);
  }

  get subMessageId() {
    return this._subMessageId;
  }

  set subMessageIdString(value: string) {
    this._subMessageId.next(value);
  }

  get connectionId() {
    return this._connectionId;
  }

  set connectionIdString(value: string) {
    this._connectionId.next(value);
  }

  get branchType() {
    return this.ui.type;
  }

  set BranchType(value:string) {
    this.ui.typeValue = value;
  }
}
