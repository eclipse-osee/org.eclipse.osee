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
import { UiService } from 'src/app/ple-services/ui/ui.service';

@Injectable({
  providedIn: 'root'
})
export class PlConfigUIStateService {
  // private _viewBranchType = new BehaviorSubject<string>("");
  // private _branchId = new BehaviorSubject<string>("");
  // private _deleteRequired = new Subject<string>();
  // private _updateRequired = new Subject<boolean>();
  private _loading = new BehaviorSubject<string>("false");
  private _editable = new BehaviorSubject<string>("false");
  private _errors = new BehaviorSubject<string>("");
  private _groups = new BehaviorSubject<string[]>([]);
  constructor(private ui: UiService) { }


  public set viewBranchTypeString(branchType: string) {
    //this._viewBranchType.next(branchType?.toLowerCase());
    this.ui.typeValue = branchType?.toLowerCase();
    this.updateReqConfig = true;
  }

  public get viewBranchType() {
    //return this._viewBranchType;
    return this.ui.type;
  }

  public set branchIdNum(branchId: string) {
    //this._branchId.next(branchId);
    this.ui.idValue = branchId;
  }
  public get branchId() {
    return this.ui.id;
    // return this._branchId.pipe(share());
  }
  public set updateReqConfig(updateReq: boolean) {
    this.ui.updated = updateReq;
    //this._updateRequired.next(updateReq);
  }
  public get updateReq() {
    return this.ui.update;
    //return this._updateRequired;
  }
  public get loading() {
    return this._loading;
  }
  public set loadingValue(loading: boolean|string) {
    this._loading.next(loading.toString());
  }
  public get editable() {
    return this._editable;
  }
  public set editableValue(edit: boolean |string) {
    this.editable.next(edit.toString())
  }
  public get errors() {
    return this._errors;
  }
  public set error(errorString: string) {
    this._errors.next(errorString);
  }
  public set groupsString(groups: string[]) {
    this._groups.next(groups);
  }
  public get groups() {
    return this._groups;
  }
}
