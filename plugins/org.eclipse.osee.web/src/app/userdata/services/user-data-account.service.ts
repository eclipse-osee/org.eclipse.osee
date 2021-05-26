import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { user } from '../types/user-data-user';

@Injectable({
  providedIn: 'root'
})
export class UserDataAccountService {

  constructor(private http: HttpClient) { }
  public getUser():Observable<user> {
    return this.http.get<user>(apiURL+'/accounts/user');
  }
}
