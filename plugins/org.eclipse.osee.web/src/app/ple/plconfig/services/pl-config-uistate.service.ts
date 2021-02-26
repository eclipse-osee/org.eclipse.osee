import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { share } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PlConfigUIStateService {
  private _viewBranchType = new BehaviorSubject<string>("");
  private _branchId = new BehaviorSubject<string>("");
  private _deleteRequired = new Subject<string>();
  private _updateRequired = new Subject<boolean>();
  private _loading = new BehaviorSubject<string>("false");
  private _editable = new BehaviorSubject<string>("false");
  private _errors = new BehaviorSubject<string>("");
  private _groups = new BehaviorSubject<string[]>([]);
  constructor() { }


  public set viewBranchTypeString(branchType: string) {
    this._viewBranchType.next(branchType?.toLowerCase()); 
    this.updateReqConfig = true;
  }

  public get viewBranchType() {
    return this._viewBranchType;
  }

  public set branchIdNum(branchId: string) {
    this._branchId.next(branchId);
  }
  public get branchId() {
    return this._branchId.pipe(share());
  }
  public set deleteReqConfig(deleteReq:string) {
    this._deleteRequired.next(deleteReq);
  }
  public get deleteReq() {
    return this._deleteRequired;
  }
  public set updateReqConfig(updateReq: boolean) {
    this._updateRequired.next(updateReq);
  }
  public get updateReq() {
    return this._updateRequired;
  }
  public get loading() {
    return this._loading;
  }
  public set loadingBool(loading: boolean) {
    this._loading.next(loading.toString());
  }
  public get editable() {
    return this._editable;
  }
  public set editableString(edit: string) {
    this._editable.next(edit);
  }
  public set editableBool(edit: boolean) {
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
