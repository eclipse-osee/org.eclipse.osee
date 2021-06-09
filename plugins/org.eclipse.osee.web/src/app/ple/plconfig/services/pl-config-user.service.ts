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
