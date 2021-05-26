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
