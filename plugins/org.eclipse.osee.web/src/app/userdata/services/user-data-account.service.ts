import { HttpClient } from '@angular/common/http';
import { jitOnlyGuardedExpression } from '@angular/compiler/src/render3/util';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { apiURL, environment } from 'src/environments/environment';
import { user } from '../types/user-data-user';

@Injectable({
  providedIn: 'root'
})
export class UserDataAccountService {

  constructor(private http: HttpClient) { }
  public getUser():Observable<user> {
    if (environment.production) {
      return this.http.get<user>(apiURL+'/accounts/user');
    } else {
      return of({id : '61106791',
      name : 'Joe Smith',
      guid : null,
      active : false,
      description : null,
      workTypes : [],
      tags : [],
      userId : '61106791',
      email : '',
      loginIds : [],
      savedSearches : [],
      userGroups : [],
      artifactId : '',
      idString : '',
      idIntValue : 0,
      uuid : 0 })
    }
  }
}
