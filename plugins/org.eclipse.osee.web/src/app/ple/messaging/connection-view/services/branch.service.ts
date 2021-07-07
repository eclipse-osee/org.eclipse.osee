import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiURL } from 'src/environments/environment';
import { messageBranch } from '../types/branches';

@Injectable({
  providedIn: 'root'
})
export class BranchService {

  constructor (private http: HttpClient) { }
  
  public getBranches(type: string): Observable<messageBranch[]> {
    return this.http.get<messageBranch[]>(apiURL+'/orcs/branches/'+type);
  }
}
