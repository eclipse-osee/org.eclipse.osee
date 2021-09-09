import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { user } from 'src/app/userdata/types/user-data-user';
import { apiURL } from 'src/environments/environment';
import { MimPreferences } from '../../types/mim.preferences';

@Injectable({
  providedIn: 'root'
})
export class MimPreferencesService {

  constructor (private http: HttpClient) { }
  
  getUserPrefs(branchId: string, user: user) {
    return this.http.get<MimPreferences>(apiURL + '/mim/user/' + branchId, {
      headers: new HttpHeaders({ 'osee.account.id': user.id })
    })
  }

  getBranchPrefs(user: user) {
    return this.http.get<string[]>(apiURL + '/mim/user/branches', {
      headers: new HttpHeaders({ 'osee.account.id': user.id })
    })
  }
}