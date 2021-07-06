import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { BranchListing } from '../../types/BranchListing';

@Injectable({
  providedIn: 'root'
})
export class BranchService {

  constructor (private http: HttpClient) { }
  
  getBranches(type:string) {
    return this.http.get<BranchListing[]>(apiURL+'/ats/ple/branches/'+type);
  }
}
